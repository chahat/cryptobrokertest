package com.cb.test.controller;

import com.cb.test.dto.OrderDTO;
import com.cb.test.dto.UserDTO;
import com.cb.test.entity.Order;
import com.cb.test.entity.User;
import com.cb.test.service.OrderService;
import com.cb.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AppController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/api/user/{userId}")
    public User fetchAccountDetails(@PathVariable(name="userId")Long userId) {
        User user = userService.getId(userId);
        user.getOrders().size();
        return user;
    }

    @GetMapping("/api/order/{orderId}")
    public Order fetchOrderDetails(@PathVariable(name="orderId")Long orderId) {
        return orderService.getId(orderId);
    }

    @PostMapping("/api/user")
    public Long createAccount(@RequestBody UserDTO userDTO){
        //public Long createAccount(@RequestBody Map<String, Object> payload){
        //String name = payload.get("name").toString();
        //Float balance = Float.valueOf(payload.get("balance").toString());
        User user = User.getUser(userDTO);
        if(!userService.findByName(user.getName()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use");
        }
        if(user.getUsdBalance() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Balance cannot be negative");
        }
        //User user = new User();
        //user.setName(name);
        //user.setBalance(balance);
        userService.set(user);

        System.out.println("User Saved Successfully");
        return user.getId();
    }

    @PostMapping("/api/order")
    public Order createLimitOrder(@RequestBody OrderDTO orderDTO){
        Order order = Order.getOrder(orderDTO);
        User user = userService.getId(order.getUser().getId());
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserID not found");
        } else {
            order.setUser(user);
            if (order.getAmount() <= 0 || order.getLimit() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount or limit must be positive");
            }
            if(orderService.getOrderNgBalanceCheck())
            {
            	Float newBalance = user.getUsdBalance() - order.getAmount()*order.getLimit();
                if (newBalance < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You do not have sufficient funds to place this order");
                }
                order.getUser().setUsdBalance(newBalance);
            }            
            orderService.set(order);
            System.out.println("Order Saved Successfully");
            return order;
        }
    }
}
