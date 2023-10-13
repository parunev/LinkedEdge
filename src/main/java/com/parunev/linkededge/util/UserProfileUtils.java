package com.parunev.linkededge.util;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.Profile;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.repository.ProfileRepository;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.ResourceNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.parunev.linkededge.security.CurrentUser.getCurrentUserDetails;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * Utility class for managing user profiles and related operations.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Component
@RequiredArgsConstructor
public class UserProfileUtils {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final LELogger leLogger = new LELogger(UserProfileUtils.class);

    /**
     * Retrieves the user and associated profile for the currently authenticated user.
     *
     * @return A {@link Pair} containing the user and profile.
     */
    public Pair<User, Profile> getUserAndProfile(){
        User user = findUserByContextHolder();
        Profile profile = findProfileByUserId(user.getId());

        return Pair.of(user, profile);
    }

    /**
     * Retrieves the user based on the current user context.
     *
     * @return The user associated with the current authentication context.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public User findUserByContextHolder() {
        return userRepository.findByUsername(getCurrentUserDetails().getUsername())
                .orElseThrow(() -> {
                    leLogger.warn("User not found.");
                    throw new ResourceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("User not present in the database.")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                });
    }

    /**
     * Retrieves the profile for a given user ID.
     *
     * @param id The user ID for which to retrieve the profile.
     * @return The user's profile.
     * @throws ResourceNotFoundException if the profile is not found.
     */
    private Profile findProfileByUserId(UUID id) {
        return profileRepository.findByUserId(id)
                .orElseThrow(() -> {
                    leLogger.warn("Profile not found");
                    throw new ResourceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("Profile not present in the database")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                });
    }
}
