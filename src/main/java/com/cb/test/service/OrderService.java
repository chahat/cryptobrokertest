package com.cb.test.service;

import com.cb.test.entity.Order;
import com.cb.test.repository.OrderRepository;
import com.cb.test.repository.UserRepository;

import org.hibernate.annotations.Synchronize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Service
public class OrderService implements AppService<Order>{
	
	@PersistenceContext
	private EntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static String USER_UPDATE;
    private static String ORDER_UPDATE;
    private static Boolean orderNgBalanceCheck;
    @Value("${com.cb.test.neg-check}'")
    private void setOrderNgBalanceCheck(String negcheck) {
    	orderNgBalanceCheck = new Boolean(negcheck);
    	String innerQuery = "SELECT   u.id as u_id, ids, cum_sum, amt_sum"
        		+ " FROM  ("
        		+ "			SELECT iduser, group_concat('ID'||id||'ID') OVER w as ids, SUM(amount) OVER w AS amt_sum, SUM(olimit*amount) OVER w AS cum_sum"
        		+ "         FROM   orders"
        		+ "         WHERE olimit >= ?1 AND status = 0"
        		+ "		  	WINDOW w AS(PARTITION BY iduser ORDER BY id ASC)"
        		+ ") t "
        		+ " JOIN user u on u.id=t.iduser " + (orderNgBalanceCheck?"":" WHERE t.cum_sum <= :param_balance ")
        		+ " group by 1 " 
        		+ " having cum_sum = max(cum_sum) ";
    	
    	String usd_balance = orderNgBalanceCheck?"":" ,usd_balance= usd_balance - daily.cum_sum ,usd_balance_prev= usd_balance ";
    	USER_UPDATE = "UPDATE user SET btc_balance = btc_balance + daily.amt_sum  " + usd_balance    
    			+ " FROM ( " + innerQuery.replace(":param_balance", "usd_balance") + " ) AS daily "
				+ " WHERE user.id = daily.u_id;";
    	
    	ORDER_UPDATE = " UPDATE orders SET status=true "
        		+ " FROM ( " + innerQuery.replace(":param_balance", "usd_balance_prev") + "  ) AS daily WHERE daily.ids like '%ID'||id||'ID%' ";
    }
    
    
//    private void setUSER_UPDATE(Boolean negcheck) {
////    	String having= negcheck?"":" having sum(olimit*amount) <= usd_balance ";
////    	String usd_balance = negcheck?"":" ,usd_balance= usd_balance - daily.debit_money ";
////    	USER_UPDATE = "UPDATE user SET btc_balance = btc_balance + daily.amt  " + usd_balance    
////		+ "FROM ("
////		+ "select iduser, sum(olimit*amount) as debit_money, sum(amount) as amt  "
////		+ "from orders "
////		+ "join user u on u.id=iduser  "
////		+ "where olimit >= ?1 and status = 0 "
////		+ "group by iduser "
////		+ having
////		+ ") AS daily WHERE user.id = daily.iduser;";
//    	
//    	
//    }
    
    
//    private void setORDER_UPDATE(Boolean negcheck) {
////    	String having= negcheck?"":" having sum(olimit*amount) <= usd_balance ";
////    	ORDER_UPDATE = "update orders set status=true "
////        		+ "FROM ("
////        		+ "select group_concat('ID'||o.id||'ID') as ids "
////        		+ "from orders o "
////        		+ "join user u on u.id=iduser  "
////        		+ "where olimit >= ?1 and status = 0 "
////        		+ "group by iduser "
////        		+ having
////        		+ ") AS daily WHERE daily.ids like '%ID'||id||'ID%' ";
//    	
//    	
//    	
//    }
    
    private Float currentMinLimitInDB = Float.MIN_VALUE;

    public List<Order> findByStatus(Boolean status) {
        return orderRepository.findByStatus(status);
    }
    
    @Transactional    
    public void updateOrderStatus(Float limit) throws Exception  {
    	try {
	    	Query query = entityManager.createNativeQuery(USER_UPDATE);
	    	query.setParameter(1, limit);
	    	query.executeUpdate();
	    	query = entityManager.createNativeQuery(ORDER_UPDATE);
	    	query.setParameter(1, limit);
	    	query.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw e;
	    }
//    	userRepository.updateBtcBalance(usd_balance, limit, having);
//      orderRepository.updateOrderStatus(limit, having);
    }

   public Order getId(Long id) {
        return orderRepository.findById(id).get();
    }

    public void set(Order item) {
        orderRepository.save(item);
        setCurrentMinLimitInDB(Float.max(currentMinLimitInDB, item.getLimit()));
    }

    public Float getCurrentMinLimitInDB() {
        return currentMinLimitInDB;
    }

    public synchronized void setCurrentMinLimitInDB(Float currentMinLimitInDB) {
        this.currentMinLimitInDB = currentMinLimitInDB;
    }

	public Boolean getOrderNgBalanceCheck() {
		return orderNgBalanceCheck;
	}   
    
}
