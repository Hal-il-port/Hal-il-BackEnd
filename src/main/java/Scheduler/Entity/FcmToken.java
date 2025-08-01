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
public class FcmToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private Long userId; //사용자 ID

    @Column(nullable = false)
    private String token;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
