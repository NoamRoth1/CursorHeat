package org.cursorheat.repository;

import org.cursorheat.model.MouseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MouseEventRepository extends JpaRepository<MouseEvent, Long> {
    List<MouseEvent> findBySessionId(Long sessionId);
    List<MouseEvent> findBySessionIdOrderByTimestampAsc(Long sessionId);
} 