package Scheduler.Service;

import Scheduler.Dto.Friend.FriendCountResponseDto;
import Scheduler.Dto.Friend.FriendRequestDto;
import Scheduler.Dto.Friend.FriendResponseDto;
import Scheduler.Dto.Friend.TeamFriendInviteRequest;
import Scheduler.Entity.Friend;
import Scheduler.Entity.Team;
import Scheduler.Entity.TeamInvitation;
import Scheduler.Entity.User;
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
                .accepted(false)
                .build();
        friendRepository.save(friend);
    }

    public List<FriendResponseDto> getReceivedRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        return friendRepository.findByToUserAndAcceptedIsFalse(user).stream()
                .map(f -> new FriendResponseDto(f.getId(), f.getFromUser().getName(), f.getFromUser().getEmail()))
                .collect(Collectors.toList());
    }

    public void acceptRequest(String email, Long requestId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));

        // equals 대신 ID 비교
        if (!friend.getToUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("권한 없음");

        friend.setAccepted(true);
        friendRepository.save(friend);
    }

    public void deleteFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        User target = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("상대 유저 정보 없음"));
        Friend relation = friendRepository.findFriendRelation(user, target)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아님"));
        friendRepository.delete(relation);
    }

    public List<FriendResponseDto> getMyFriends(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        List<Friend> sent = friendRepository.findByFromUserAndAcceptedIsTrue(user);
        List<Friend> received = friendRepository.findByToUserAndAcceptedIsTrue(user);

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

    public void rejectRequest(String email, Long requestId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));
        if (!friend.getToUser().equals(user)) throw new IllegalArgumentException("권한 없음");
        friendRepository.delete(friend);
    }

    public void cancelRequest(String email, Long requestId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청 없음"));
        if (!friend.getFromUser().equals(user)) throw new IllegalArgumentException("권한 없음");
        friendRepository.delete(friend);
    }
}
