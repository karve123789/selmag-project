package ag.selm.recommendationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_rating", schema = "rating")
public class ProductRating {

    @Id
    @Column( name = "product_id")
    private Integer  productId;

    @Column( name = "rating_average")
    private Integer ratingAverage;

    @Column( name = "rating_count")
    private Integer ratingCount;
}

