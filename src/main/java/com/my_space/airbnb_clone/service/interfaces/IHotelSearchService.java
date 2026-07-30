package com.my_space.airbnb_clone.service.interfaces;

import com.my_space.airbnb_clone.dto.HotelMinPriceDto;
import com.my_space.airbnb_clone.dto.HotelSearchRequest;
import org.springframework.data.domain.Page;

public interface IHotelSearchService {
    Page<HotelMinPriceDto> searchAvailableHotels(HotelSearchRequest hotelSearchRequest, Integer page, Integer size);
}
