package com.my_space.airbnb_clone.repository;

import com.my_space.airbnb_clone.dto.HotelPriceDto;
import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {
    @Query("""
            SELECT new com.my_space.airbnb_clone.dto.HotelPriceDto(i.hotel, AVG(i.price)) FROM HotelMinPrice i
            WHERE i.hotel.city = :city
                AND i.hotel.active = true
                AND i.date BETWEEN :startDate AND :endDate
            GROUP BY i.hotel
            """)
    Page<HotelPriceDto> getAvailableHotels(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
