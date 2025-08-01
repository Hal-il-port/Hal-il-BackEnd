package Scheduler.Dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchResponseDto {
    private Long id;
    private String name;
    private String email;
}
