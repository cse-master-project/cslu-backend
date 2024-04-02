package com.example.csemaster.features.account.user;

import com.example.csemaster.mapper.ActiveUserMapper;
import com.example.csemaster.repository.ActiveUserRepository;
import com.example.csemaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final ActiveUserRepository activeUserRepository;
    public ResponseEntity<?> getUserInfo(String userId) {
        return activeUserRepository.findById(userId)
                .map(activeUser -> ResponseEntity.ok().body(ActiveUserMapper.INSTANCE.toUserInfo(activeUser)))
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> setUserNickname(String userId, String nickname) {
        try {
            return activeUserRepository.findById(userId)
                    .map(activeUser -> {
                        activeUser.setNickname(nickname);
                        activeUserRepository.save(activeUser);

                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
