package com.karthick.youtubeclone.repository;

import com.karthick.youtubeclone.entity.Subscriber;
import com.karthick.youtubeclone.entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepo extends MongoRepository<Subscription, String> {

    @Query("{$and:[{userId:?0},{subscriptionUserId:?1}]}")
    Optional<Subscription> findSubscription(String userId, String subscriptionUserId);
}
