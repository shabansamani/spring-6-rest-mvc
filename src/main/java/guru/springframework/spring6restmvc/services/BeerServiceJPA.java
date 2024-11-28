package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {
  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher applicationEventPublisher;

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 25;

  @Cacheable(cacheNames = "beerListCache")
  @Override
  public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
      Integer pageSize) {

    PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

    Page<Beer> beerPage;

    if (StringUtils.hasText(beerName) && beerStyle == null) {
      beerPage = listBeerByName(beerName, pageRequest);
    } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
      beerPage = listBeerByStyle(beerStyle, pageRequest);
    } else if (StringUtils.hasText(beerName) && beerStyle != null) {
      beerPage = listBeersByNameAndStyle(beerName, beerStyle, pageRequest);
    } else {
      beerPage = beerRepository.findAll(pageRequest);
    }

    if (showInventory != null && !showInventory) {
      beerPage.forEach(beer -> beer.setQuantityOnHand(null));
    }

    return beerPage.map(beerMapper::beerToBeerDTO);
  }

  protected PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
    int queryPageNumber;
    int queryPageSize;

    if (pageNumber != null && pageNumber > 0) {
      queryPageNumber = pageNumber;
    } else {
      queryPageNumber = DEFAULT_PAGE;
    }

    if (pageSize != null) {
      if (pageSize > 1000)
        queryPageSize = 1000;
      else
        queryPageSize = pageSize;
    } else {
      queryPageSize = DEFAULT_PAGE_SIZE;
    }

    Sort sort = Sort.by(Sort.Order.asc("beerName"));

    return PageRequest.of(queryPageNumber, queryPageSize, sort);
  }

  protected Page<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageable);
  }

  protected Page<Beer> listBeerByName(String beerName, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
  }

  protected Page<Beer> listBeerByStyle(BeerStyle beerStyle, Pageable pageable) {
    return beerRepository.findAllByBeerStyle(beerStyle, pageable);
  }

  @Cacheable(cacheNames = "beerCache", key = "#id")
  @Override
  public Optional<BeerDTO> getBeerById(UUID id) {
    return Optional.ofNullable(beerMapper.beerToBeerDTO(beerRepository.findById(id)
        .orElse(null)));
  }

  @Override
  public BeerDTO saveNewBeer(BeerDTO beer) {
    cacheManager.getCache("beerListCache").clear();

    val savedBeer = beerRepository.save(beerMapper.beerDTOToBeer(beer));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    applicationEventPublisher.publishEvent(new BeerCreatedEvent(savedBeer, auth));

    return beerMapper.beerToBeerDTO(savedBeer);
  }

  @Override
  public Optional<BeerDTO> updateBeerById(UUID id, BeerDTO beer) {
    clearCache(id);
    AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>(Optional.empty());
    beerRepository.findById(id).ifPresent(foundBeer -> {
      foundBeer.setBeerName(beer.getBeerName());
      foundBeer.setBeerStyle(beer.getBeerStyle());
      foundBeer.setPrice(beer.getPrice());
      foundBeer.setUpc(beer.getUpc());
      atomicReference.set(Optional.of(beerMapper
          .beerToBeerDTO(beerRepository.save(foundBeer))));
    });
    return atomicReference.get();
  }

  /**
   * //does not work
   * 
   * @Caching(evict = {
   * @CacheEvict(cacheNames = "beerCache", key = "#beerId"),
   * @CacheEvict(cacheNames = "beerListCache")
   *                        })
   */
  @Override
  public Boolean deleteById(UUID beerId) {
    clearCache(beerId);

    if (beerRepository.existsById(beerId)) {
      beerRepository.deleteById(beerId);
      return true;
    }
    return false;
  }

  private void clearCache(UUID beerId) {
    cacheManager.getCache("beerCache").evict(beerId);
    cacheManager.getCache("beerListCache").clear();
  }

  @Override
  public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer) {
    clearCache(beerId);
    AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>(Optional.empty());
    beerRepository.findById(beerId).ifPresent(foundBeer -> {
      if (beer.getBeerName() != null) {
        foundBeer.setBeerName(beer.getBeerName());
      }
      if (beer.getBeerStyle() != null) {
        foundBeer.setBeerStyle(beer.getBeerStyle());
      }
      if (beer.getPrice() != null) {
        foundBeer.setPrice(beer.getPrice());
      }
      if (beer.getUpc() != null) {
        foundBeer.setUpc(beer.getUpc());
      }
      atomicReference.set(Optional.of(beerMapper
          .beerToBeerDTO(beerRepository.save(foundBeer))));
    });
    return atomicReference.get();
  }
}
