package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.entity.Booking;
import com.stripe.exception.StripeException;

public interface ICheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
