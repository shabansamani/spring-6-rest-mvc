package guru.springframework.spring6restmvc.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;

public interface BeerService {

  Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);
  Optional<BeerDTO> getBeerById(UUID id);
  BeerDTO saveNewBeer(BeerDTO beer);
  Optional<BeerDTO> updateBeerById(UUID id, BeerDTO beer);
  Boolean deleteById(UUID beerId);
  Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer);
}
