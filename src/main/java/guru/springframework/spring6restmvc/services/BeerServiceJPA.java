package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ch.qos.logback.core.util.StringUtil;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public List<BeerDTO> listBeers() {
    return this.beerRepository.findAll()
        .stream()
        .map(beerMapper::beerToBeerDTO)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    return Optional.ofNullable(beerMapper.beerToBeerDTO(beerRepository.findById(id).orElse(null)));
  }

  @Override
  public BeerDTO saveNewBeer(BeerDTO beer) {
    return beerMapper.beerToBeerDTO(beerRepository.save(beerMapper.beerDtoToBeer(beer)));
  }

  @Override
  public Optional<BeerDTO> updateById(UUID beerId, BeerDTO beer) {
    AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>(Optional.empty());

    beerRepository.findById(beerId).ifPresentOrElse(beerEntity -> {
      beerEntity.setBeerName(beer.getBeerName());
      beerEntity.setBeerStyle(beer.getBeerStyle());
      beerEntity.setPrice(beer.getPrice());
      beerEntity.setUpc(beer.getUpc());
      beerRepository.save(beerEntity);
      atomicReference.set(Optional.of(beerMapper.beerToBeerDTO(beerEntity)));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

  @Override
  public boolean deleteById(UUID beerId) {
    if (beerRepository.existsById(beerId)) {
      beerRepository.deleteById(beerId);
      return true;
    }
    return false;
  }

  @Override
  public Optional<BeerDTO> patchById(UUID beerId, BeerDTO beer) {
    AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

    beerRepository.findById(beerId).ifPresentOrElse(existing -> {
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
      atomicReference.set(Optional.of(beerMapper.beerToBeerDTO(beerRepository.save(existing))));

    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

}
