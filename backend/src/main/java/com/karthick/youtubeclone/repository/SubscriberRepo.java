package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.Subscriber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepo extends MongoRepository<Subscriber, String> {


    @Query("{$and:[{userId:?0},{subscriberUserId:?1}]}")
    Optional<Subscriber> findSubscriber(String userId, String subscriberUserId);
}
