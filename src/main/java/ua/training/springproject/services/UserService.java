package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.training.springproject.entities.Role;
import ua.training.springproject.entities.User;
import ua.training.springproject.repositories.UserRepository;

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

    public void updateUserBalance(long id, BigDecimal value) {
        User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        user.setBalance(user.getBalance().add(value));
        userRepository.save(user);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
    }

    public boolean saveUser(User user) {
        try {
            User userFromDB = userRepository.findByUsername(user.getName()).orElseThrow(IllegalArgumentException::new);
            return false;
        }catch (IllegalArgumentException ex){
            user.setRole(new Role(1L, "ROLE_USER"));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setBalance(new BigDecimal("0.0"));
            user.setDiscount(new BigDecimal("0.0"));
            userRepository.save(user);
        }
        return true;
    }

}

