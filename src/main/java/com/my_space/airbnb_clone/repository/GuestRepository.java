package com.my_space.airbnb_clone.repository;

import com.my_space.airbnb_clone.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}