package ua.training.springproject.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

/**
 * Entity for work with database
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp="[A-Z][a-z]+|[А-ЯЁІЇ][а-яёії']+$",message="Name should begin from capital letter" +
            " and have at least 2 characters")
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Pattern(regexp="[A-Z][a-z]+|[А-ЯЁІЇ][а-яёії']+$",message="Surname should begin from capital letter" +
            " and have at least 2 characters")
    @Column(name = "surname", length = 30, nullable = false)
    private String surname;

    @Pattern(regexp="^[A-Za-z][A-Za-z0-9_-]{3,25}$",message="Username should begin from letter," +
            " can consist of letters, numbers, '-', '_'; size >=4")
    @Column(name = "username", length = 26, nullable = false, unique = true)
    private String username;

    @Pattern(regexp="\\+?[0-9]{12}|[0-9]{10}",message="Use correct phone format")
    @Column(name = "phone", length = 13, nullable = false)
    private String phone;

    @Digits(integer = 5, fraction = 2)
    @Column(name="balance", nullable = false)
    private BigDecimal balance;

    @Digits(integer = 2, fraction = 1)
    @Column(name="discount", nullable = false)
    private BigDecimal discount;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Transient
    private boolean accountNonExpired = true;
    @Transient
    private boolean accountNonLocked = true;
    @Transient
    private boolean credentialsNonExpired = true;
    @Transient
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(getRole());
    }


}

