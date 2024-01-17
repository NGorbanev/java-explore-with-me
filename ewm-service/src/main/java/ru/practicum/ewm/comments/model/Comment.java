package ru.practicum.ewm.comments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Length(min = 2, max = 1000)
    @Column(name = "text", nullable = false)
    String text;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;
}
