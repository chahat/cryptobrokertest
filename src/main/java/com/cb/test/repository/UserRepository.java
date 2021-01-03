package com.cb.test.repository;

import com.cb.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByName(String name);
    
    @Modifying
//    @Query(value = "UPDATE user SET btc_balance = btc_balance + daily.amt  FROM (SELECT sum(amount) AS amt, iduser FROM orders WHERE olimit >= ? GROUP BY 2) AS daily WHERE user.id = daily.iduser;", nativeQuery = true)
    @Query(value = "UPDATE user SET btc_balance = btc_balance + daily.amt, usd_balance=?  "
    		+ "FROM ("
    		+ "select iduser, sum(olimit*amount) as debit_money, sum(amount) as amt  "
    		+ "from orders "
    		+ "join user u on u.id=iduser  "
    		+ "where olimit >= ? and status = 0 "
    		+ "group by iduser "
    		+ " ? "
    		+ ") AS daily WHERE user.id = daily.iduser;", nativeQuery = true)
    void updateBtcBalance(String usd_balance, Float limit, String having);
}
