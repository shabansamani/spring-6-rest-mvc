package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import guru.springframework.spring6restmvc.model.CustomerDTO;

public interface CustomerService {

  List<CustomerDTO> listCustomers();

  Optional<CustomerDTO> getCustomerById(UUID id);

  CustomerDTO saveNewCustomer(CustomerDTO customer);

  Optional<CustomerDTO> updateById(UUID customerId, CustomerDTO customer);

  boolean deleteById(UUID customerId);

  Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customer);
}
