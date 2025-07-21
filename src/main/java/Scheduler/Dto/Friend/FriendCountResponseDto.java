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
public class FriendCountResponseDto {

    @Schema(description = "친구 수", example = "5")
    private int total;
}
