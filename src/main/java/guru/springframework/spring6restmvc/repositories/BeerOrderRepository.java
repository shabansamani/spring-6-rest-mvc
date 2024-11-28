package guru.springframework.spring6restmvc.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.springframework.spring6restmvc.entities.BeerOrder;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
    
}
