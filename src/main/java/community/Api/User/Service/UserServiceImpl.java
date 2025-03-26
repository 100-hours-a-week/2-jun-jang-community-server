package community.Api.User.Service;

import community.Api.User.Converter.UserConverter;
import community.Api.User.Dtos.UserRequest;
import community.Api.User.Dtos.UserResponse;
import community.Common.Enums.MessageCode;
import community.Exception.UserException.UserException;
import community.Model.User;
import community.Repository.UserRepository;
import community.Util.EntityValidator;
import community.Util.JwtUtil;
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
    private final UserRepository userRepository;
    private final EntityValidator entityValidator;

    public UserResponse.CreateUserResponse createUserService(UserRequest.CreateUserRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            throw new UserException.UserIsValidException();
        }


        String userId = "USER-" + UUID.randomUUID();
        User user = User.builder()
                .userId(userId)
                .userProfile(request.getProfileImage())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .deletedAt(null)
                .build();
        userRepository.save(user);


        return UserConverter.toCreateUserResponse(user);
    }

    public UserResponse.LoginUserResponse loginUserService(UserRequest.LoginUserRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty() || userOptional.get().getDeletedAt() != null) {
            throw new UserException.LoginFailedException();
        }
        User user = userOptional.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String accessToken = JwtUtil.generateAccessToken(user.getUserId());
            String refreshToken = JwtUtil.generateRefreshToken(user.getUserId());
            return UserConverter.toLoginUserResponse(accessToken, refreshToken);
        } else {
            throw new UserException.LoginFailedException();
        }
    }

    public UserResponse.GetUserResponse getUserService(String userId) {
        return UserConverter.toGetUserResponse(entityValidator.getValidUserOrThrow(userId));
    }

    @Transactional
    public String patchUserProfileService(String userId, UserRequest.UpdateUserProfileRequest request) {

        User user = entityValidator.getValidUserOrThrow(userId);
        user.setNickname(request.getNickname() != null ? request.getNickname() : user.getNickname());
        user.setUserProfile(request.getProfileImage() != null ? request.getProfileImage() : user.getUserProfile());


        return MessageCode.PROFILE_UPDATED.getMessage();
    }

    @Transactional
    public String putUserPasswordService(String userId, UserRequest.UpdateUserPasswordRequest request) {


        User user = entityValidator.getValidUserOrThrow(userId);
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        return MessageCode.PASSWORD_UPDATED.getMessage();
    }

    @Transactional
    public String deleteUserService(String userId) {

        User user = entityValidator.getValidUserOrThrow(userId);
        user.setDeletedAt(OffsetDateTime.now());

        return MessageCode.USER_DELETED.getMessage();
    }
}
