package community.Api.User.Service;

import community.Api.User.Converter.UserConverter;
import community.Model.JdbcModel.UserJdbc;
import community.RepositoryJdbc.UserRepositoryJdbc;
import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryJdbc userRepositoryJdbc;

    public UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request){
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String userId= "USER-"+UUID.randomUUID().toString();


        //Jdbc 사용
        UserJdbc userJdbc=new UserJdbc(
                userId,
                request.getNickname(),
                request.getEmail(),
                encodedPassword,
                request.getProfileImage()
        );

        userJdbc.setCreatedAt(OffsetDateTime.now());
        userJdbc.setUpdatedAt(OffsetDateTime.now());

        userRepositoryJdbc.save(userJdbc);


        return UserConverter.toCreateUserResponse(userJdbc);
    }
}
