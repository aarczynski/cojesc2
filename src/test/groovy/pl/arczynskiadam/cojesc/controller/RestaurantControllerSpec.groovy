package pl.arczynskiadam.cojesc.controller

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.arczynskiadam.cojesc.restaurant.Restaurant
import pl.arczynskiadam.cojesc.restaurant.Restaurants
import pl.arczynskiadam.cojesc.service.MenuService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ RestaurantController ])
class RestaurantControllerSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @SpringBean
    private MenuService menuService = Mock()

    @SpringBean
    private Restaurants restaurants = Mock()

    void setup() {
        restaurants.getAll() >> [
                new Restaurant(id: 'test-restaurant-1'),
                new Restaurant(id: 'test-restaurant-2')
        ]
    }

    def "should return lunch menu url"() {
        given:
        def testRestaurant = 'test-restaurant'
        menuService.findLunchMenu(_) >> Optional.of('<img src="https://some.image.url"/>')

        when:
        def result = mockMvc.perform(
                get("/restaurants/$testRestaurant/lunch")
                        .header("Accept", MediaType.TEXT_PLAIN)
        )

        then:
        result
                .andExpect(status().isOk())
                .andExpect(content().string('<img src="https://some.image.url"/>'))
    }

    def "should return 404 when lunch menu not found"() {
        given:
        def testRestaurant = 'test-restaurant'
        menuService.findLunchMenu(_) >> Optional.empty()

        when:
        def result = mockMvc.perform(
                get("/restaurants/$testRestaurant/lunch")
                        .header("Accept", MediaType.TEXT_PLAIN)
        )

        then:
        result
                .andExpect(status().isNotFound())
    }

    def "should return all restaurant ids"() {
        when:
        def result = mockMvc.perform(
                get("/restaurants")
        )

        then:
        result
                .andExpect(status().isOk())
                .andExpect(content().string('[{"id":"test-restaurant-1"},{"id":"test-restaurant-2"}]'))
    }
}
