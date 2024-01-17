package ru.practicum.ewm.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comments.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "select c from  Comment c join fetch c.event e where e.state =?1 and e.id=?2 and c.id=?3")
    Optional<Comment> findByIdForEvent(String state, Long eventId, Long commentId);

    @Query(value = "select c from Comment c join fetch c.event e where e.state =?1 and e.id =?2")
    List<Comment> findAllByStateAndEventId(@Param(value = "state") String state, @Param(value = "eventId") Long eventId,
                                           PageRequest pageRequest);
}
