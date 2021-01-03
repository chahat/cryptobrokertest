package com.cb.test.entity;

import com.cb.test.dto.UserDTO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="name", unique=true, nullable=false)
    private String name;

    @Column(name="usd_balance")
    private Float usdBalance;
    
    @Column(name="usd_balance_prev")
    private Float usdBalancePrev;

    @Column(name="btc_balance")
    private Integer btcBalance = 0;
    // there should be a margin variable instead I assume balance to take care of margin

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Set<Order> Orders = new HashSet<Order>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Order> getOrders() {
        return Orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getUsdBalance() {
        return usdBalance;
    }

    public void setUsdBalance(Float balance) {
        this.usdBalance = balance;
    }

    public Integer getBtcBalance() {
        return btcBalance;
    }

    public void setBtcBalance(Integer btcBalance) {
        this.btcBalance = btcBalance;
    }

    public static User getUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.name);
        user.setUsdBalance(userDTO.balance);
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
