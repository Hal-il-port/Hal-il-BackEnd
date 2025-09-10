package Scheduler.Entity;

public enum NotificationType {
    PERSONAL_DEADLINE,    // 개인 일정 마감
    TEAM_INVITE,          // 팀 초대
    TEAM_INVITE_ACCEPT,   // 팀 초대 수락
    TEAM_INVITE_REJECT, // 팀초대 거절
    FRIEND_REQUEST,       // 친구 요청
    FRIEND_ACCEPT, // 친구 수락
    FRIEND_REJECT, //친구 거절
    TEAM_DEADLINE,        // 팀 일정 마감
    TEAM_SCHEDULE_CREATE,     // 팀 일정 생성
    TEAM_SCHEDULE_UPDATE      // 팀 일정 상태 변경
}
