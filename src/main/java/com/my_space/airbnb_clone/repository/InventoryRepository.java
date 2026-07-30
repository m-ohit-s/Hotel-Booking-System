package com.my_space.airbnb_clone.repository;

import com.my_space.airbnb_clone.dto.InventoryResponseDto;
import com.my_space.airbnb_clone.dto.UpdateInventoryRequestDto;
import com.my_space.airbnb_clone.entity.Booking;
import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.Inventory;
import com.my_space.airbnb_clone.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);

    @Query("""
            SELECT DISTINCT i.hotel FROM Inventory i
            WHERE i.city = :city
                AND i.closed = false
                AND i.date BETWEEN :startDate AND :endDate
                AND i.totalCount - (i.bookedCount + i.reservedCount) >= :roomsCount
            GROUP BY i.hotel, i.room
            HAVING COUNT(i.date) = :daysCount
            """)
    Page<Hotel> getAvailableHotels(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("daysCount") Long daysCount,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.closed = false
                AND i.date BETWEEN :startDate AND :endDate
                AND i.totalCount - (i.bookedCount + i.reservedCount) >= :roomsCount
            """)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i
            WHERE i.room.id=:roomId
                AND i.closed=false
                AND i.date BETWEEN :startDate AND :endDate
                AND i.totalCount - i.bookedCount >= :roomsCount
            """)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
            UPDATE Inventory i SET i.reservedCount = i.reservedCount + :roomsCount
            WHERE i.closed = false
                AND i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.totalCount - (i.reservedCount + i.bookedCount) >= :roomsCount
            """)
    void initBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
            UPDATE Inventory i SET
            i.bookedCount=i.bookedCount + :roomsCount,
            i.reservedCount=i.reservedCount-:roomsCount
            WHERE i.room.id=:roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.totalCount - i.bookedCount >= :roomsCount
                AND i.reservedCount >= :roomsCount
                AND i.closed = false
            """)
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("roomsCount") Integer roomsCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate dateStart, LocalDate dateEnd);


    @Modifying
    @Query("""
            UPDATE Inventory i SET i.bookedCount=i.bookedCount-:roomsCount
                WHERE i.room.id=:roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.totalCount - i.bookedCount >= :roomsCount
                AND i.closed = false
            """)
    void cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("roomsCount") Integer roomsCount
    );

    List<Inventory> findByRoomAndClosedFalse(Room room);

    List<Inventory> findByRoomAndClosedFalseOrderByDate(Room room);

    @Modifying
    @Query("""
                UPDATE Inventory i SET i.price = :basePrice * i.surgeFactor
                WHERE i.room.id = :roomId
            """)
    void updateInventoryOfUpdatedRoom(
            @Param("roomId") Long roomId,
            @Param("basePrice") BigDecimal newBasePrice
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i
            WHERE i.room.id = :roomId
            AND i.date BETWEEN :startDate AND :endDate
            """)
    List<Inventory> lockInventoryBeforeUpdate(
            @Param("roomId") Long roomId,
            @Param("surgeFactor") BigDecimal surgeFactor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("closed") Boolean closed
    );

    @Modifying
    @Query("""
            UPDATE Inventory i SET i.surgeFactor = :surgeFactor, price = i.price * :surgeFactor, closed=:closed
            WHERE i.room.id = :roomId AND i.date BETWEEN :startDate AND :endDate
            """)
    void updateInventoryForAdmin(
            @Param("roomId") Long roomId,
            @Param("surgeFactor") BigDecimal surgeFactor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("closed") Boolean closed
    );
}
