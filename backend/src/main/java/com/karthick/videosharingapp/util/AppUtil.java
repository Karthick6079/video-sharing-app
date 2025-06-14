package com.karthick.videosharingapp.util;


import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;


@Component
public class AppUtil {


    public String generateRandomFourDigitNumber(){
       int randomFourDigitNumber = ThreadLocalRandom.current().nextInt(1000, 10000);
       return  Integer.toString(randomFourDigitNumber);

    }
}
