package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final CacheManager cacheManager;

  @Cacheable("customerListCache")
  @Override
  public List<CustomerDTO> listCustomers() {
    return customerRepository.findAll()
        .stream()
        .map(customerMapper::customerToCustomerDTO)
        .toList();
  }

  @Cacheable(cacheNames = "customerCache", key = "#id")
  @Override
  public Optional<CustomerDTO> getCustomerById(UUID id) {
    return Optional.ofNullable(customerMapper.customerToCustomerDTO(customerRepository.findById(id)
        .orElse(null)));
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    if (cacheManager.getCache("customerListCache") != null)
      cacheManager.getCache("customerListCache").clear();
    return customerMapper
        .customerToCustomerDTO(customerRepository.save(customerMapper.customerDtoToCustomer(customer)));
  }

  @Override
  public Optional<CustomerDTO> updateCustomerById(UUID id, CustomerDTO customer) {
    clearCache(id);
    AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>(Optional.empty());
    customerRepository.findById(id).ifPresent(foundCustomer -> {
      foundCustomer.setCustomerName(customer.getCustomerName());
      atomicReference.set(Optional.of(customerMapper
          .customerToCustomerDTO(customerRepository.save(foundCustomer))));
    });
    return atomicReference.get();
  }

  @Override
  public Boolean deleteById(UUID customerId) {
    clearCache(customerId);
    if (customerRepository.existsById(customerId)) {
      customerRepository.deleteById(customerId);
      return true;
    }
    return false;
  }

  private void clearCache(UUID customerId) {
    if (cacheManager.getCache("customerListCache") != null) {
      cacheManager.getCache("customerListCache").clear();
    }
    if (cacheManager.getCache("customerCache") != null) {
      cacheManager.getCache("customerCache").evict(customerId);
    }
  }

  @Override
  public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer) {
    AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>(Optional.empty());
    customerRepository.findById(customerId).ifPresent(foundCustomer -> {
      if (!customer.getCustomerName().isBlank()
          && !customer.getCustomerName().equals(foundCustomer.getCustomerName())) {
        foundCustomer.setCustomerName(customer.getCustomerName());
        atomicReference.set(Optional.of(customerMapper
            .customerToCustomerDTO(customerRepository.save(foundCustomer))));
      } else {
        atomicReference.set(Optional.of(customerMapper.customerToCustomerDTO(foundCustomer)));
      }
    });
    return atomicReference.get();
  }
}
