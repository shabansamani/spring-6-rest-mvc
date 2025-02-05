package guru.springframework.spring6restmvc.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
  Page<Beer> findAllByBeerNameIsLikeIgnoreCase(String beerName, Pageable pageable);

  Page<Beer> findAllByBeerStyle(BeerStyle beerStyle, Pageable pageable);

  Page<Beer> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(String beerName, BeerStyle beerStyle, Pageable pageable);
}
