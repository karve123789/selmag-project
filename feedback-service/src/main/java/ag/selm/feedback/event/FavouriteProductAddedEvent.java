package ag.selm.feedback.event;

import ag.selm.feedback.entity.FavouriteProduct;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FavouriteProductAddedEvent extends ApplicationEvent {

    private final FavouriteProduct favouriteProduct;

    public FavouriteProductAddedEvent(Object source, FavouriteProduct favouriteProduct) {
        super(source);
        this.favouriteProduct = favouriteProduct;
    }
}