package ua.training.springproject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.springproject.entities.User;
import ua.training.springproject.repositories.RoleRepository;
import ua.training.springproject.services.UserService;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringProjectApplication.class)
public class UserServiceTest {

    private RoleRepository roleRepository;
    private UserService userService;
    private static User testUser;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @BeforeClass
    public static void createUser(){
        testUser = User.builder().name("Name").surname("Surname").username("test").phone("+380555555555")
                .password("$2a$10$FqMqlNj0UT85V/Vqf50VZ./VgG9bFcCc0BgNFK3K8Zg019wFl.uMO")
                .accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).build();
    }

    @Test
    public void testUserSearch() {
        UserDetails user = userService.loadUserByUsername("test");
        Assert.assertNotNull(user);
    }

    @Test
    public void testBalanceGrowth(){
        Long id = ((User) userService.loadUserByUsername("test")).getId();
        boolean result = userService.updateUserBalance(id, BigDecimal.valueOf(5));
        userService.updateUserBalance(id, BigDecimal.valueOf(-5));
        Assert.assertTrue(result);
    }

    @Test
    public void testBalanceDecrease(){
        double growth = 5, decrease = -3;
        BigDecimal initialBalance = changeBalance("test", BigDecimal.valueOf(growth));
        BigDecimal finishBalance = changeBalance("test", BigDecimal.valueOf(decrease));
        changeBalance("test", BigDecimal.valueOf(Math.abs(decrease)));
        Assert.assertEquals(0, finishBalance.compareTo(initialBalance.add(BigDecimal.valueOf(decrease))));
    }

    @Test
    public void testBalancePersistence(){
        Long id = ((User) userService.loadUserByUsername("test")).getId();
        Assert.assertFalse(userService.getMoneyFromUser(BigDecimal.valueOf(10), id));
    }

    @Test
    public void testDiscountChange(){
        Long id = ((User) userService.loadUserByUsername("test")).getId();
        Assert.assertTrue(userService.increaseDiscount(id));
    }

    @Test
    public void testUserSaving() {
        User newUser = User.builder().name("Test").surname("Test").username("test1").phone("+380555555555")
                .password("qwerty")
                .accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).build();
        boolean result = userService.saveUser(newUser);
        Assert.assertTrue(result);
    }

    private BigDecimal changeBalance(String name, BigDecimal value){
        User user = (User) userService.loadUserByUsername(name);
        userService.updateUserBalance(user.getId(), value);
        return ((User) userService.loadUserByUsername(name)).getBalance();
    }

}
