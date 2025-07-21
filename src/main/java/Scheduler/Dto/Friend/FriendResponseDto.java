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
public class FriendResponseDto {

    @Schema(description = "친구 ID", example = "1")
    private Long id;

    @Schema(description = "친구 이름", example = "ooo")
    private String name;

    @Schema(description = "친구 이메일", example = "friend@example.com")
    private String email;
}
