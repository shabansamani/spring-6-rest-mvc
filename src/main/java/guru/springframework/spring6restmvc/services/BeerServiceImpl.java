package guru.springframework.spring6restmvc.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

  private Map<UUID, BeerDTO> beerMap;

  public BeerServiceImpl() {
    this.beerMap = new HashMap<>();

    BeerDTO beerOne = BeerDTO.builder()
        .id(UUID.randomUUID())
        .version(1)
        .beerName("Galaxy Cat")
        .beerStyle(BeerStyle.PALE_ALE)
        .upc("123456")
        .price(new BigDecimal("12.99"))
        .quantityOnHand(122)
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();

    BeerDTO beerTwo = BeerDTO.builder()
        .id(UUID.randomUUID())
        .version(1)
        .beerName("Crank")
        .beerStyle(BeerStyle.PALE_ALE)
        .upc("123456222")
        .price(new BigDecimal("11.99"))
        .quantityOnHand(392)
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();

    BeerDTO beerThree = BeerDTO.builder()
        .id(UUID.randomUUID())
        .version(1)
        .beerName("Sunshine City")
        .beerStyle(BeerStyle.IPA)
        .upc("123456789")
        .price(new BigDecimal("11.99"))
        .quantityOnHand(553)
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();

    this.beerMap.put(beerOne.getId(), beerOne);
    this.beerMap.put(beerTwo.getId(), beerTwo);
    this.beerMap.put(beerThree.getId(), beerThree);
  }

  @Override
  public List<BeerDTO> listBeers() {
    return new ArrayList<>(beerMap.values());
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    log.debug("getBeerById -- in BeerServiceImpl");

    return Optional.of(beerMap.get(id));
  }

  @Override
  public BeerDTO saveNewBeer(BeerDTO beer) {
    BeerDTO savedBeer = BeerDTO.builder()
        .id(UUID.randomUUID())
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .beerName(beer.getBeerName())
        .beerStyle(beer.getBeerStyle())
        .upc(beer.getUpc())
        .quantityOnHand(beer.getQuantityOnHand())
        .price(beer.getPrice())
        .version(1)
        .build();

    beerMap.put(savedBeer.getId(), savedBeer);

    return savedBeer;
  }

  @Override
  public Optional<BeerDTO> updateById(UUID beerId, BeerDTO beer) {
    BeerDTO existing = beerMap.get(beerId);
    existing.setBeerName(beer.getBeerName());
    existing.setBeerStyle(beer.getBeerStyle());
    existing.setPrice(beer.getPrice());
    existing.setUpc(beer.getUpc());
    existing.setQuantityOnHand(beer.getQuantityOnHand());
    existing.setUpdatedDate(LocalDateTime.now());

    beerMap.put(beerId, existing);

    return Optional.of(existing);
  }

  @Override
  public boolean deleteById(UUID beerId) {
    beerMap.remove(beerId);

    return true;
  }

  @Override
  public Optional<BeerDTO> patchById(UUID beerId, BeerDTO beer) {
    BeerDTO existing = beerMap.get(beerId);
    if (StringUtils.hasText(beer.getBeerName())) {
      existing.setBeerName(beer.getBeerName());
    }
    if (beer.getBeerStyle() != null) {
      existing.setBeerStyle(beer.getBeerStyle());
    }
    if (beer.getPrice() != null) {
      existing.setPrice(beer.getPrice());
    }
    if (beer.getQuantityOnHand() != null) {
      existing.setQuantityOnHand(beer.getQuantityOnHand());
    }
    if (StringUtils.hasText(beer.getUpc())) {
      existing.setUpc(beer.getUpc());
    }

    beerMap.put(beerId, existing);

    return Optional.of(existing);
  }

}
