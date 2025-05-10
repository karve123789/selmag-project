package ag.selm.feedback.event;

import ag.selm.feedback.entity.ProductReview;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProductReviewCreatedEvent extends ApplicationEvent {

    private final ProductReview productReview;


    public ProductReviewCreatedEvent(Object source, ProductReview productReview) {
        super(source);
        this.productReview = productReview;
    }
}