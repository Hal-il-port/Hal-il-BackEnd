package Scheduler.Controller;

import Scheduler.Dto.Friend.FriendCountResponseDto;
import Scheduler.Dto.Friend.FriendRequestDto;
import Scheduler.Dto.Friend.FriendResponseDto;
import Scheduler.Service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Tag(name = "친구", description = "친구 요청, 수락, 삭제 등 친구 관련 API")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 요청", description = "다른 유저에게 친구 요청을 보냅니다.")
    @PostMapping("/request")
    public ResponseEntity<Void> sendRequest(@RequestBody FriendRequestDto request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("친구 요청 들어옴: from=" + userDetails.getUsername() + " to=" + request.getEmail());
        friendService.sendRequest(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "받은 친구 요청 조회", description = "자신이 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponseDto>> getRequests(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userDetails.getUsername()));
    }

    @Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다.")
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Void> accept(
            @PathVariable Long requestId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        friendService.acceptRequest(userDetails.getUsername(), requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 삭제", description = "친구 목록에서 해당 유저를 삭제합니다.")
    @DeleteMapping("/{friendEmail}")
    public ResponseEntity<Void> deleteFriend(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String friendEmail) {
        friendService.deleteFriend(userDetails.getUsername(), friendEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 친구 목록 조회", description = "현재 로그인한 유저의 친구 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FriendResponseDto>> getFriends(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getMyFriends(userDetails.getUsername()));
    }

    @Operation(summary = "친구 수 조회", description = "내 친구 수를 반환합니다.")
    @GetMapping("/count")
    public ResponseEntity<FriendCountResponseDto> getFriendCount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendCount(userDetails.getUsername()));
    }

    @Operation(summary = "친구 검색", description = "닉네임이나 이메일로 친구를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<FriendResponseDto>> search(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "검색 키워드 (이름 또는 이메일)", example = "john") @RequestParam String keyword) {
        return ResponseEntity.ok(friendService.searchFriends(userDetails.getUsername(), keyword));
    }

    @Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다.")
    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<Void> reject(
            @PathVariable Long requestId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        friendService.rejectRequest(userDetails.getUsername(), requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 취소", description = "내가 보낸 친구 요청을 취소합니다.")
    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long requestId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        friendService.cancelRequest(userDetails.getUsername(), requestId);
        return ResponseEntity.ok().build();
    }
}