package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.dto.HotelMinPriceDto;
import com.my_space.airbnb_clone.dto.HotelPriceDto;
import com.my_space.airbnb_clone.dto.HotelSearchRequest;
import com.my_space.airbnb_clone.mapper.HotelMapper;
import com.my_space.airbnb_clone.mapper.HotelPageMapper;
import com.my_space.airbnb_clone.repository.HotelMinPriceRepository;
import com.my_space.airbnb_clone.service.interfaces.IHotelSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinPriceHotelSearchService implements IHotelSearchService {

    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final HotelPageMapper hotelPageMapper;
    private final HotelMapper hotelMapper;

    @Override
    public Page<HotelMinPriceDto> searchAvailableHotels(HotelSearchRequest hotelSearchRequest, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HotelPriceDto> hotelPriceDtos = hotelMinPriceRepository.getAvailableHotels(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                pageable
        );
        return hotelPriceDtos.map(
                hotelPriceDto ->
                        new HotelMinPriceDto(
                                hotelMapper.toHotelResponseDto(hotelPriceDto.getHotel()), hotelPriceDto.getPrice()
                        )
        );
    }
}
