package org.cursorheat.controller;

import org.cursorheat.model.EventType;
import org.cursorheat.model.MouseEvent;
import org.cursorheat.model.Session;
import org.cursorheat.repository.MouseEventRepository;
import org.cursorheat.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private MouseEventRepository mouseEventRepository;

    @GetMapping("/sessions")
    public ResponseEntity<List<Map<String, ? extends Serializable>>> getSessions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Session> sessions = sessionRepository.findByStartTimeBetween(startTime, endTime);
        List<Map<String, ? extends Serializable>> response = sessions.stream()
                .map(session -> {
                    Map<String, Serializable> sessionMap = new HashMap<>();
                    sessionMap.put("id", session.getId());
                    sessionMap.put("startTime", session.getStartTime());
                    sessionMap.put("endTime", session.getEndTime());
                    sessionMap.put("pageUrl", session.getPageUrl());
                    sessionMap.put("eventCount", session.getEvents().size());
                    return sessionMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}/events")
    public ResponseEntity<List<Map<String, ? extends Serializable>>> getSessionEvents(@PathVariable Long sessionId) {
        List<MouseEvent> events = mouseEventRepository.findBySessionId(sessionId);
        List<Map<String, ? extends Serializable>> response = events.stream()
                .map(event -> {
                    Map<String, Serializable> eventMap = new HashMap<>();
                    eventMap.put("id", event.getId());
                    eventMap.put("x", event.getX());
                    eventMap.put("y", event.getY());
                    eventMap.put("type", event.getType().name());
                    eventMap.put("timestamp", event.getTimestamp());
                    eventMap.put("elementId", event.getElementId());
                    eventMap.put("elementClass", event.getElementClass());
                    eventMap.put("elementTag", event.getElementTag());
                    return eventMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<Map<String, ? extends Serializable>>> getHeatmapData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) EventType eventType) {
        List<Session> sessions = sessionRepository.findByStartTimeBetween(startTime, endTime);
        List<Map<String, ? extends Serializable>> response = sessions.stream()
                .flatMap(session -> session.getEvents().stream())
                .filter(event -> eventType == null || event.getType() == eventType)
                .map(event -> {
                    Map<String, Serializable> eventMap = new HashMap<>();
                    eventMap.put("x", event.getX());
                    eventMap.put("y", event.getY());
                    eventMap.put("type", event.getType().name());
                    eventMap.put("timestamp", event.getTimestamp());
                    return eventMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Session> sessions = sessionRepository.findByStartTimeBetween(startTime, endTime);
        long totalEvents = sessions.stream()
                .mapToLong(session -> session.getEvents().size())
                .sum();
        long clickEvents = sessions.stream()
                .flatMap(session -> session.getEvents().stream())
                .filter(event -> event.getType() == EventType.CLICK)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", sessions.size());
        stats.put("totalEvents", totalEvents);
        stats.put("totalClicks", clickEvents);
        stats.put("averageEventsPerSession", sessions.isEmpty() ? 0 : (double) totalEvents / sessions.size());

        return ResponseEntity.ok(stats);
    }
} 