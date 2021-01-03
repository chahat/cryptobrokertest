package com.cb.test.service;

import com.cb.test.entity.User;
import com.cb.test.repository.OrderRepository;
import com.cb.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService implements AppService<User>{

    @Autowired
    private UserRepository userRepository;

    public List<User> findByName(String name)  {
        return userRepository.findByName(name);
    }
    
    public User getId(Long id) {
        return userRepository.findById(id).get();
    }

    
    public void set(User item) {
        userRepository.saveAndFlush(item);
    }
}
