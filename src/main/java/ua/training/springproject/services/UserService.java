package ua.training.springproject.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.springproject.entities.Role;
import ua.training.springproject.entities.User;
import ua.training.springproject.exceptions.NotEnoughMoneyException;
import ua.training.springproject.exceptions.UserNotFoundException;
import ua.training.springproject.repositories.UserRepository;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;

/**
 * Service for processing users
 */
@Service
public class UserService implements UserDetailsService {

    /**
     * User`s repository
     */
    private final UserRepository userRepository;

    /**
     * Password encrypter
     */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * Constructor for dependency injection
     * @param userRepository - user`s repository bean
     * @param bCryptPasswordEncoder - password encoder`s bean
     */
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Method for loading user
     * @param username - user`s username
     * @return user
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws IllegalArgumentException {
        logger.info("Loading user by username");
        return userRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Method for updating user`s balance
     * @param id - user`s id
     * @param value - value
     * @return true if balance was updated
     */
    @Transactional
    public boolean updateUserBalance(long id, BigDecimal value) {
        try {
            User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            user.setBalance(user.getBalance().add(value));
            userRepository.save(user);
        }catch (IllegalArgumentException exception){
            return false;
        }
        return true;
    }

    /**
     * Method for getting money from user
     * @param total - value
     * @param id - user`s id
     * @return true if money were debited
     */
    @Transactional
    public boolean getMoneyFromUser(BigDecimal total, Long id){
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (user.getBalance().compareTo(total)<0){
            throw new NotEnoughMoneyException("You don`t have enough money to make order");
        }
        user.setBalance(user.getBalance().subtract(total));
        userRepository.save(user);
        return true;
    }

    /**
     * Meth for increasing user`s discount
     * @param id - user`s id
     * @return true if discount was increased
     */
    @Transactional
    public boolean increaseDiscount(Long id){
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setDiscount(user.getDiscount().add(BigDecimal.valueOf(MyConstants.DISCOUNT_STEP)));
        if(user.getDiscount().compareTo(BigDecimal.valueOf(30.0))<=0){
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * Method for saving user
     * @param user - user to be saved
     * @return true if user was saved
     */
    @Transactional
    public boolean saveUser(User user) {
        try {
            userRepository.findByUsername(user.getName()).orElseThrow(UserNotFoundException::new);
        }catch (UserNotFoundException ex){
            user.setRole(new Role(1L, "ROLE_USER"));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setBalance(new BigDecimal("0.00"));
            user.setDiscount(new BigDecimal("0.0"));
            userRepository.save(user);
        }
        return true;
    }

}

