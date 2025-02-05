package guru.springframework.spring6restmvc.bootstrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final BeerRepository beerRepository;
  private final CustomerRepository customerRepository;
  private final BeerCsvService beerCsvService;

  @Transactional
  @Override
  public void run(String... args) throws Exception {
    loadBeerData();
    loadCsvData();
    loadCustomerData();
  }

  private void loadCsvData() throws FileNotFoundException {
    if (beerRepository.count() < 10) {
      File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
      List<BeerCSVRecord> records = beerCsvService.convertCSV(file);

      for (BeerCSVRecord beerCSVRecord : records) {
        BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
          case "American Pale Lager" -> BeerStyle.LAGER;
          case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
            BeerStyle.ALE;
          case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
          case "American Porter" -> BeerStyle.PORTER;
          case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
          case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
          case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
          case "English Pale Ale" -> BeerStyle.PALE_ALE;
          default -> BeerStyle.PILSNER;
        };

        beerRepository.save(Beer.builder()
            .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
            .beerStyle(beerStyle)
            .price(BigDecimal.TEN)
            .upc(beerCSVRecord.getRow().toString())
            .quantityOnHand(beerCSVRecord.getCount())
            .build());
      }
    }
  }

  private void loadCustomerData() {
    if (customerRepository.count() == 0) {
      Customer customerOne = Customer.builder()
          .customerName("Arin Hanson")
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      Customer customerTwo = Customer.builder()
          .customerName("Dan Avidan")
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      Customer savedCustomerOne = customerRepository.save(customerOne);
      Customer savedCustomerTwo = customerRepository.save(customerTwo);

    }

  }

  private void loadBeerData() {
    if (beerRepository.count() == 0) {

      Beer beerOne = Beer.builder()
          .beerName("Galaxy Cat")
          .beerStyle(BeerStyle.PALE_ALE)
          .upc("123456")
          .price(new BigDecimal("12.99"))
          .quantityOnHand(122)
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      Beer beerTwo = Beer.builder()
          .beerName("Crank")
          .beerStyle(BeerStyle.PALE_ALE)
          .upc("123456222")
          .price(new BigDecimal("11.99"))
          .quantityOnHand(392)
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      Beer beerThree = Beer.builder()
          .beerName("Sunshine City")
          .beerStyle(BeerStyle.IPA)
          .upc("123456789")
          .price(new BigDecimal("11.99"))
          .quantityOnHand(553)
          .createdDate(LocalDateTime.now())
          .updatedDate(LocalDateTime.now())
          .build();

      Beer beerOneSaved = beerRepository.save(beerOne);
      Beer beerTwoSaved = beerRepository.save(beerTwo);
      Beer beerThreesaved = beerRepository.save(beerThree);
    }
  }

}
