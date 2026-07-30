package com.my_space.airbnb_clone.strategy;

import com.my_space.airbnb_clone.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
