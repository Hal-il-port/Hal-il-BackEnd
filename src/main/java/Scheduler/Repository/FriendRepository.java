package Scheduler.Repository;

import Scheduler.Entity.Friend;
import Scheduler.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByToUserAndAcceptedIsFalse(User toUser);
    List<Friend> findByFromUserAndAcceptedIsTrue(User fromUser);
    List<Friend> findByToUserAndAcceptedIsTrue(User toUser);
    Optional<Friend> findByFromUserAndToUser(User from, User to);

    @Query("SELECT COUNT(f) FROM Friend f WHERE (f.fromUser = :user OR f.toUser = :user) AND f.accepted = true")
    int countAllFriends(@Param("user") User user);

    @Query("SELECT f FROM Friend f WHERE f.accepted = true AND (f.fromUser = :user OR f.toUser = :user) AND (f.fromUser.name LIKE %:keyword% OR f.fromUser.email LIKE %:keyword% OR f.toUser.name LIKE %:keyword% OR f.toUser.email LIKE %:keyword%)")
    List<Friend> searchFriends(@Param("user") User user, @Param("keyword") String keyword);

    @Query("SELECT f FROM Friend f WHERE f.accepted = true AND ((f.fromUser = :user AND f.toUser = :target) OR (f.fromUser = :target AND f.toUser = :user))")
    Optional<Friend> findFriendRelation(@Param("user") User user, @Param("target") User target);
}
