package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import jakarta.transaction.Transactional;
import lombok.val;

@SpringBootTest
class BeerControllerIT {

  @Autowired
  BeerController beerController;

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  BeerMapper beerMapper;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext wac;

  MockMvc mockMvc;

  static String username = "user1";

  static String password = "password";

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        .apply(springSecurity())
        .build();
  }

  @Test
  void testCreateBeerMVC() throws Exception {
    val beerDTO = BeerDTO.builder()
        .beerName("New Beer")
        .beerStyle(BeerStyle.IPA)
        .upc("123123")
        .price(BigDecimal.TEN)
        .quantityOnHand(5)
        .build();

    mockMvc.perform(post(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(beerDTO)))
        .andExpect(status().isCreated())
        .andReturn();
  }

  @Test
  void testUnauthorizedRequest() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH))
        .andExpect(status().isUnauthorized());

  }

  @Test
  void tesListBeersByStyleAndNameShowInventoryTruePage2() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
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
  void testListBeersByStyleAndNameShowInventoryTrue() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("showInventory", "true")
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(310)))
        .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
  }

  @Test
  void testListBeersByStyleAndNameShowInventoryFalse() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("showInventory", "false")
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(310)))
        .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()));
  }

  @Test
  void testListBeersByStyleAndName() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .queryParam("beerName", "IPA")
        .queryParam("beerStyle", BeerStyle.IPA.name())
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(310)));
  }

  @Test
  void testListBeersByName() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .queryParam("beerName", "IPA")
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", greaterThanOrEqualTo(336)));
  }

  @Test
  void testListBeersByStyle() throws Exception {
    mockMvc.perform(get(BeerController.BEER_PATH)
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .queryParam("beerStyle", BeerStyle.LAGER.name())
        .queryParam("pageSize", "800"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()", is(39)));
  }

  @Rollback
  @Transactional
  @Test
  void testPatchBeer() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
    beerDTO.setId(null);
    beerDTO.setVersion(null);
    final String beerName = "UPDATED";
    beerDTO.setBeerName(beerName);

    ResponseEntity<?> responseEntity = beerController.updateBeerPatchById(beer.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    Beer updatedBeer = beerRepository.findById(beer.getId()).get();
    assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
  }

  @Test
  void testPatchBeerBadName() throws Exception {
    Beer beer = beerRepository.findAll().get(0);

    Map<String, Object> beerMap = new HashMap<>();
    beerMap.put("beerName",
        "New Name 8403840184081-348938401848148184-183-481948-185819581479184701340917340713094719037409173408173");

    mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
        .with(BeerControllerTest.jwtRequestPostProcessor)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(beerMap)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(1)));
  }

  @Test
  void testPatchBeerNotFound() {
    assertThrows(NotFoundException.class,
        () -> beerController.updateBeerPatchById(UUID.randomUUID(), BeerDTO.builder().build()));
  }

  @Rollback
  @Transactional
  @Test
  void deleteByIdFound() {
    Beer beer = beerRepository.findAll().get(0);
    ResponseEntity<?> responseEntity = beerController.deleteById(beer.getId());
    assertThat(beerRepository.findById(beer.getId())).isEmpty();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
  }

  @Test
  void deleteByIdNotFound() {
    assertThrows(NotFoundException.class, () -> beerController.deleteById(UUID.randomUUID()));
  }

  @Test
  void testUpdateNotFound() {
    assertThrows(NotFoundException.class,
        () -> beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build()));
  }

  @Rollback
  @Transactional
  @Test
  void updateExistingBeer() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
    beerDTO.setId(null);
    beerDTO.setVersion(null);
    final String beerName = "UPDATED";
    beerDTO.setBeerName(beerName);

    ResponseEntity<?> responseEntity = beerController.updateById(beer.getId(), beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    Beer updatedBeer = beerRepository.findById(beer.getId()).get();
    assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
  }

  @Test
  void testListBeers() {
    Page<BeerDTO> dtos = beerController.listBeers(null, null, false, 1, 25);

    assertThat(dtos.getContent().size()).isGreaterThan(0);
    assertThat(dtos.getContent().size()).isEqualTo(25);
  }

  @Rollback
  @Transactional
  @Test
  void testEmptyList() {
    beerRepository.deleteAll();
    Page<BeerDTO> dtos = beerController.listBeers(null, null, false, 1, 25);

    assertThat(dtos.getContent().size()).isEqualTo(0);
  }

  @Test
  void testGetById() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDTO beerDTO = beerController.getBeerById(beer.getId());

    assertThat(beerDTO).isNotNull();
  }

  @Test
  void testBeerIdNotFound() {
    assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
  }

  @Rollback
  @Transactional
  @Test
  void testSaveNewBeer() {
    BeerDTO beerDTO = BeerDTO.builder().beerName("New Beer").build();
    ResponseEntity<?> responseEntity = beerController.handlePost(beerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
    assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

    @SuppressWarnings("null")
    String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID savedUUID = UUID.fromString(locationUUID[4]);

    Beer beer = beerRepository.findById(savedUUID).get();
    assertThat(beer).isNotNull();
  }
}
