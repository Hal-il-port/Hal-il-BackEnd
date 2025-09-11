package Scheduler.Service;

import Scheduler.Dto.Friend.FriendCountResponseDto;
import Scheduler.Dto.Friend.FriendRequestDto;
import Scheduler.Dto.Friend.FriendResponseDto;
import Scheduler.Dto.Friend.TeamFriendInviteRequest;
import Scheduler.Entity.*;
import Scheduler.Repository.FriendRepository;
import Scheduler.Repository.TeamInvitationRepository;
import Scheduler.Repository.TeamRepository;
import Scheduler.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 친구 요청 보내기
    public void sendRequest(String fromEmail, FriendRequestDto request) {
        User fromUser = userRepository.findByEmail(fromEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보 없음"));
        User toUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("수신자 정보 없음"));

        if (friendRepository.findByFromUserAndToUser(fromUser, toUser).isPresent()) {
            throw new IllegalArgumentException("이미 요청 보냄");
        }

        Friend friend = Friend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .status(InvitationStatus.PENDING) // ✅ enum 사용
                .build();
        friendRepository.save(friend);

        notificationService.sendNotification(
                toUser.getId(),
                NotificationType.FRIEND_REQUEST,
                fromUser.getName() + "님이 친구 요청을 보냈습니다.",
                friend.getId()
        );
    }

    // 내가 받은 요청 (PENDING 상태만)
    public List<FriendResponseDto> getReceivedRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        return friendRepository.findByToUserAndStatus(user, InvitationStatus.PENDING).stream()
                .map(f -> new FriendResponseDto(f.getId(), f.getFromUser().getName(), f.getFromUser().getEmail()))
                .collect(Collectors.toList());
    }

    // 친구 요청 수락
    public void acceptRequest(String email, Long requestId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));

        if (!friend.getToUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("권한 없음");

        friend.setStatus(InvitationStatus.ACCEPTED);
        friendRepository.save(friend);

        notificationService.sendNotification(
                friend.getFromUser().getId(),
                NotificationType.FRIEND_ACCEPT,
                user.getName() + "님이 친구 요청을 수락했습니다.",
                null
        );
    }

    // 친구 요청 거절
    public void rejectRequest(String email, Long requestId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));

        if (!friend.getToUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("권한 없음");

        friend.setStatus(InvitationStatus.REJECTED);
        friendRepository.save(friend);

        notificationService.sendNotification(
                friend.getFromUser().getId(),
                NotificationType.FRIEND_REJECT,
                user.getName() + "님이 친구 요청을 거절했습니다.",
                null
        );
    }

    // 친구 삭제
    public void deleteFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        User target = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("상대 유저 정보 없음"));
        Friend relation = friendRepository.findFriendRelation(user, target)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아님"));
        friendRepository.delete(relation);
    }

    // 내 친구 목록 (ACCEPTED 상태만)
    public List<FriendResponseDto> getMyFriends(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        List<Friend> sent = friendRepository.findByFromUserAndStatus(user, InvitationStatus.ACCEPTED);
        List<Friend> received = friendRepository.findByToUserAndStatus(user, InvitationStatus.ACCEPTED);

        return Stream.concat(sent.stream().map(F -> F.getToUser()), received.stream().map(F -> F.getFromUser()))
                .distinct()
                .map(u -> new FriendResponseDto(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
    }

    public FriendCountResponseDto getFriendCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        int total = friendRepository.countAllFriends(user);
        return new FriendCountResponseDto(total);
    }

    public List<FriendResponseDto> searchFriends(String email, String keyword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        return friendRepository.searchFriends(user, keyword).stream()
                .map(f -> {
                    User target = f.getFromUser().equals(user) ? f.getToUser() : f.getFromUser();
                    return new FriendResponseDto(target.getId(), target.getName(), target.getEmail());
                }).collect(Collectors.toList());
    }

    // 요청 취소 (내가 보낸 PENDING 요청만 취소 가능)
    public void cancelRequest(String email, Long requestId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));

        if (!friend.getFromUser().equals(user))
            throw new IllegalArgumentException("권한 없음");

        if (friend.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청은 취소 불가");
        }

        friendRepository.delete(friend);
    }
}
