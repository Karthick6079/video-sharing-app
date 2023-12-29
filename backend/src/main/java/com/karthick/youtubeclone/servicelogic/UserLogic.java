package com.karthick.youtubeclone.servicelogic;

import com.karthick.youtubeclone.entity.User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserLogic {


    // Increment the subscriber count in target user
    // Add current user to subscribed user list for subscribingUser
    // Updated current user with subscribed user
    public void subscribe(User currentUser, User subscribeToUser){
        if(!isAlreadySubscribedToUser(currentUser, subscribeToUser.getId())){
            subscribeToUser(currentUser, subscribeToUser);
        } else{
            unsubscribeToUser(currentUser, subscribeToUser);
        }
    }
    public void unsubscribe(User currentUser, User subscribeToUser){
        if(isAlreadySubscribedToUser(currentUser, subscribeToUser.getId())){
            unsubscribeToUser(currentUser, subscribeToUser);
        } else{
            subscribeToUser(currentUser, subscribeToUser);
        }
    }


    private static void subscribeToUser(User currentUser, User subscribeToUser) {
        subscribeToUser.incrementSubscriberCount();

        Set<String> subscribers =  subscribeToUser.getSubscribers();
        subscribers.add(currentUser.getId());
        subscribeToUser.setSubscribers(subscribers);

        currentUser.getSubscribedToUsers().add(subscribeToUser.getId());
        currentUser.incrementSubscribedToCount();
    }

    public void unsubscribeToUser(User currentUser, User unsubscribeToUser){
        // Increment the subscriber count
        // Add current user to subscribed user list for subscribingUsere
        unsubscribeToUser.decrementSubscriberCount();

        Set<String> subscribers =  unsubscribeToUser.getSubscribers();
        subscribers.remove(currentUser.getId());
        unsubscribeToUser.setSubscribers(subscribers);

        currentUser.getSubscribedToUsers().remove(unsubscribeToUser.getId());
        currentUser.decrementSubscribedToCount();

    }

    public boolean isAlreadySubscribedToUser(User currentUser, String subscribeToUserId){
        return currentUser.getSubscribedToUsers().stream().anyMatch(existingId -> existingId.equals(subscribeToUserId));
    }

}
