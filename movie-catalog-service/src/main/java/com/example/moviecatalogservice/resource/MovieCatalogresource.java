package com.example.moviecatalogservice.resource;

import com.example.moviecatalogservice.data.CatalogItem;
import com.example.moviecatalogservice.data.Movie;
import com.example.moviecatalogservice.data.Rating;
import com.example.moviecatalogservice.data.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogresource {
    @Autowired
    RestTemplate restTemplate;


    @Autowired
    private WebClient.Builder  webClientBuilder;

    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod="getFallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId)
    {

       UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
        return ratings.getUserRating().stream().map(rating -> {
           Movie movie= restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
     return new CatalogItem(movie.getName(),movie.getDescription(),rating.getRating());
        })
        .collect(Collectors.toList());


    }
    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId)
    {
        return Arrays.asList(new CatalogItem("No Movie","",0));
    }


}




//            Movie movie=  webClientBuilder.build()
//                    .get()
//                    .uri("http://localhost:8082/movies/\"+rating.getMovieId()"+rating.getMovieId())
//                    .retrieve()
//                    .bodyToMono(Movie.class)
//                    .block();

