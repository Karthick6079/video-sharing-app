package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findBySub(String sub);
}
