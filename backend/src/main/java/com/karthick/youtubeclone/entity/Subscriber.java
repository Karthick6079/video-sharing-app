package com.karthick.youtubeclone.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("subscribers")
@Getter
@Setter
@RequiredArgsConstructor
public class Subscriber {

    @Id
    private String id;

    private String userId;

    private String subscriberUserId;

    private LocalDateTime subscribedOn;
}
