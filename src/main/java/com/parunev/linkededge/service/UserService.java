package com.parunev.linkededge.service;

import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.util.LELogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * The `UserService` class is responsible for user authentication and retrieval of user details
 * for the LinkedEdge application. It implements the Spring Security `UserDetailsService`
 * interface, allowing it to load user details for authentication purposes.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LELogger logger = new LELogger(UserService.class);

    /**
     * This method is used to load user details by their username for authentication.
     *
     * @param username The username of the user to be loaded.
     * @return A UserDetails object containing the user's information.
     * @throws UsernameNotFoundException if the user with the specified username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempt to load a user by username: {}", username);

        // Retrieve the user from the repository by their username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User with username {} not found", username);
                    throw new UsernameNotFoundException(
                            "User with username " + username + " not found!"
                    );
                });
    }
}
