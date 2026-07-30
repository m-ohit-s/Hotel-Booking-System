package com.my_space.airbnb_clone.repository;

import com.my_space.airbnb_clone.entity.Booking;
import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String paymentSessionId);

    Page<Booking> findByHotel(Hotel hotel, Pageable pageable);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, Instant createdAtStart, Instant createdAtEnd);

    List<Booking> findByHotelAndCreatedAtAfter(Hotel hotel, Instant createdAt);

    List<Booking> findByHotelAndCreatedAtBefore(Hotel hotel, Instant createdAt);

    List<Booking> findByUser(User user);
}
