package com.my_space.airbnb_clone.service.impls;

import com.my_space.airbnb_clone.entity.Hotel;
import com.my_space.airbnb_clone.entity.HotelMinPrice;
import com.my_space.airbnb_clone.entity.Inventory;
import com.my_space.airbnb_clone.repository.HotelMinPriceRepository;
import com.my_space.airbnb_clone.repository.HotelRepository;
import com.my_space.airbnb_clone.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {

    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final PricingService pricingService;


    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
        int page = 0;
        int batchSize = 100;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if (hotelPage.isEmpty()) {
                break;
            }
            hotelPage.getContent().forEach(this::updateHotelPrices);
            page ++;
        }
    }

    private void updateHotelPrices(Hotel hotel) {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusYears(1);

        List<Inventory> inventories = inventoryRepository.findByHotelAndDateBetween(hotel, start, end);
        updateInventoryPrices(inventories);
        updateHotelMinPrice(hotel, inventories);
    }

    private void updateInventoryPrices(List<Inventory> inventories) {
        inventories.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAllAndFlush(inventories);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventories) {
        Map<LocalDate, BigDecimal> dailyMinPrices = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                )).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMinPrice> hotelMinPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date).orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setPrice(price);
            hotelMinPrices.add(hotelMinPrice);
        });

        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }
}
