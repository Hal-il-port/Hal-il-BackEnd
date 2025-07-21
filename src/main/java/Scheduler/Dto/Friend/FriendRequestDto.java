package Scheduler.Dto.Friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {

    @Schema(description = "친구 요청할 대상의 이메일", example = "friend@example.com")
    private String email;
}
