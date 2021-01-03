package com.cb.test.repository;

import com.cb.test.entity.Order;
import com.cb.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Order,Long> {
    List<Order> findByStatus(Boolean status);

    @Modifying
    //@Query(value = "update orders set status=true where olimit >= ?", nativeQuery = true)
    @Query(value = "update orders set status=true "
    		+ "FROM ("
    		+ "select group_concat('ID'||o.id||'ID') as ids "
    		+ "from orders o "
    		+ "join user u on u.id=iduser  "
    		+ "where olimit >= ? and status = 0 "
    		+ "group by iduser "
    		+ " ? "
    		+ ") AS daily WHERE daily.ids like '%ID'||id||'ID%' ", nativeQuery = true)
    void updateOrderStatus(Float price, String having);
}
