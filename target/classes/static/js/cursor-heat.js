class CursorHeat {
    constructor(apiKey, projectId, options = {}) {
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.options = {
            batchSize: options.batchSize || 50,
            batchInterval: options.batchInterval || 5000,
            endpoint: options.endpoint || 'http://localhost:8080/api/v1',
            heatmapOptions: {
                radius: options.radius || 20,
                maxOpacity: options.maxOpacity || 0.5,
                minOpacity: options.minOpacity || 0,
                blur: options.blur || 0.75,
                ...options.heatmapOptions
            },
            ...options
        };
        
        this.events = [];
        this.sessionId = null;
        this.isTracking = false;
        this.heatmap = null;
        this.retryCount = 0;
        this.maxRetries = 3;
        
        this.init();
    }

    async init() {
        try {
            await this.createSession();
            this.startTracking();
            this.setupBatchProcessing();
            this.setupErrorHandling();
            this.setupHeatmap();
        } catch (error) {
            console.error('Failed to initialize CursorHeat:', error);
            this.handleError(error);
        }
    }

    async createSession() {
        const response = await fetch(`${this.options.endpoint}/sessions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-API-Key': this.apiKey
            },
            body: JSON.stringify({
                projectId: this.projectId,
                pageUrl: window.location.href,
                viewport: {
                    width: window.innerWidth,
                    height: window.innerHeight
                },
                userAgent: navigator.userAgent
            })
        });
        
        if (!response.ok) {
            throw new Error(`Failed to create session: ${response.statusText}`);
        }
        
        const session = await response.json();
        this.sessionId = session.id;
    }

    startTracking() {
        if (this.isTracking) return;
        
        // Track mouse movements with throttling
        let lastMoveTime = 0;
        const moveThrottle = 50; // ms
        
        document.addEventListener('mousemove', (e) => {
            const now = Date.now();
            if (now - lastMoveTime >= moveThrottle) {
                this.trackEvent('MOVE', e);
                lastMoveTime = now;
            }
        });
        
        // Track clicks with element information
        document.addEventListener('click', (e) => {
            this.trackEvent('CLICK', e);
            this.updateHeatmap(e.clientX, e.clientY, 1);
        });
        
        // Track scrolls with position
        let lastScrollTime = 0;
        const scrollThrottle = 100; // ms
        
        document.addEventListener('scroll', (e) => {
            const now = Date.now();
            if (now - lastScrollTime >= scrollThrottle) {
                this.trackEvent('SCROLL', e);
                lastScrollTime = now;
            }
        });
        
        // Track form interactions
        document.addEventListener('focus', (e) => {
            if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') {
                this.trackEvent('FOCUS', e);
            }
        }, true);
        
        // Track page visibility
        document.addEventListener('visibilitychange', () => {
            this.trackEvent('VISIBILITY', { type: 'visibilitychange', target: document });
        });
        
        this.isTracking = true;
    }

    setupBatchProcessing() {
        // Process batches at regular intervals
        setInterval(() => this.processBatch(), this.options.batchInterval);
        
        // Handle page unload
        window.addEventListener('beforeunload', () => this.processBatch(true));
        
        // Handle page visibility changes
        document.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'hidden') {
                this.processBatch(true);
            }
        });
    }

    setupErrorHandling() {
        window.addEventListener('error', (e) => {
            this.trackEvent('ERROR', {
                type: 'error',
                message: e.message,
                filename: e.filename,
                lineno: e.lineno,
                colno: e.colno
            });
        });
        
        window.addEventListener('unhandledrejection', (e) => {
            this.trackEvent('ERROR', {
                type: 'unhandledrejection',
                reason: e.reason
            });
        });
    }

    setupHeatmap() {
        if (typeof h337 !== 'undefined') {
            this.heatmap = h337.create({
                container: document.body,
                ...this.options.heatmapOptions
            });
        }
    }

    trackEvent(type, event) {
        const target = event.target;
        const rect = target.getBoundingClientRect();
        
        const eventData = {
            type,
            x: event.clientX,
            y: event.clientY,
            timestamp: new Date().toISOString(),
            elementId: target.id,
            elementClass: target.className,
            elementTag: target.tagName,
            viewport: {
                width: window.innerWidth,
                height: window.innerHeight
            },
            scroll: {
                x: window.scrollX,
                y: window.scrollY
            }
        };

        // Add additional data based on event type
        if (type === 'CLICK') {
            eventData.clickCount = event.detail;
            eventData.button = event.button;
        } else if (type === 'SCROLL') {
            eventData.scrollDelta = {
                x: window.scrollX - (this.lastScrollX || 0),
                y: window.scrollY - (this.lastScrollY || 0)
            };
            this.lastScrollX = window.scrollX;
            this.lastScrollY = window.scrollY;
        }
        
        this.events.push(eventData);

        if (this.events.length >= this.options.batchSize) {
            this.processBatch();
        }
    }

    updateHeatmap(x, y, intensity = 1) {
        if (this.heatmap) {
            this.heatmap.addData({
                x: x,
                y: y,
                value: intensity
            });
        }
    }

    async processBatch(force = false) {
        if (this.events.length === 0 || (!force && this.events.length < this.options.batchSize)) {
            return;
        }

        const eventsToSend = [...this.events];
        this.events = [];

        try {
            const response = await fetch(`${this.options.endpoint}/events/${this.sessionId}/batch`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-API-Key': this.apiKey
                },
                body: JSON.stringify(eventsToSend)
            });

            if (!response.ok) {
                throw new Error(`Failed to send events: ${response.statusText}`);
            }

            this.retryCount = 0;
        } catch (error) {
            console.error('Failed to send events:', error);
            this.handleError(error, eventsToSend);
        }
    }

    handleError(error, events = null) {
        if (events) {
            // Put events back in the queue
            this.events = [...events, ...this.events];
        }

        if (this.retryCount < this.maxRetries) {
            this.retryCount++;
            setTimeout(() => this.processBatch(true), 1000 * this.retryCount);
        } else {
            console.error('Max retries reached, events will be lost');
            this.retryCount = 0;
        }
    }

    stopTracking() {
        if (!this.isTracking) return;
        
        document.removeEventListener('mousemove', this.trackEvent);
        document.removeEventListener('click', this.trackEvent);
        document.removeEventListener('scroll', this.trackEvent);
        document.removeEventListener('focus', this.trackEvent);
        document.removeEventListener('visibilitychange', this.trackEvent);
        
        this.isTracking = false;
    }

    // Public methods for external control
    getSessionId() {
        return this.sessionId;
    }

    getEventCount() {
        return this.events.length;
    }

    clearHeatmap() {
        if (this.heatmap) {
            this.heatmap.setData({
                max: 1,
                data: []
            });
        }
    }
}

// Usage example:
// const tracker = new CursorHeat('your-api-key', 'your-project-id', {
//     batchSize: 50,
//     batchInterval: 5000,
//     heatmapOptions: {
//         radius: 20,
//         maxOpacity: 0.5,
//         minOpacity: 0,
//         blur: 0.75
//     }
// }); 