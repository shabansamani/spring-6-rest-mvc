package guru.springframework.spring6restmvc.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import guru.springframework.spring6restmvc.model.CustomerDTO;

@Service
public class CustomerServiceImpl implements CustomerService {

  private Map<UUID, CustomerDTO> customerMap;

  public CustomerServiceImpl() {
    this.customerMap = new HashMap<>();

    CustomerDTO customerOne = CustomerDTO.builder()
        .id(UUID.randomUUID())
        .version(1)
        .customerName("Arin Hanson")
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();

    CustomerDTO customerTwo = CustomerDTO.builder()
        .id(UUID.randomUUID())
        .version(1)
        .customerName("Dan Avidan")
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();

    customerMap.put(customerOne.getId(), customerOne);
    customerMap.put(customerTwo.getId(), customerTwo);
  }

  @Override
  public List<CustomerDTO> listCustomers() {
    return new ArrayList<>(customerMap.values());
  }

  @Override
  public Optional<CustomerDTO> getCustomerById(UUID id) {
    return Optional.of(customerMap.get(id));
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    CustomerDTO savedCustomer = CustomerDTO.builder()
        .id(UUID.randomUUID())
        .customerName(customer.getCustomerName())
        .version(1)
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();

    this.customerMap.put(savedCustomer.getId(), savedCustomer);

    return savedCustomer;
  }

  @Override
  public Optional<CustomerDTO> updateById(UUID customerId, CustomerDTO customer) {
    CustomerDTO existing = customerMap.get(customerId);
    existing.setCustomerName(customer.getCustomerName());
    existing.setUpdatedDate(LocalDateTime.now());

    this.customerMap.put(existing.getId(), existing);

    return Optional.of(existing);
  }

  @Override
  public boolean deleteById(UUID customerId) {
    this.customerMap.remove(customerId);

    return true;
  }

  @Override
  public Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customer) {
    CustomerDTO existing = this.customerMap.get(customerId);

    if (StringUtils.hasText(customer.getCustomerName())) {
      existing.setCustomerName(customer.getCustomerName());
    }

    this.customerMap.put(customerId, customer);
    return Optional.of(existing);
  }

}
