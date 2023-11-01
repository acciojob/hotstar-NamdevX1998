package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser=userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user=userRepository.findById(userId).get();
        if(user==null)
            return 0;
        int age=user.getAge();
        String plan=user.getSubscription().getSubscriptionType().toString();
        List<WebSeries>webSeriesList=webSeriesRepository.findAll();
        int count=0;
        for(WebSeries webSeries:webSeriesList){
            int agelimit=webSeries.getAgeLimit();
            String subscriptionType=webSeries.getSubscriptionType().toString();
            if(plan.equalsIgnoreCase("basic")){
                if(age>=agelimit && (subscriptionType.equalsIgnoreCase("basic"))){
                    count++;
                }
            }else if(plan.equalsIgnoreCase("pro")){
                if(age>=agelimit && (subscriptionType.equalsIgnoreCase("basic") ||
                        subscriptionType.equalsIgnoreCase("pro"))){
                    count++;
                }
            }
            else{
                if(age>=agelimit && (subscriptionType.equalsIgnoreCase("basic") ||
                        subscriptionType.equalsIgnoreCase("pro") ||
                        subscriptionType.equalsIgnoreCase("elite"))){
                    count++;
                }
            }
        }
        return count;
    }


}
