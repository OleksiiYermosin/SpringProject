package ua.training.springproject.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ua.training.springproject.entities.Order;
import ua.training.springproject.entities.User;

public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {

    Page<Order> findAllByUser(User user, Pageable pageable);

}
