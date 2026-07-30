package com.my_space.airbnb_clone.repository;

import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel(Hotel hotel);

    long deleteByHotel(Hotel hotel);
}
