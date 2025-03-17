package community.Security;

import community.Exception.UserException.UserException;
import community.Model.JdbcModel.UserJdbc;
import community.RepositoryJdbc.UserRepositoryJdbc;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepositoryJdbc userRepository;

    public CustomUserDetailsService(UserRepositoryJdbc userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserJdbc userJdbc = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("유저를 찾지 못했습니다."));

        return new UserDetailsImpl(userJdbc);
    }
}

