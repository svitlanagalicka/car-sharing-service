package mate.academy.carsharing.security;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Can not find user by email"));
    }
}
