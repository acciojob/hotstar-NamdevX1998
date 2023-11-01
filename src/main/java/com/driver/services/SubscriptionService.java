package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){
        Subscription subscription=new Subscription();
        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        if(user==null)
            return 0;
        int noOfScreens=subscriptionEntryDto.getNoOfScreensRequired();
        SubscriptionType subscriptionType=subscriptionEntryDto.getSubscriptionType();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setNoOfScreensSubscribed(noOfScreens);
        int cost=0;
        if(subscription.getSubscriptionType().toString().equalsIgnoreCase("BASIC")){
            cost=500+(200*subscription.getNoOfScreensSubscribed());
        }else if(subscription.getSubscriptionType().toString().equalsIgnoreCase("PRO")){
            cost=800+(250*subscription.getNoOfScreensSubscribed());
        }else{
            cost=1000+(350*subscription.getNoOfScreensSubscribed());
        }
        subscription.setTotalAmountPaid(cost);
        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);
        //Save The subscription Object into the Db and return the total Amount that user has to pay

        return cost;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{
        User user=userRepository.findById(userId).get();
        if(user==null)
            return 0;
        Subscription subscription=user.getSubscription();
        int noOfDevices=subscription.getNoOfScreensSubscribed();
        SubscriptionType subscriptionType=user.getSubscription().getSubscriptionType();
        if(subscriptionType.toString().equalsIgnoreCase("BASIC")){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            int old=500+(200*noOfDevices);
            int New=800+(250*noOfDevices);
            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+(New-old));
            subscriptionRepository.save(subscription);
            return  New-old;
        }
        if(subscriptionType.toString().equalsIgnoreCase("PRO")){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            int old=800+(250*noOfDevices);
            int New=1000+(350*noOfDevices);
            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+(New-old));
            subscriptionRepository.save(subscription);
            return  New-old;
        }
        return -1;

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>list=subscriptionRepository.findAll();
        int revenue=0;
        for(Subscription subscription:list){
            revenue+=subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
