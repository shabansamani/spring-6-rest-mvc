package guru.springframework.spring6restmvc.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;

@SpringBootTest
public class CustomerControllerIT {

  @Autowired
  CustomerController customerController;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  CustomerMapper customerMapper;

  @Test
  void testPatchCustomerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      customerController.patchById(UUID.randomUUID(), CustomerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void patchCustomerTest() {
    Customer customer = customerRepository.findAll().get(0);
    CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
    customerDTO.setId(null);
    customerDTO.setVersion(null);
    final String customerName = "New Name";
    customerDTO.setCustomerName(customerName);

    ResponseEntity<?> responseEntity = customerController.patchById(customer.getId(), customerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
  }

  @Test
  void testDeleteByIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      customerController.deleteById(UUID.randomUUID());
    });
  }

  @Rollback
  @Transactional
  @Test
  void deleteCustomerTest() {
    Customer customer = customerRepository.findAll().get(0);
    ResponseEntity<?> responseEntity = customerController.deleteById(customer.getId());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
    assertThat(customerRepository.findById(customer.getId())).isEmpty();
  }

  @Test
  void testUpdateCustomerNotFound() {
    assertThrows(NotFoundException.class, () -> {
      customerController.updateById(UUID.randomUUID(), CustomerDTO.builder().build());
    });
  }

  @Rollback
  @Transactional
  @Test
  void updateCustomerTest() {
    Customer customer = customerRepository.findAll().get(0);
    CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
    customerDTO.setId(null);
    customerDTO.setVersion(null);
    final String customerName = "New Name";
    customerDTO.setCustomerName(customerName);

    ResponseEntity<?> responseEntity = customerController.updateById(customer.getId(), customerDTO);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

    Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
    assertThat(updatedCustomer.getCustomerName()).isEqualTo(customerName);

  }

  @Test
  void saveNewCustomerTest() {
    CustomerDTO customerDTO = CustomerDTO.builder().customerName("New Customer").build();

    ResponseEntity<?> responseEntity = customerController.saveNewCustomer(customerDTO);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
    assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

    String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID savedUUID = UUID.fromString(locationUUID[4]);

    Customer customer = customerRepository.findById(savedUUID).get();
    assertThat(customer).isNotNull();
  }

  @Test
  void testCustomerIdNotFound() {
    assertThrows(NotFoundException.class, () -> {
      customerController.getCustomerById(UUID.randomUUID());
    });
  }

  @Test
  void testGetCustomerById() {
    Customer customer = customerRepository.findAll().get(0);

    CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());
    assertThat(customerDTO).isNotNull();
    assertThat(customerDTO.getId()).isEqualTo(customer.getId());
  }

  @Rollback
  @Transactional
  @Test
  void testEmptyList() {
    customerRepository.deleteAll();

    List<CustomerDTO> customerDTOs = customerController.listCustomers();

    assertThat(customerDTOs.size()).isZero();
  }

  @Test
  void testListBeers() {
    List<CustomerDTO> customerDTOs = customerController.listCustomers();

    assertThat(customerDTOs.size()).isNotZero();
  }
}
