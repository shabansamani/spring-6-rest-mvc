package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public List<CustomerDTO> listCustomers() {
    return customerRepository.findAll()
        .stream()
        .map(customerMapper::customerToCustomerDTO)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<CustomerDTO> getCustomerById(UUID id) {
    return Optional.ofNullable(customerMapper.customerToCustomerDTO(customerRepository.findById(id).orElse(null)));
  }

  @Override
  public CustomerDTO saveNewCustomer(CustomerDTO customer) {
    return customerMapper
        .customerToCustomerDTO(customerRepository.save(customerMapper.customerDtoToCustomer(customer)));
  }

  @Override
  public Optional<CustomerDTO> updateById(UUID customerId, CustomerDTO customer) {
    AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>(Optional.empty());
    customerRepository.findById(customerId).ifPresentOrElse(customerEntity -> {
      customerEntity.setCustomerName(customer.getCustomerName());
      customerRepository.save(customerEntity);
      atomicReference.set(Optional.of(customerMapper.customerToCustomerDTO(customerEntity)));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

  @Override
  public boolean deleteById(UUID customerId) {
    if (customerRepository.existsById(customerId)) {
      customerRepository.deleteById(customerId);
      return true;
    }
    return false;

  }

  @Override
  public Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customer) {
    AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
    customerRepository.findById(customerId).ifPresentOrElse(existing -> {
      if (StringUtils.hasText(customer.getCustomerName())) {
        existing.setCustomerName(customer.getCustomerName());
      }
      atomicReference.set(Optional.of(customerMapper.customerToCustomerDTO(customerRepository.save(existing))));
    }, () -> {
      atomicReference.set(Optional.empty());
    });

    return atomicReference.get();
  }

}
