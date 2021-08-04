package ua.training.springproject.entities;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

@Data
@DynamicUpdate
@Entity
@Table(name = "taxi")
public class Taxi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "info")
    private String info;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "taxi_class_id", nullable = false)
    private TaxiClass taxiClass;

    @Column(name = "capacity")
    private int capacity;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "taxi_status_id", nullable = false)
    private TaxiStatus taxiStatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToMany(mappedBy = "taxi")
    private Set<Order> orders;

}
