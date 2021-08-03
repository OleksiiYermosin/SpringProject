package ua.training.springproject.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total")
    private BigDecimal total;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Calendar date;

    @Column(name = "address_from")
    private String addressFrom;

    @Column(name = "address_to")
    private String addressTo;

    @Column(name = "distance")
    private BigDecimal distance;

    @Column(name = "people")
    private int peopleAmount;

    @ManyToMany
    @JoinTable(
            name = "order_taxi",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "taxi_id"))
    private Set<Taxi> taxi;

    public Object[] getTaxiArray(){
        Taxi[] array = new Taxi[taxi.size()];
        taxi.toArray(array);
        return array;
    }


}
