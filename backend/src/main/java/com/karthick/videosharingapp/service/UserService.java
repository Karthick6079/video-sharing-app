package com.karthick.videosharingapp.service;


import com.karthick.videosharingapp.dto.UserDTO;
import com.karthick.videosharingapp.entity.Subscription;
import com.karthick.videosharingapp.entity.User;
import com.karthick.videosharingapp.repository.SubscriptionRepo;
import com.karthick.videosharingapp.repository.UserRepository;
import com.karthick.videosharingapp.servicelogic.UserLogic;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${spring.auth.user-info-endpoint}")
    private String userInfoEndpoint;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    private final UserLogic userLogic;

    private final SubscriptionRepo subscriptionRepo;

    private final String ANONYMOUS_USER = "anonymousUser";

    private final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserDTO registerUser(Jwt jwt) {
        User user;
        logger.info("Fetching logged in user from database");
        user = getUserFromDB(jwt.getSubject());
        if (user != null) {
            removeSubscriberDetailsFromUser(user);
            return convertUsertoUserDto(user, mapper);
        }
        // Get User information from auth provider using jwt token
        user = getUserInfoFromAuthProvider(jwt);
        logger.info("Store newly signed up user information into database ");
        User savedUser = userRepository.save(user);
        removeSubscriberDetailsFromUser(savedUser);
        return convertUsertoUserDto(savedUser, mapper);
    }


    private void removeSubscriberDetailsFromUser(User user){

        user.setSubscribedToUsers(null);
        user.setSubscribers(null);
    }

    private User getUserInfoFromAuthProvider(Jwt jwt) {
        User user;
        RestClient restClient = RestClient.create();
        try {
            logger.info("Getting necessary information from Auth provider using JWT token for user: {}", jwt.getSubject());
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
        logger.info("Getting user profile information using token");
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User savedUser = getUserFromDB(jwt.getSubject());

        return convertUsertoUserDto(savedUser, mapper);
    }

    public UserDTO convertUsertoUserDto(User user, ModelMapper mapper) {
        logger.info("Mapping the user entity values to User DTO");
        if (mapper.getTypeMap(User.class, UserDTO.class) == null) {
            TypeMap<User, UserDTO> typeMapper = mapper.createTypeMap(User.class, UserDTO.class);
            typeMapper.addMapping(User::getGivenName, UserDTO::setFirstName);
            typeMapper.addMapping(User::getFamilyName, UserDTO::setLastName);
        }
        return mapper.map(user, UserDTO.class);
    }

    public User getCurrentUser() {
        logger.info("Getting currently logged in user information based on access token");
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if(securityContext != null && ANONYMOUS_USER.equals(securityContext.getAuthentication().getPrincipal())){
            return null;
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserFromDB(jwt.getSubject());
    }

    public User getUserFromDB(String sub) {
        logger.info("Getting user from database using jwt sub");
        Optional<User> userOp = userRepository.findBySub(sub);
        return userOp.orElse(null);
    }

    public User getUserById(String userId) {
        logger.info("Getting user from database using userid");
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User cannot find using id - " + userId));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Map<String, Object> subscribe(String userId) {

        User currentUser = getCurrentUser();
        User channel = getUserById(userId);
        logger.info("The user: {} subscribing to user: {}", currentUser.getName(), channel.getName());
        Subscription subscription = new Subscription();
        subscription.setSubscriberId(currentUser.getId());
        subscription.setChannelId(channel.getId());

        subscriptionRepo.save(subscription);
        logger.info("Subscribed information updated in database");

        Long channelSubscribersCount = subscriptionRepo.countByChannelId(channel.getId());

        UserDTO currentUserDTO = mapper.map(currentUser, UserDTO.class);
        Map<String, Object> returnMap = new LinkedHashMap<>();
        logger.info("The updated subscribers count: {} and current user user DTO information sent to frontend", channelSubscribersCount);
        returnMap.put("currentUser", currentUserDTO);
        returnMap.put("videoUploadedSubscribersCount", channelSubscribersCount);

        return returnMap;
    }



    public Map<String, Object> unsubscribe(String userId) {
        User currentUser = getCurrentUser();
        User channel = getUserById(userId);

        logger.info("The user: {} unsubscribing to user: {}", currentUser.getName(), channel.getName());

        subscriptionRepo.deleteBySubscriberIdAndChannelId(currentUser.getId(), channel.getId());
        logger.info("unsubscribed information updated in database");

        Long channelSubscribersCount = subscriptionRepo.countByChannelId(channel.getId());

        UserDTO currentUserDTO = mapper.map(currentUser, UserDTO.class);
        Map<String, Object> returnMap = new LinkedHashMap<>();
        logger.info("The updated subscribers count: {} and current user user DTO information sent to frontend", channelSubscribersCount);
        returnMap.put("currentUser", currentUserDTO);
        returnMap.put("videoUploadedSubscribersCount", channelSubscribersCount);
        return returnMap;
    }

}
