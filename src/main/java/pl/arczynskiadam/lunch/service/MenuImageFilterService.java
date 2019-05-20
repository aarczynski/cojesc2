package pl.arczynskiadam.lunch.service;

import org.springframework.stereotype.Service;
import pl.arczynskiadam.lunch.http.client.facebook.FacebookApiClient;
import pl.arczynskiadam.lunch.http.client.facebook.dto.album.Album;
import pl.arczynskiadam.lunch.http.client.facebook.dto.album.Image;
import pl.arczynskiadam.lunch.http.client.facebook.dto.album.ImageGroup;
import pl.arczynskiadam.lunch.ocr.client.googlecloudvision.OcrService;
import pl.arczynskiadam.lunch.restaurant.Restaurant;
import pl.arczynskiadam.lunch.restaurant.RestaurantsProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.function.Predicate;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

@Service
public class MenuImageFilterService {
    private FacebookApiClient facebookClient;
    private RestaurantsProperties restaurantsProperties;
    private OcrService ocrService;

    public MenuImageFilterService(FacebookApiClient facebookClient, RestaurantsProperties restaurantsProperties, OcrService ocrService) {
        this.facebookClient = facebookClient;
        this.restaurantsProperties = restaurantsProperties;
        this.ocrService = ocrService;
    }

    public String getLunchMenuImageLink(Restaurant restaurant) {
        var restaurantProperties = restaurantsProperties.getForRestaurant(restaurant);
        var album = facebookClient.getAlbum(restaurantProperties.getFacebookAlbumId());
        return findNewestLunchMenuImageLink(album, restaurant);
    }

    private String findNewestLunchMenuImageLink(Album album, Restaurant restaurant) {
        return album.getData().stream()
                .filter(after(expectedMenuPublishDate(restaurant)))
                .map(ImageGroup::getBiggest)
                .filter(isMenuImage(restaurant))
                .findFirst()
                .get()
                .getSource();
    }

    private Predicate<Image> isMenuImage(Restaurant restaurant) {
        return img -> {
            try {
                return isMenuImg(new URL(img.getSource()), restaurant);
            } catch (MalformedURLException e) {
                throw new RuntimeException(img.getSource() + " is incorrect url");
            }
        };
    }

    private Predicate<ImageGroup> after(ZonedDateTime time) {
        return imgGr -> imgGr.getCreatedTime().isAfter(time);
    }

    private boolean isMenuImg(URL imgUrl, Restaurant restaurant) {
        return ocrService.imageContainsKeywords(imgUrl, restaurantsProperties.getForRestaurant(restaurant).getMenuKeyWords());
    }

    private ZonedDateTime expectedMenuPublishDate(Restaurant restaurant) {
        Duration menuValidTime = Duration.ofDays(restaurantsProperties.getForRestaurant(restaurant).getMenuValidity());
        return ZonedDateTime.now().truncatedTo(DAYS).plus(18, HOURS).minus(menuValidTime);
    }
}