package com.karthick.youtubeclone.servicelogic;

import com.karthick.youtubeclone.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserLogic {

    private final Logger logger = LoggerFactory.getLogger(UserLogic.class);


    // Increment the subscriber count in target user
    // Add current user to subscribed user list for subscribingUser
    // Updated current user with subscribed user
    public void subscribe(User currentUser, User subscribeToUser){
        if(!isAlreadySubscribedToUser(currentUser, subscribeToUser.getId())){
            logger.info("The current user is not subscriber of {}, So subscribing the user", subscribeToUser.getName());
            subscribeToUser(currentUser, subscribeToUser);
        } else{
            logger.info("The current user is subscriber of {}, So unsubscribing the user", subscribeToUser.getName());
            unsubscribeToUser(currentUser, subscribeToUser);
        }
    }
    public void unsubscribe(User currentUser, User subscribeToUser){
        if(isAlreadySubscribedToUser(currentUser, subscribeToUser.getId())){
            logger.info("The current user is subscriber of {}, So unsubscribing the user", subscribeToUser.getName());
            unsubscribeToUser(currentUser, subscribeToUser);
        } else{
            logger.info("The current user is not subscriber of {}, So subscribing the user", subscribeToUser.getName());
            subscribeToUser(currentUser, subscribeToUser);
        }
    }


    private void subscribeToUser(User currentUser, User subscribeToUser) {
        logger.info("Increasing the {}'s subscriber count by 1 from {}", subscribeToUser.getName(), subscribeToUser.getSubscribersCount());
        subscribeToUser.incrementSubscriberCount();

        Set<String> subscribers =  subscribeToUser.getSubscribers();
        subscribers.add(currentUser.getId());
        subscribeToUser.setSubscribers(subscribers);

        currentUser.getSubscribedToUsers().add(subscribeToUser.getId());
        logger.info("Increasing the {}'s subscribed count by 1 from {}", currentUser.getName(), currentUser.getSubscribersCount());
        currentUser.incrementSubscribedToCount();
    }

    public void unsubscribeToUser(User currentUser, User unsubscribeToUser){
        // Increment the subscriber count
        // Add current user to subscribed user list for subscribingUsere
        logger.info("Decreasing the {}'s subscriber count by 1 from {}", unsubscribeToUser.getName(), unsubscribeToUser.getSubscribersCount());
        unsubscribeToUser.decrementSubscriberCount();

        Set<String> subscribers =  unsubscribeToUser.getSubscribers();
        subscribers.remove(currentUser.getId());
        unsubscribeToUser.setSubscribers(subscribers);

        currentUser.getSubscribedToUsers().remove(unsubscribeToUser.getId());
        logger.info("Decreasing the {}'s subscribed count by 1 from {}", currentUser.getName(), currentUser.getSubscribersCount());
        currentUser.decrementSubscribedToCount();

    }

    public boolean isAlreadySubscribedToUser(User currentUser, String subscribeToUserId){
        return currentUser.getSubscribedToUsers().stream().anyMatch(existingId -> existingId.equals(subscribeToUserId));
    }

}
