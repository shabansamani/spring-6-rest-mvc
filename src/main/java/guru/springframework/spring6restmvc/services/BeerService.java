package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import guru.springframework.spring6restmvc.model.BeerDTO;

public interface BeerService {

  List<BeerDTO> listBeers();

  Optional<BeerDTO> getBeerById(UUID id);

  BeerDTO saveNewBeer(BeerDTO beer);

  Optional<BeerDTO> updateById(UUID beerId, BeerDTO beer);

  boolean deleteById(UUID beerId);

  Optional<BeerDTO> patchById(UUID beerId, BeerDTO beer);
}
