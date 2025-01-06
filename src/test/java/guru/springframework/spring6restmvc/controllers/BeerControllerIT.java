package guru.springframework.spring6restmvc.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
public class BeerControllerIT {

  @Autowired
  BeerController beerController;

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  BeerMapper beerMapper;

  @Test
  void testPatchBeerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.patchById(UUID.randomUUID(), BeerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void patchBeerTest() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
    final String beerName = "New Name";
    beerDTO.setBeerName(beerName);

    ResponseEntity<?> responseEntity = beerController.patchById(beerDTO.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    Beer patchedBeer = beerRepository.findById(beerDTO.getId()).get();
    assertEquals(beerName, patchedBeer.getBeerName());
  }

  @Test
  void deleteBeerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.deleteById(UUID.randomUUID());
    });
  }

  @Rollback
  @Transactional
  @Test
  void deleteBeerTest() {
    Beer beer = beerRepository.findAll().get(0);
    ResponseEntity<?> responseEntity = beerController.deleteById(beer.getId());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
    assertThat(beerRepository.findById(beer.getId())).isEmpty();

  }

  @Test
  void testUpdateBeerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void updateBeerTest() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
    beerDTO.setId(null);
    beerDTO.setVersion(null);
    final String beerName = "UPDATED";
    beerDTO.setBeerName(beerName);

    ResponseEntity<?> responseEntity = beerController.updateById(beer.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    Beer updatedBeer = beerRepository.findById(beer.getId()).get();
    assertThat(updatedBeer).isNotNull();
    assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
  }

  @Test
  void saveNewBeerTest() {
    BeerDTO beerDTO = BeerDTO.builder()
        .beerName("New Beer")
        .build();

    ResponseEntity<?> responseEntity = beerController.handlePost(beerDTO);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
    assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

    String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID savedUUID = UUID.fromString(locationUUID[4]);

    Beer beer = beerRepository.findById(savedUUID).get();
    assertThat(beer).isNotNull();
  }

  @Test
  void testBeerIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      beerController.getBeerById(UUID.randomUUID());
    });
  }

  @Test
  void testGetBeerById() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO savedBeer = beerController.getBeerById(beer.getId());

    assertThat(beer.getId()).isEqualTo(savedBeer.getId());
  }

  @Test
  void testListBeers() {
    List<BeerDTO> dtos = beerController.listBeers();

    assertThat(dtos.size()).isEqualTo(3);
  }

  @Test
  @Transactional
  @Rollback
  void testEmptyList() {
    beerRepository.deleteAll();
    List<BeerDTO> dtos = beerController.listBeers();

    assertThat(dtos.size()).isEqualTo(0);
  }
}
