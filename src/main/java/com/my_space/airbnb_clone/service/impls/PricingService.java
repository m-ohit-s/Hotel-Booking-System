package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.entity.Inventory;
import com.my_space.airbnb_clone.strategy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {
    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        // adding strategies
        pricingStrategy = new SurgePriceStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    public BigDecimal totalPriceForOneRoom(List<Inventory> inventories) {
        return inventories.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalPrice(List<Inventory> inventories, int roomsCount) {
        return totalPriceForOneRoom(inventories).multiply(BigDecimal.valueOf(roomsCount));
    }
}
