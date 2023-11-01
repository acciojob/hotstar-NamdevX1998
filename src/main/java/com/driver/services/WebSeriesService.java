package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries webSeries=new WebSeries();
        String seriesname=webSeriesEntryDto.getSeriesName();
        WebSeries webSeries1=webSeriesRepository.findBySeriesName(seriesname);
        if(webSeries1!=null){
            throw new Exception("Series is already present");
        }
        Optional<ProductionHouse> productionHouseOptional=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
        if(productionHouseOptional.isPresent()==false){
            throw new Exception("Invalid Production id");
        }
        ProductionHouse productionHouse=productionHouseOptional.get();

        webSeries.setSeriesName(seriesname);
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(webSeries);

        //set rating of production house
        List<WebSeries>webSeriesList= productionHouse.getWebSeriesList();
        int n=webSeriesList.size();
        double sum=0.0;
        for(WebSeries webSeries2:webSeriesList){
            sum+=webSeries2.getRating();
        }
        productionHouse.setRatings(sum/(n*1.0));

        productionHouseRepository.save(productionHouse);
        return productionHouse.getId();
    }

}
