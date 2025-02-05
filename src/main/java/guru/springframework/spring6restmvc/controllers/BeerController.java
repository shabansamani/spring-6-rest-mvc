package guru.springframework.spring6restmvc.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
public class BeerController {

  public static final String BEER_PATH = "/api/v1/beer";
  public static final String BEER_PATH_ID = BEER_PATH + "/{id}";

  private final BeerService beerService;

  @PatchMapping(BEER_PATH_ID)
  public ResponseEntity<?> patchById(@PathVariable("id") UUID beerId, @RequestBody BeerDTO beer) {
    if (beerService.patchById(beerId, beer).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping(BEER_PATH_ID)
  public ResponseEntity<?> deleteById(@PathVariable("id") UUID beerId) {
    if (!beerService.deleteById(beerId)) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping(BEER_PATH_ID)
  public ResponseEntity<?> updateById(@PathVariable("id") UUID beerId, @Validated @RequestBody BeerDTO beer) {
    if (beerService.updateById(beerId, beer).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping(BEER_PATH)
  public ResponseEntity<?> handlePost(@Validated @RequestBody BeerDTO beer) {
    BeerDTO savedBeer = beerService.saveNewBeer(beer);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", String.format("/api/v1/beer/%s", savedBeer.getId()));
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping(BEER_PATH)
  public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
      @RequestParam(required = false) BeerStyle beerStyle,
      @RequestParam(required = false, defaultValue = "false") Boolean showInventory,
      @RequestParam(required = false) Integer pageNumber,
      @RequestParam(required = false) Integer pageSize) {
    return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
  }

  @GetMapping(BEER_PATH_ID)
  public BeerDTO getBeerById(@PathVariable UUID id) {
    return beerService.getBeerById(id).orElseThrow(NotFoundException::new);
  }

}
