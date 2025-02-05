package guru.springframework.spring6restmvc.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;

@DataJpaTest
@Import(BeerCsvServiceImpl.class)
public class BootstrapDataTest {

  @Autowired
  BeerRepository beerRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  BeerCsvService beerCsvService;

  BootstrapData bootstrapData;

  @BeforeEach
  void setUp() {
    bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService);
  }

  @Test
  void testRun() throws Exception {
    bootstrapData.run();

    assertThat(beerRepository.count()).isEqualTo(2413);
    assertThat(customerRepository.count()).isEqualTo(2);
  }
}
