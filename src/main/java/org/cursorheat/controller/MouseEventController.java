package org.cursorheat.controller;

import org.cursorheat.model.MouseEvent;
import org.cursorheat.model.Session;
import org.cursorheat.repository.MouseEventRepository;
import org.cursorheat.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class MouseEventController {

    private final MouseEventRepository mouseEventRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public MouseEventController(MouseEventRepository mouseEventRepository, SessionRepository sessionRepository) {
        this.mouseEventRepository = mouseEventRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/{sessionId}")
    public ResponseEntity<MouseEvent> recordMouseEvent(
            @PathVariable Long sessionId,
            @RequestBody MouseEvent event) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        event.setSession(session);
        return ResponseEntity.ok(mouseEventRepository.save(event));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<MouseEvent>> getSessionEvents(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(mouseEventRepository.findBySessionIdOrderByTimestampAsc(sessionId));
    }
} 