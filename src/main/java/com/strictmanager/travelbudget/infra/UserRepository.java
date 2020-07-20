package com.strictmanager.travelbudget.infra;

import com.strictmanager.travelbudget.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(String kakaoId);
}
