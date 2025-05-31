package com.karthick.videosharingapp.service;


import com.karthick.videosharingapp.domain.dto.ChannelInfoDTO;
import com.karthick.videosharingapp.domain.dto.UserDTO;
import com.karthick.videosharingapp.entity.Subscription;
import com.karthick.videosharingapp.entity.User;
import com.karthick.videosharingapp.repository.SubscriptionRepository;
import com.karthick.videosharingapp.repository.UserRepository;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${spring.auth.user-info-endpoint}")
    private String userInfoEndpoint;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    private final SubscriptionRepository subscriptionRepository;

    private final String ANONYMOUS_USER = "anonymousUser";

    private final RecommendationRefreshQueue recommendationRefreshQueue;


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

    public boolean isUserLoggedIn(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext == null || !ANONYMOUS_USER.equals(securityContext.getAuthentication().getPrincipal());
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

    public ChannelInfoDTO subscribe(String userId) {

        User currentUser = getCurrentUser();
        User channel = getUserById(userId);
        logger.info("The user: {} subscribing to user: {}", currentUser.getName(), channel.getName());
        Subscription subscription = new Subscription();
        subscription.setSubscriberId(currentUser.getId());
        subscription.setChannelId(channel.getId());

        subscriptionRepository.save(subscription);
        logger.info("Subscribed information updated in database");

        Long channelSubscribersCount = subscriptionRepository.countByChannelId(channel.getId());

        logger.info("Preparing channel DTO to frontend after subscribe");
        ChannelInfoDTO channelInfoDTO = getChannelInfoDTO(channel, channelSubscribersCount);
        recommendationRefreshQueue.markUserForRefresh(currentUser.getId());
        return channelInfoDTO;
    }

    private static ChannelInfoDTO getChannelInfoDTO(User channel, Long channelSubscribersCount) {
        ChannelInfoDTO channelInfoDTO = new ChannelInfoDTO();
        channelInfoDTO.setName(channel.getName());
        channelInfoDTO.setDisplayName(channel.getGivenName());
        channelInfoDTO.setSubscriberCount(channelSubscribersCount);
        channelInfoDTO.setUserSubscribed(true);
        return channelInfoDTO;
    }


    public ChannelInfoDTO unsubscribe(String userId) {
        User currentUser = getCurrentUser();
        User channel = getUserById(userId);

        logger.info("The user: {} unsubscribing to user: {}", currentUser.getName(), channel.getName());

        subscriptionRepository.deleteBySubscriberIdAndChannelId(currentUser.getId(), channel.getId());
        logger.info("unsubscribed information updated in database");

        Long channelSubscribersCount = subscriptionRepository.countByChannelId(channel.getId());

        logger.info("Preparing channel DTO to frontend after unsubscribe");
        ChannelInfoDTO channelInfoDTO = getChannelInfoDTO(channel, channelSubscribersCount);
        recommendationRefreshQueue.markUserForRefresh(currentUser.getId());
        return channelInfoDTO;
    }

}
