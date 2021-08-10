package ua.training.springproject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.springproject.entities.Role;
import ua.training.springproject.entities.User;
import ua.training.springproject.repositories.RoleRepository;
import ua.training.springproject.services.UserService;

import java.math.BigDecimal;
import java.util.Objects;

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
                .password("123").accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).build();
    }

    @Before
    public void insertData(){
        roleRepository.save(new Role(1L, "ROLE_USER"));
        roleRepository.save(new Role(2L, "ROLE_ADMIN"));
        userService.saveUser(testUser);
    }

    @Test
    public void testUserSearch() {
        UserDetails user = userService.loadUserByUsername("test");
        Assert.assertTrue(Objects.deepEquals(user, testUser));
    }

    @Test
    public void testBalanceGrowth(){
        Long id = ((User) userService.loadUserByUsername("test")).getId();
        Assert.assertTrue(userService.updateUserBalance(id, BigDecimal.valueOf(5)));
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
        boolean result = userService.saveUser(testUser);
        Assert.assertTrue(result);
    }

    private BigDecimal changeBalance(String name, BigDecimal value){
        User user = (User) userService.loadUserByUsername(name);
        userService.updateUserBalance(user.getId(), value);
        return ((User) userService.loadUserByUsername(name)).getBalance();
    }

}
