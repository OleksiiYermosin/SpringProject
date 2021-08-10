package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.springproject.entities.Role;
import ua.training.springproject.entities.User;
import ua.training.springproject.repositories.UserRepository;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws IllegalArgumentException {
        return userRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public boolean updateUserBalance(long id, BigDecimal value) {
        try {
            User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
            user.setBalance(user.getBalance().add(value));
            userRepository.save(user);
        }catch (IllegalArgumentException exception){
            return false;
        }
        return true;
    }

    @Transactional
    public boolean getMoneyFromUser(BigDecimal total, Long id){
        User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if (user.getBalance().compareTo(total)==-1){
            return false;
        }
        user.setBalance(user.getBalance().subtract(total));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean increaseDiscount(Long id){
        User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        user.setDiscount(user.getDiscount().add(BigDecimal.valueOf(MyConstants.DISCOUNT_STEP)));
        if(user.getDiscount().compareTo(BigDecimal.valueOf(30.0))<=0){
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean saveUser(User user) {
        try {
            userRepository.findByUsername(user.getName()).orElseThrow(IllegalArgumentException::new);
            return false;
        }catch (IllegalArgumentException ex){
            user.setRole(new Role(1L, "ROLE_USER"));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setBalance(new BigDecimal("0.00"));
            user.setDiscount(new BigDecimal("0.0"));
            userRepository.save(user);
        }
        return true;
    }

}

