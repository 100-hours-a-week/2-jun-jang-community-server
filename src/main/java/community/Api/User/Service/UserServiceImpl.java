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
                ,null
        );

        userJdbc.setCreatedAt(OffsetDateTime.now());
        userJdbc.setUpdatedAt(OffsetDateTime.now());

        userRepositoryJdbc.save(userJdbc);


        return UserConverter.toCreateUserResponse(userJdbc);
    }

    public UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request){
        Optional<UserJdbc> userJdbc = userRepositoryJdbc.findByEmail(request.getEmail());

        if (userJdbc.isEmpty() || userJdbc.get().getDeletedAt() != null) {
            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었거나, 탈퇴한 계정입니다.");
        }

        UserJdbc user = userJdbc.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String accessToken = JwtUtil.generateAccessToken(user.getUserId());
            String refreshToken = JwtUtil.generateRefreshToken(user.getUserId());
            return UserConverter.toLoginUserResponse(accessToken, refreshToken);
        } else {
            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    public UserResponse.GetUserResponse GetUserService(String userId){
        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);

        if (user.isEmpty() || user.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        return UserConverter.toGetUserResponse(user.get());
    }
    public String PatchUserProfileService(String userId, UserRequest.UpdateUserProfileRequest request){
        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);

        if (user.isEmpty() || user.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        UserJdbc userJdbc = user.get();
        String updatedNickname = request.getNickname() != null ? request.getNickname() : userJdbc.getNickname();
        String updatedProfileImage = request.getProfileImage() != null ? request.getProfileImage() : userJdbc.getUserProfile();

        userRepositoryJdbc.updateUserInfo(userId, updatedNickname, updatedProfileImage);

        return "프로필 변경에 성공했습니다.";
    }
    public String PutUserPasswordService(String userId, UserRequest.UpdateUserPasswordRequest request){
        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);

        if (user.isEmpty() || user.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        userRepositoryJdbc.updatePassword(userId, passwordEncoder.encode(request.getPassword()));

        return "비밀번호 변경에 성공했습니다.";
    }
    public String DeleteUserService(String userId) {
        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);
        log.info("userId: {}", userId);
        log.info("user from DB: {}", user.orElse(null));
        if (user.isEmpty() || user.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 이미 탈퇴한 계정입니다.");
        }

        userRepositoryJdbc.deleteByUserId(userId);

        return "회원 탈퇴가 완료되었습니다.";
    }
}
