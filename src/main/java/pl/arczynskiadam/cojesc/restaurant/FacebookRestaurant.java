package pl.arczynskiadam.cojesc.restaurant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class FacebookRestaurant extends Restaurant {
    private String facebookId;
    private String[] menuKeyWords;
    private String cssClass;
}
