package community.Api.User.Service;

import community.Api.User.Converter.UserConverter;
import community.Exception.UserException.UserException;
import community.Model.JdbcModel.UserJdbc;
import community.RepositoryJdbc.UserRepositoryJdbc;
import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.utill.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryJdbc userRepositoryJdbc;

    public UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request){
        Optional<UserJdbc> user= userRepositoryJdbc.findByEmail(request.getEmail());
        if(user.isPresent()){

            throw new UserException.UserIsValidException("이미 회원정보가 존재합니다.");
        }


        String userId= "USER-"+UUID.randomUUID();


        //Jdbc 사용
        UserJdbc userJdbc=new UserJdbc(
                userId,
                request.getNickname(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getProfileImage()
        );

        userJdbc.setCreatedAt(OffsetDateTime.now());
        userJdbc.setUpdatedAt(OffsetDateTime.now());

        userRepositoryJdbc.save(userJdbc);


        return UserConverter.toCreateUserResponse(userJdbc);
    }

    public UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request){
        Optional<UserJdbc> userJdbc= userRepositoryJdbc.findByEmail(request.getEmail());
        UserJdbc user;
        if(userJdbc.isPresent()){
            user=userJdbc.get();

        }else{
            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었습니다. ");
        }
        if(passwordEncoder.matches(request.getPassword(), user.getPassword())){
           String accessToken = JwtUtil.generateAccessToken(user.getUserId());
           String refreshToken = JwtUtil.generateRefreshToken(user.getUserId());
           return UserConverter.toLoginUserResponse(accessToken, refreshToken);
        }else{
            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었습니다. ");
        }

    }
}
