package com.cb.test;

import com.cb.test.dto.PriceDTO;
import com.cb.test.entity.Order;
import com.cb.test.service.OrderService;
import com.cb.test.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SpringBootApplication
@EnableSwagger2
@EnableScheduling
public class TestApplication {

	@Autowired
	private OrderService orderService;

	@Autowired
	private RestService restService;

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@Scheduled(fixedRateString = "${com.cb.test.btc-tick-rate}") //fixedRate = 1000
	public void scheduleFixedRateTask() {
		PriceDTO priceDTO = restService.getPostsPlainJSON();
		if(priceDTO != null && priceDTO.price <= orderService.getCurrentMinLimitInDB()) {
			try {
				orderService.updateOrderStatus(priceDTO.price);
				// can lead to race condition while setting currentMinLimitInDB here , hence using synchronized
				orderService.setCurrentMinLimitInDB(Float.MIN_VALUE);
				System.out.println("Updated limits");
			} catch (Exception ex) {
				
			}
		}
	}

}
