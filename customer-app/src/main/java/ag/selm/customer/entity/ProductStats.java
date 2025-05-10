package ag.selm.customer.entity;

public record ProductStats (
        Integer productId,
        Integer ratingAverage,
        Integer ratingCount,
        Integer favouriteCount
) {}
