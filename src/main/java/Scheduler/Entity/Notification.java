package Scheduler.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 알림 받는 사람
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String message; // 표시할 알림 메시지

    @Column
    private Long targetId; // 클릭 시 이동에 필요한 대상 ID

    @Column(name = "is_read", nullable = false)
    private Boolean read = false; // 읽음 여부

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
