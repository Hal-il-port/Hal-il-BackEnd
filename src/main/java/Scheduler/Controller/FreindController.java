package Scheduler.Controller;

import Scheduler.Dto.Friend.FriendCountResponseDto;
import Scheduler.Dto.Friend.FriendRequestDto;
import Scheduler.Dto.Friend.FriendResponseDto;
import Scheduler.Dto.Friend.TeamFriendInviteRequest;
import Scheduler.Service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FreindController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<Void> sendRequest(@RequestBody FriendRequestDto request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        friendService.sendRequest(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponseDto>> getRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userDetails.getUsername()));
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Void> accept(@PathVariable Long requestId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        friendService.acceptRequest(userDetails.getUsername(), requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendEmail}")
    public ResponseEntity<Void> deleteFriend(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String friendEmail) {
        friendService.deleteFriend(userDetails.getUsername(), friendEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendResponseDto>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getMyFriends(userDetails.getUsername()));
    }

    @GetMapping("/count")
    public ResponseEntity<FriendCountResponseDto> getFriendCount(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendCount(userDetails.getUsername()));
    }

    @PostMapping("/invite/team")
    public ResponseEntity<Void> inviteToTeam(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody TeamFriendInviteRequest request) {
        friendService.inviteFriendsToTeam(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FriendResponseDto>> search(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam String keyword) {
        return ResponseEntity.ok(friendService.searchFriends(userDetails.getUsername(), keyword));
    }

    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<Void> reject(@PathVariable Long requestId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        friendService.rejectRequest(userDetails.getUsername(), requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<Void> cancel(@PathVariable Long requestId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        friendService.cancelRequest(userDetails.getUsername(), requestId);
        return ResponseEntity.ok().build();
    }
}
