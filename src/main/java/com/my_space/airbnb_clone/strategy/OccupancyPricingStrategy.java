package com.my_space.airbnb_clone.strategy;

import com.my_space.airbnb_clone.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
        BigDecimal price = wrapped.calculatePrice(inventory);
        if (occupancyRate > 0.8) {
            return price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
