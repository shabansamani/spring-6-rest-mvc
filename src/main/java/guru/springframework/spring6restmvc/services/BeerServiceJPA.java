package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  private final static int DEFAULT_PAGE_NUMBER = 0;
  private final static int DEFAULT_PAGE_SIZE = 25;

  public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
    if (pageNumber == null || pageNumber <= 0)
      pageNumber = DEFAULT_PAGE_NUMBER;

    if (pageSize == null || pageSize <= 0)
      pageSize = DEFAULT_PAGE_SIZE;

    if (pageSize >= 1000)
      pageSize = 1000;

    Sort sort = Sort.by(Sort.Order.asc("beerName"));

    return PageRequest.of(pageNumber, pageSize, sort);
  }

  @Override
  public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
      Integer pageSize) {

    PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

    Page<Beer> beerPage;

    if (StringUtils.hasText(beerName) && Objects.nonNull(beerStyle)) {
      beerPage = listBeersByNameAndStyle(beerName, beerStyle, pageRequest);
    } else if (StringUtils.hasText(beerName)) {
      beerPage = listBeersByName(beerName, pageRequest);
    } else if (!Objects.isNull(beerStyle)) {
      beerPage = listBeersByStyle(beerStyle, pageRequest);
    } else {
      beerPage = this.beerRepository.findAll(pageRequest);
    }

    if (Objects.nonNull(showInventory) && !showInventory) {
      beerPage.forEach(beer -> beer.setQuantityOnHand(null));
    }

    return beerPage.map(beerMapper::beerToBeerDTO);
  }

  private Page<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle, PageRequest pageRequest) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageRequest);
  }

  private Page<Beer> listBeersByStyle(BeerStyle beerStyle, PageRequest pageRequest) {
    return beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
  }

  public Page<Beer> listBeersByName(String beerName, PageRequest pageRequest) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageRequest);
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
