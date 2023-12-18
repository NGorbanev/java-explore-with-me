package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(" SELECT new ru.practicum.model.Stats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM Hit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "AND (eh.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC ")
    List<Stats> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);


    @Query(" SELECT new ru.practicum.model.Stats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM Hit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "AND (eh.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC ")
    List<Stats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);
}
