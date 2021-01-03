package com.cb.test.dto;

public class PriceDTO {
    public Float price;
    public String timestamp;

    @Override
    public String toString() {
        return "PriceDTO{" +
                "price=" + price +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
