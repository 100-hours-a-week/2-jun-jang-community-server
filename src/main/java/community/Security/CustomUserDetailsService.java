package community.Security;

import community.Exception.UserException.UserException;
import community.Model.User;
import community.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User userJpa = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException());

        return new UserDetailsImpl(userJpa);

    }
}

