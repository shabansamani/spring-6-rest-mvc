package guru.springframework.spring6restmvc.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.g;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
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

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext webApplicationContext;

  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void testListBeersByStyleAndNameShowInventoryTruePage2() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("showInventory", "true")
        .queryParam("pageNumber", "2")
        .queryParam("pageSize", "50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(50)))
        .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));

  }

  @Test
  void testListBeersByStyleAndNameShowInventoryFalse() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("showInventory", "FALSE")
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(310)))
        .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()));

  }

  @Test
  void testListBeersByStyleAndNameShowInventoryTrue() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("showInventory", "TRUE")
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(310)))
        .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));

  }

  @Test
  void testListBeersByStyleAndName() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(310)));
  }

  @Test
  void testListBeersByName() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .queryParam("beerName", "IPA")
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(336)));
  }

  @Test
  void testListBeersByStyle() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(548)));
  }

  @Test
  void testPatchBeerWithBadName() throws Exception {
    Beer beer = beerRepository.findAll().get(0);

    Map<String, Object> beerMap = new HashMap<>();
    beerMap.put("beerName", "New Name 999999999999999999999999999999999999999999999999999999");

    mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(beerMap)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(1)))
        .andReturn();
  }

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

  @Rollback
  @Transactional
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
    Page<BeerDTO> dtos = beerController.listBeers(null, null, null, 1, 50);

    assertThat(dtos.getContent().size()).isEqualTo(50);
  }

  @Test
  @Transactional
  @Rollback
  void testEmptyList() {
    beerRepository.deleteAll();
    Page<BeerDTO> dtos = beerController.listBeers(null, null, null, 1, 50);

    assertThat(dtos.getContent().size()).isEqualTo(0);
  }
}
