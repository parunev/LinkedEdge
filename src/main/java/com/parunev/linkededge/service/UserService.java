package com.parunev.linkededge.service;

import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.util.LELogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LELogger logger = new LELogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempt to load a user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User with username {} not found", username);
                    throw new UsernameNotFoundException(
                            "User with username " + username + " not found!"
                    );
                });
    }
}
