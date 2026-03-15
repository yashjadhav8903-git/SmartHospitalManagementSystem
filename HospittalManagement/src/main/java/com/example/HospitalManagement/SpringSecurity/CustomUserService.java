package com.example.HospitalManagement.SpringSecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;
    // 3 . ager user database me hai toh AuthenticationManager ko dega (AuthService me jo hai usko)
    // User kaha se aayega ager sign up he tohh
    @Override
    @Cacheable("users")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByUsername(username).orElseThrow();
    }
}