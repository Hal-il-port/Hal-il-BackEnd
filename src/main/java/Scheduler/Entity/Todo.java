package Scheduler.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content; // 할일 내용

    @Column(nullable = false)
    private LocalDate dueDate; // 마감 날짜 (MM/DD)

    @Enumerated(EnumType.STRING)
    private Status status; // IN_PROGRESS, DONE

    @Enumerated(EnumType.STRING)
    private TodoType type; // PERSONAL, TEAM

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 개인일정의 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team; // 팀일정 소속팀

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        IN_PROGRESS, DONE
    }

    public enum TodoType {
        PERSONAL, TEAM
    }
}
