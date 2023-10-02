package com.parunev.linkededge.util;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.Profile;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.repository.ProfileRepository;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.ProfileNotFoundException;
import com.parunev.linkededge.security.exceptions.UserNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.parunev.linkededge.security.CurrentUser.getCurrentUserDetails;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@Component
@RequiredArgsConstructor
public class UserProfileUtils {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final LELogger leLogger = new LELogger(UserProfileUtils.class);

    public Pair<User, Profile> getUserAndProfile(){
        User user = findUserByContextHolder();
        Profile profile = findProfileByUserId(user.getId());

        return Pair.of(user, profile);
    }

    public User findUserByContextHolder() {
        return userRepository.findByUsername(getCurrentUserDetails().getUsername())
                .orElseThrow(() -> {
                    leLogger.warn("User not found.");
                    throw new UserNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("User not present in the database.")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                });
    }

    private Profile findProfileByUserId(UUID id) {
        return profileRepository.findByUserId(id)
                .orElseThrow(() -> {
                    leLogger.warn("Profile not found");
                    throw new ProfileNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("Profile not present in the database")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                });
    }
}
