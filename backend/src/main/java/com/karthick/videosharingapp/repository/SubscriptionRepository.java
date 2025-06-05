package com.karthick.videosharingapp.repository;

import com.karthick.videosharingapp.domain.dto.ChannelIdDTO;
import com.karthick.videosharingapp.entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    List<Subscription> findByChannelId(String userId); // Who subscribed to me or Fetch list of subscribers for given user

    List<Subscription> findBySubscriberId(String userId);  // Who I subscribed To  or Fetch given user subscribed channel list

    boolean existsBySubscriberIdAndChannelId(String subscriber, String channel);

    void deleteBySubscriberIdAndChannelId(String subscriber, String channel);

    Long countByChannelId(String channelId);

    @Query( value = "{'subscriberId':?0}", fields = "{'channelId':1, '_id':0}")
    List<ChannelIdDTO> findChannelIdBySubscriberId(String userId);
}