package Scheduler.Service;

import Scheduler.Entity.FcmToken;
import Scheduler.Repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository tokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    // 토큰 저장/갱신
    public void saveToken(Long userId, String token) {
        tokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existing -> {
                            existing.setToken(token);
                            existing.setUpdatedAt(LocalDateTime.now());
                            tokenRepository.save(existing);
                        },
                        () -> tokenRepository.save(FcmToken.builder()
                                .userId(userId)
                                .token(token)
                                .build())
                );
    }

    // 단일 사용자 알림
    public void sendMessage(Long userId, String title, String body) {
        String token = tokenRepository.findByUserId(userId)
                .map(FcmToken::getToken)
                .orElse(null);

        if (token == null || token.isBlank()) {
            System.out.println("유저 " + userId + " 의 FCM 토큰이 없어 알림 전송을 건너뜀");
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(
                        com.google.firebase.messaging.Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("FCM 전송 실패", e);
        }
    }
}
