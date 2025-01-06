package guru.springframework.spring6restmvc.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
public class CustomerController {

  public static final String CUSTOMER_PATH = "/api/v1/customer";
  public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{id}";

  private final CustomerService customerService;

  @PatchMapping(CUSTOMER_PATH_ID)
  public ResponseEntity<?> patchById(@PathVariable("id") UUID customerId, @RequestBody CustomerDTO customer) {
    if (customerService.patchById(customerId, customer).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping(CUSTOMER_PATH_ID)
  public ResponseEntity<?> deleteById(@PathVariable("id") UUID customerId) {
    if (!customerService.deleteById(customerId)) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping(CUSTOMER_PATH_ID)
  public ResponseEntity<?> updateById(@PathVariable("id") UUID customerId, @RequestBody CustomerDTO customer) {
    if (customerService.updateById(customerId, customer).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping(CUSTOMER_PATH)
  public ResponseEntity<?> saveNewCustomer(@RequestBody CustomerDTO customer) {
    CustomerDTO savedCustomer = customerService.saveNewCustomer(customer);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", String.format("/api/v1/customer/%s", savedCustomer.getId()));

    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping(CUSTOMER_PATH)
  public List<CustomerDTO> listCustomers() {
    return customerService.listCustomers();
  }

  @GetMapping(CUSTOMER_PATH_ID)
  public CustomerDTO getCustomerById(@PathVariable("id") UUID id) {
    log.debug("In CustomerController -- getCustomerById");
    return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);
  }
}
