package ag.selm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewProductEvent {

    private Integer productId;
    private String title;
    private String details;

    @JsonCreator
    public NewProductEvent(@JsonProperty("productId") Integer productId,
                           @JsonProperty("title") String title,
                           @JsonProperty("details") String details) {
        this.productId = productId;
        this.title = title;
        this.details = details;
    }
}