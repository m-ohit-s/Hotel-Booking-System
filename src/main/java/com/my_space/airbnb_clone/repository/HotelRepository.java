package com.my_space.airbnb_clone.repository;

import com.my_space.airbnb_clone.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByIdAndActiveTrue(Long id);

    Page<Hotel> findByActiveTrue(Pageable pageable);

    Page<Hotel> findByActiveFalse(Pageable pageable);
}
