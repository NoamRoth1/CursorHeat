package org.cursorheat.repository;

import org.cursorheat.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
} 