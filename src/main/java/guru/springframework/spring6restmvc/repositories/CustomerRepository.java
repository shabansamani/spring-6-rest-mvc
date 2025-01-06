package guru.springframework.spring6restmvc.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.springframework.spring6restmvc.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

}
