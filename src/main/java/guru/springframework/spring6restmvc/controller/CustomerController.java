package guru.springframework.spring6restmvc.controller;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CustomerController {
  private final CustomerService customerService;

  public static final String CUSTOMER_PATH = "/api/v1/customer";
  public static final String CUSTOMER_ID_PATH = CUSTOMER_PATH + "{customerId}";

  @PatchMapping(CUSTOMER_ID_PATH)
  public ResponseEntity<?> updateCustomerPatchById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer) {
    if(customerService.patchCustomerById(customerId, customer).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping(CUSTOMER_ID_PATH)
  public ResponseEntity<?> deleteById(@PathVariable("customerId") UUID customerId) {
    if(!customerService.deleteById(customerId)) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping(CUSTOMER_ID_PATH)
  public ResponseEntity<?> updateById(@PathVariable("customerId") UUID id, @RequestBody CustomerDTO customer) {
    if(customerService.updateCustomerById(id, customer).isEmpty()) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping(CUSTOMER_PATH)
  public ResponseEntity<?> handlePost(@RequestBody CustomerDTO customer) {
    CustomerDTO savedCustomer = customerService.saveNewCustomer(customer);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", "/api/v1/customer/" + savedCustomer.getId().toString());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping(CUSTOMER_PATH)
  public List<CustomerDTO> listCustomers() {
    return customerService.listCustomers();
  }

  @GetMapping(CUSTOMER_ID_PATH)
  public CustomerDTO getCustomerById(@PathVariable("customerId") UUID id) {
    log.debug("Get Customer by Id - in controller");
    return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);  
  }
}
