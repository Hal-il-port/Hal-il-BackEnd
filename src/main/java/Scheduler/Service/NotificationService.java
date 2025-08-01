package Scheduler.Service;

import Scheduler.Entity.Notification;
import Scheduler.Entity.NotificationType;
import Scheduler.Entity.User;
import Scheduler.Repository.NotificationRepository;
import Scheduler.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    public void sendNotification(Long userId, NotificationType type, String message, Long targetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 1. DB 저장
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .targetId(targetId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // 2. FCM 푸시 전송
        fcmService.sendMessage(userId, type.name(), message);
    }

    public List<Notification> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 없음"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한 없음");
        }

        notification.setRead(true);
    }
}
