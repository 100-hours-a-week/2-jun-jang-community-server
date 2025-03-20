package community.Api.User.Service;

import community.Repository.RepositoryJpa.UserRepositoryJpa;
import community.Api.User.Converter.UserConverter;
import community.Exception.UserException.UserException;

import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Model.JpaModel.UserJpa;
import community.utill.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserRepositoryJpa userRepositoryJpa;
    public UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request){
        Optional<UserJpa> userOptional= userRepositoryJpa.findByEmail(request.getEmail());
        if(userOptional.isPresent()){
            throw new UserException.UserIsValidException("이미 회원정보가 존재합니다.");
        }


        String userId= "USER-"+UUID.randomUUID();
        UserJpa user= UserJpa.builder()
                .userId(userId)
                .userProfile(request.getProfileImage())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .deletedAt(null)
                .build();
        userRepositoryJpa.save(user);

//        //Jdbc 사용
//        UserJdbc userJdbc=new UserJdbc(
//                userId,
//                request.getNickname(),
//                request.getEmail(),
//                passwordEncoder.encode(request.getPassword()),
//                request.getProfileImage()
//                ,null
//        );
//
//        userJdbc.setCreatedAt(OffsetDateTime.now());
//        userJdbc.setUpdatedAt(OffsetDateTime.now());
//
//        userRepositoryJdbc.save(userJdbc);


        return UserConverter.toCreateUserResponse(user);
    }

    public UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request){
//        Optional<UserJdbc> userJdbc = userRepositoryJdbc.findByEmail(request.getEmail());
//
//        if (userJdbc.isEmpty() || userJdbc.get().getDeletedAt() != null) {
//            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었거나, 탈퇴한 계정입니다.");
//        }
//
//        UserJdbc user = userJdbc.get();
        Optional<UserJpa> userOptional= userRepositoryJpa.findByEmail(request.getEmail());
        if(userOptional.isEmpty() || userOptional.get().getDeletedAt() != null){
            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었거나, 탈퇴한 계정입니다.");
        }
        UserJpa user= userOptional.get();
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String accessToken = JwtUtil.generateAccessToken(user.getUserId());
            String refreshToken = JwtUtil.generateRefreshToken(user.getUserId());
            return UserConverter.toLoginUserResponse(accessToken, refreshToken);
        } else {
            throw new UserException.LoginFailedException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    public UserResponse.GetUserResponse GetUserService(String userId){
//        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);
        Optional<UserJpa> userOptional= userRepositoryJpa.findById(userId);
        if (userOptional.isEmpty() || userOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        return UserConverter.toGetUserResponse(userOptional.get());
    }
    @Transactional
    public String PatchUserProfileService(String userId, UserRequest.UpdateUserProfileRequest request){
//        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);
        Optional<UserJpa> userOptional= userRepositoryJpa.findById(userId);
        if (userOptional.isEmpty() || userOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }

        UserJpa userJpa = userOptional.get();
        userJpa.setNickname(request.getNickname() != null ? request.getNickname() : userJpa.getNickname());
        userJpa.setUserProfile(request.getProfileImage() != null ? request.getProfileImage() : userJpa.getUserProfile());

//        userRepositoryJdbc.updateUserInfo(userId, updatedNickname, updatedProfileImage);

        return "프로필 변경에 성공했습니다.";
    }
    @Transactional
    public String PutUserPasswordService(String userId, UserRequest.UpdateUserPasswordRequest request){
//        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);
        Optional<UserJpa> userOptional= userRepositoryJpa.findById(userId);
        if (userOptional.isEmpty() || userOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 탈퇴한 계정입니다.");
        }
        UserJpa userJpa = userOptional.get();
        userJpa.setPassword(passwordEncoder.encode(request.getPassword()));

//        userRepositoryJdbc.updatePassword(userId, passwordEncoder.encode(request.getPassword()));

        return "비밀번호 변경에 성공했습니다.";
    }
    @Transactional
    public String DeleteUserService(String userId) {
//        Optional<UserJdbc> user = userRepositoryJdbc.findByUserId(userId);
        Optional<UserJpa> userOptional= userRepositoryJpa.findById(userId);


        if (userOptional.isEmpty() || userOptional.get().getDeletedAt() != null) {
            throw new UserException.UserNotFoundException("유저를 찾을 수 없거나 이미 탈퇴한 계정입니다.");
        }
        UserJpa userJpa = userOptional.get();
        userJpa.setDeletedAt(OffsetDateTime.now());
//        userRepositoryJdbc.deleteByUserId(userId);

        return "회원 탈퇴가 완료되었습니다.";
    }
}
