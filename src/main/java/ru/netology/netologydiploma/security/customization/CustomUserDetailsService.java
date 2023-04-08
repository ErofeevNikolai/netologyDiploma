package ru.netology.netologydiploma.security.customization;

import ru.netology.netologydiploma.security.entity.User;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.netology.netologydiploma.security.repository.UserRepository;

@Component
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
      return userRepository.findByUserName(username);
    }
}
