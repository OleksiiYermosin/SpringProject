package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.OrderStatus;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    Optional<OrderStatus> findByName(String name);
}
