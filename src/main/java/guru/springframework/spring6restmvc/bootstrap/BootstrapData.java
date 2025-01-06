package guru.springframework.spring6restmvc.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final BeerRepository beerRepository;
  private final CustomerRepository customerRepository;

  @Override
  public void run(String... args) throws Exception {
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
