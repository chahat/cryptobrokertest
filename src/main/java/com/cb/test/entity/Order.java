package com.cb.test.entity;

import com.cb.test.dto.OrderDTO;
import com.cb.test.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="iduser", nullable=false)
    private User user;

    @Column(name="olimit")
    private Float limit;

    @Column(name="amount")
    private Integer amount;

    @Column(name="status")
    private Boolean status = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Float getLimit() {
        return limit;
    }

    public void setLimit(Float limit) {
        this.limit = limit;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public static Order getOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setLimit(orderDTO.limit);
        order.setAmount(orderDTO.amount);
        User user = new User();
        user.setId(orderDTO.idUser);
        order.setUser(user);
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
