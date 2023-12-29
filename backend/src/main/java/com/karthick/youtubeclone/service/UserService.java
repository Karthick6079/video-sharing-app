package com.karthick.youtubeclone.service;


import com.karthick.youtubeclone.dto.UserDto;
import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${spring.auth.user-info-endpoint}")
    private String userInfoEndpoint;

    private final ModelMapper mapper;


    public void registerUser(String token){
        User user = null;

        RestClient restClient = RestClient.create();
        try{
             user = restClient.get()
                     .uri(userInfoEndpoint)
                    .header("Authorization", "Bearer " + token)
                    .retrieve().body(User.class);
        } catch (Exception exp){
            throw new RuntimeException("Exception occurred while registering the user");
        }

        assert user != null;
        userRepository.save(user);
    }

    public UserDto getUserProfileInformation(){
       Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       User savedUser = getUserFromDB(jwt.getSubject());

       return convertUsertoUserDto(savedUser);
    }

    private UserDto convertUsertoUserDto(User user){
        if(this.mapper.getTypeMap(User.class, UserDto.class) == null){
            TypeMap<User, UserDto> typeMapper = this.mapper.createTypeMap(User.class, UserDto.class);
            typeMapper.addMapping(User::getGivenName, UserDto::setFirstName);
            typeMapper.addMapping(User::getFamilyName, UserDto::setLastName);
        }
        return mapper.map(user, UserDto.class);
    }

    public User getCurrentUser(){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserFromDB(jwt.getSubject());
    }

    public User getUserFromDB(String sub){
        return userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("User cannot find using sub - " + sub));
    }

    public void saveUser(User user){
        userRepository.save(user);
    }
}
