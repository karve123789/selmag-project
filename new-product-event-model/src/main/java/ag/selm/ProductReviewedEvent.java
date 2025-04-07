package ag.selm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductReviewedEvent {
    private Integer productId;
    private String userId;
    private Integer rating;

    @JsonCreator //Оставьте только эту аннотацию и убедитесь, что имена параметров соответствуют JSON
    public ProductReviewedEvent(@JsonProperty("productId") Integer productId,
                                @JsonProperty("userId") String userId,
                                @JsonProperty("rating") Integer rating) {
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;

    }
}


