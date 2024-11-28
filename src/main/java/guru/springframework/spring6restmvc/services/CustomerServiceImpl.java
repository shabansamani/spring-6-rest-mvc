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
  private final Map<UUID, CustomerDTO> customerMap;

  public CustomerServiceImpl() {
    this.customerMap = new HashMap<>();

    CustomerDTO customer1 = CustomerDTO.builder()
    .customerName("Lawrence Penderson")
    .id(UUID.randomUUID())
    .version(1)
    .createdDate(LocalDateTime.now())
    .lastModifiedDate(LocalDateTime.now())
    .build();

    CustomerDTO customer2 = CustomerDTO.builder()
    .customerName("Dan Avidan")
    .id(UUID.randomUUID())
    .version(1)
    .createdDate(LocalDateTime.now())
    .lastModifiedDate(LocalDateTime.now())
    .build();

    CustomerDTO customer3 = CustomerDTO.builder()
    .customerName("Arin Hanson")
    .id(UUID.randomUUID())
    .version(1)
    .createdDate(LocalDateTime.now())
    .lastModifiedDate(LocalDateTime.now())
    .build();

    this.customerMap.put(customer1.getId(), customer1);
    this.customerMap.put(customer2.getId(), customer2);
    this.customerMap.put(customer3.getId(), customer3);
  }
  @Override
  public List<CustomerDTO> listCustomers() {
    return new ArrayList<>(this.customerMap.values());
  }

  @Override
  public Optional<CustomerDTO> getCustomerById(UUID id) {
   return Optional.of(customerMap.get(id)); 
  }
  
  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    CustomerDTO savedCustomer = CustomerDTO.builder()
    .customerName(customer.getCustomerName())
    .id(UUID.randomUUID())
    .version(1)
    .createdDate(LocalDateTime.now())
    .lastModifiedDate(LocalDateTime.now())
    .build();

    this.customerMap.put(savedCustomer.getId(), savedCustomer);
    return savedCustomer;
  }

  @Override
  public Optional<CustomerDTO> updateCustomerById(UUID id, CustomerDTO customer) {
    CustomerDTO existing = this.customerMap.get(id);
    existing.setCustomerName(customer.getCustomerName());
    existing.setLastModifiedDate(LocalDateTime.now());
    this.customerMap.put(existing.getId(), existing);
    return Optional.of(existing);
  }

  @Override
  public Boolean deleteById(UUID customerId) {
    this.customerMap.remove(customerId);
    return true;
  }
  
  @Override
  public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer) {
    CustomerDTO existing = customerMap.get(customerId);

    if(StringUtils.hasText(customer.getCustomerName())) existing.setCustomerName(customer.getCustomerName());
    existing.setLastModifiedDate(LocalDateTime.now());
    customerMap.put(existing.getId(), existing);
    return Optional.of(existing);
  }
}
