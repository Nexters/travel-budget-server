package com.strictmanager.travelbudget.domain.user;

import com.strictmanager.travelbudget.infra.persistence.jpa.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User signUp(User user) {
        Optional<User> existUser = getUserByKakaoId(user.getKakaoId());
        return existUser.orElseGet(() -> userRepository.save(user));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserException::new);
    }

    public Optional<User> getUserByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(Long.valueOf(username)).orElse(null);
    }
}
