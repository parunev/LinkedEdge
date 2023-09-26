package com.parunev.linkededge.service;

import com.parunev.linkededge.model.User;
import com.parunev.linkededge.model.enums.Authority;
import com.parunev.linkededge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "username";
        User sampleUser = User.builder()
                .username(username)
                .authorities(Collections.singleton(Authority.AUTHORITY_USER))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = userService.loadUserByUsername(username);

        Set<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(sampleUser.getUsername(), userDetails.getUsername());
        assertTrue(authorities.contains("ROLE_USER"));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String nonExistentUser = "username";

        when(userRepository.findByUsername(nonExistentUser)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(nonExistentUser));

        verify(userRepository, times(1)).findByUsername(nonExistentUser);
    }


}
