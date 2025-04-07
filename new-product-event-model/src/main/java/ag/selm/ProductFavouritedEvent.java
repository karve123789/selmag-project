package ag.selm;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductFavouritedEvent {
    private Integer productId;
    private String userId;

    @JsonCreator
    public ProductFavouritedEvent(@JsonProperty("productId") Integer productId,
                                  @JsonProperty("userId") String userId){
        this.productId = productId;
        this.userId = userId;
    }

}


