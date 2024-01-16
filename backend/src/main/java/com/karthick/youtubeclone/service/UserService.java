package com.karthick.youtubeclone.service;


import com.karthick.youtubeclone.dto.UserDTO;
import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.repository.UserRepository;
import com.karthick.youtubeclone.servicelogic.UserLogic;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${spring.auth.user-info-endpoint}")
    private String userInfoEndpoint;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    private final UserLogic userLogic;

    private final String ANONYMOUS_USER = "anonymousUser";


    public UserDTO registerUser(Jwt jwt) {
        User user;
        user = getUserFromDB(jwt.getSubject());
        if (user != null) {
            return convertUsertoUserDto(user, mapper);
        }

        // Get User information from auth provider using jwt token
        user = getUserInfoFromAuthProvider(jwt);
        User savedUser = userRepository.save(user);
        return convertUsertoUserDto(savedUser, mapper);
    }

    private User getUserInfoFromAuthProvider(Jwt jwt) {
        User user;
        RestClient restClient = RestClient.create();
        try {
            user = restClient.get()
                    .uri(userInfoEndpoint)
                    .header("Authorization", "Bearer " + jwt.getTokenValue())
                    .retrieve().body(User.class);
        } catch (Exception exp) {
            throw new RuntimeException("Exception occurred while registering the user");
        }
        return user;
    }

    public UserDTO getUserProfileInformation() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User savedUser = getUserFromDB(jwt.getSubject());

        return convertUsertoUserDto(savedUser, mapper);
    }

    public UserDTO convertUsertoUserDto(User user, ModelMapper mapper) {
        if (mapper.getTypeMap(User.class, UserDTO.class) == null) {
            TypeMap<User, UserDTO> typeMapper = mapper.createTypeMap(User.class, UserDTO.class);
            typeMapper.addMapping(User::getGivenName, UserDTO::setFirstName);
            typeMapper.addMapping(User::getFamilyName, UserDTO::setLastName);
        }
        return mapper.map(user, UserDTO.class);
    }

    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if(securityContext != null && ANONYMOUS_USER.equals(securityContext.getAuthentication().getPrincipal())){
            return null;
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserFromDB(jwt.getSubject());
    }

    public User getUserFromDB(String sub) {
        return userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("User cannot find using sub - " + sub));
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User cannot find using id - " + userId));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void subscribe(String userId) {
        User currentUser = getCurrentUser();
        User subscribeToUser = getUserById(userId);

        userLogic.subscribe(currentUser, subscribeToUser);

        userRepository.saveAll(Arrays.asList(currentUser, subscribeToUser));
    }

    public void unsubscribe(String userId) {
        User currentUser = getCurrentUser();
        User unsubscribeToUser = getUserById(userId);

        userLogic.unsubscribe(currentUser, unsubscribeToUser);

        userRepository.saveAll(Arrays.asList(currentUser, unsubscribeToUser));
    }

    public void addToWatchHistory(String videoId) {
        User user = getCurrentUser();
        if(user != null){
            user.addToVideoHistory(videoId);
            userRepository.save(user);
        }
    }

}
