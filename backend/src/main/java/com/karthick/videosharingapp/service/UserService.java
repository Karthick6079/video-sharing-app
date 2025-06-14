package com.karthick.videosharingapp.service;


import com.karthick.videosharingapp.domain.dto.ChannelInfoDTO;
import com.karthick.videosharingapp.domain.dto.UserDTO;
import com.karthick.videosharingapp.entity.Subscription;
import com.karthick.videosharingapp.entity.User;
import com.karthick.videosharingapp.repository.SubscriptionRepository;
import com.karthick.videosharingapp.repository.UserRepository;
import com.karthick.videosharingapp.util.AppUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.karthick.videosharingapp.constants.DatabaseConstants.*;

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

    private final MongoTemplate mongoTemplate;

    private final AppUtil appUtil;


    public UserDTO registerUser(Jwt jwt) {
        User userDetailsFromDB;
        logger.info("Fetching logged in user from database");
        userDetailsFromDB = getUserFromDB(jwt.getSubject());
        User userDetailsFromAuthProvider = getUserInfoFromAuthProvider(jwt);
        User savedUser = updateLatestUserDetailsAndSaveInDB(userDetailsFromDB, userDetailsFromAuthProvider);
        logger.info("Store newly signed up user information into database ");
        return convertUsertoUserDto(savedUser, mapper);
    }


    private User  updateLatestUserDetailsAndSaveInDB  (User userDetailsFromDB, User  userDetailsFromAuthProvider){


        // User first time login into system
        String name = "";

        if(userDetailsFromDB == null){
            String displayName = userDetailsFromAuthProvider.getDisplayName();
            name = displayName.replaceAll("\\s","").toLowerCase();

            // If no difference between name and display name means, display name can be name
            if(name.equals(displayName))
                name = displayName;

            Query query = new Query(Criteria.where(NAME_COLUMN).is(name));
            boolean isUsernameAlreadyExist = mongoTemplate.exists(query,USERS_COLLECTION);
            if(isUsernameAlreadyExist){
                String randomNumberString = appUtil.generateRandomFourDigitNumber();
                name = name.concat(randomNumberString);
            }
        } else {
            name = userDetailsFromDB.getName();
            userDetailsFromAuthProvider.setId(userDetailsFromDB.getId());
        }

//        Update update = getUpdate(userDetailsFromAuthProvider, name);

//        UpdateResult updateResult =  mongoTemplate.upsert(query,update, USERS_COLLECTION);

        userDetailsFromAuthProvider.setName(name);
        return userRepository.save(userDetailsFromAuthProvider);
    }

    private static Update getUpdate(User userDetailsFromAuthProvider, String name) {
        Update update = new Update();
        update.set(NAME_COLUMN, name);
        update.set(DISPLAY_NAME_COLUMN, userDetailsFromAuthProvider.getDisplayName());
        update.set(FIRSTNAME_COLUMN, userDetailsFromAuthProvider.getFirstname());
        update.set(LASTNAME_COLUMN, userDetailsFromAuthProvider.getLastname());
        update.set(PICTURE_COLUMN, userDetailsFromAuthProvider.getPicture());
        update.set(SUB_COLUMN, userDetailsFromAuthProvider.getSub());
        update.set(EMAIL_COLUMN, userDetailsFromAuthProvider.getEmail());
        return update;
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
            typeMapper.addMapping(User::getFirstname, UserDTO::setFirstname);
            typeMapper.addMapping(User::getLastname, UserDTO::setLastname);
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
        logger.info("The user: {} subscribing to user: {}", currentUser.getDisplayName(), channel.getDisplayName());
        Subscription subscription = new Subscription();
        subscription.setSubscriberId(currentUser.getId());
        subscription.setChannelId(channel.getId());

        subscriptionRepository.save(subscription);
        logger.info("Subscribed information updated in database");

        Long channelSubscribersCount = subscriptionRepository.countByChannelId(channel.getId());

        logger.info("Preparing channel DTO to frontend after subscribe");
        ChannelInfoDTO channelInfoDTO = getChannelInfoDTO(channel, channelSubscribersCount, true);
        recommendationRefreshQueue.markUserForRefresh(currentUser.getId());
        return channelInfoDTO;
    }

    private static ChannelInfoDTO getChannelInfoDTO(User channel, Long channelSubscribersCount, boolean isSubscribed) {
        ChannelInfoDTO channelInfoDTO = new ChannelInfoDTO();
        channelInfoDTO.setName(channel.getDisplayName());
        channelInfoDTO.setDisplayName(channel.getFirstname());
        channelInfoDTO.setSubscribersCount(channelSubscribersCount);
        channelInfoDTO.setUserSubscribed(isSubscribed);
        return channelInfoDTO;
    }


    public ChannelInfoDTO unsubscribe(String userId) {
        User currentUser = getCurrentUser();
        User channel = getUserById(userId);

        logger.info("The user: {} unsubscribing to user: {}", currentUser.getDisplayName(), channel.getDisplayName());

        subscriptionRepository.deleteBySubscriberIdAndChannelId(currentUser.getId(), channel.getId());
        logger.info("unsubscribed information updated in database");

        Long channelSubscribersCount = subscriptionRepository.countByChannelId(channel.getId());

        logger.info("Preparing channel DTO to frontend after unsubscribe");
        ChannelInfoDTO channelInfoDTO = getChannelInfoDTO(channel, channelSubscribersCount, false);
        recommendationRefreshQueue.markUserForRefresh(currentUser.getId());
        return channelInfoDTO;
    }

}
