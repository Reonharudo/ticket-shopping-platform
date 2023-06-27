package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CartItemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Cart;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CartRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanSeatService;
import at.ac.tuwien.sepm.groupphase.backend.service.SeatRowService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CartRepository cartRepository;
    private final EventService eventService;
    private final HallPlanSeatService seatService;
    private final SeatRowService seatRowService;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, EventService eventService, HallPlanSeatService seatService, SeatRowService seatRowService) {
        this.cartRepository = cartRepository;
        this.eventService = eventService;
        this.seatService = seatService;
        this.seatRowService = seatRowService;
    }

    @Override
    public void addItemList(List<SeatDto> seatDtoList, Long userId) throws NotFoundException {
        LOGGER.debug("Adding a List of items to the Cart of user {}", userId);
        if (seatDtoList.isEmpty()) {
            throw new NotFoundException();
        }

        for (SeatDto seatDto : seatDtoList) {
            if (!seatService.doesSeatExist(seatDto.getId())) {
                throw new NotFoundException();
            }
        }

        for (SeatDto seatDto : seatDtoList) {
            if (seatService.tryReserveSeat(seatDto.getId())) {
                Cart cart = new Cart(userId, seatDto.getId());
                cartRepository.save(cart);
            }
        }
    }

    @Override
    public List<CartItemDto> getItems(Long userId) {
        LOGGER.debug("Fetching the items of the cart from user {}", userId);
        List<CartItemDto> itemList = new ArrayList<>();

        List<Cart> cartItemList = cartRepository.findByUserId(userId);
        for (Cart cart : cartItemList) {
            HallPlanSeatDto hallPlanSeatDto = seatService.getSeatById(cart.getSeatId());
            SeatRowDto rowDto = seatRowService.getSeatRowById(hallPlanSeatDto.getSeatrowId());
            EventDetailDto eventDto = eventService.getEventFromHallplanId(rowDto.getHallPlanId());
            SeatDto seatDto = new SeatDto(hallPlanSeatDto, rowDto);
            itemList.add(new CartItemDto(seatDto, eventDto, cart.getId()));
        }

        return itemList;
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long userId, boolean freeSeat) {
        LOGGER.debug("Delete item {} out of cart from user {}", itemId, userId);
        Cart cart = cartRepository.findTopBySeatIdAndUserId(itemId, userId);
        if (cart == null) {
            return;
        }
        if (!cart.getUserId().equals(userId)) {
            return;
        }
        if (freeSeat && (!seatService.cancelReservation(itemId))) {
            LOGGER.error("unable to free a seat that was reserved");
        }
        cartRepository.deleteCartById(cart.getId());
    }


    @Override
    public boolean itemBelongsToUserCart(Long itemId, Long userId) {
        LOGGER.debug("Check if item {} belongs to the Cart of user {}", itemId, userId);
        Cart cart = cartRepository.findTopBySeatIdAndUserId(itemId, userId);
        if (cart == null) {
            return false;
        }
        return cart.getUserId().equals(userId);
    }

}
