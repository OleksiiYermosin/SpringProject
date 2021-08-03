package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
