package guru.springframework.spring6restmvc.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.spring6restmvc.config.SpringSecConfig;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;

@WebMvcTest(CustomerController.class)
@Import(SpringSecConfig.class)
public class CustomerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  CustomerService customerService;

  CustomerServiceImpl customerServiceImpl;

  @Captor
  ArgumentCaptor<UUID> uuidArgumentCaptor;

  @Captor
  ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

  @Value("${spring.security.user.name}")
  String username;

  @Value("${spring.security.user.password}")
  String password;

  @BeforeEach
  void setUp() {
    customerServiceImpl = new CustomerServiceImpl();
  }

  @Test
  void testPatchCustomer() throws Exception {
    CustomerDTO customer = customerServiceImpl.listCustomers().get(0);

    Map<String, Object> customerMap = new HashMap<>();
    customerMap.put("customerName", "New customer");

    given(customerService.patchCustomerById(any(UUID.class), any(CustomerDTO.class))).willReturn(Optional.of(customer));

    mockMvc.perform(patch(CustomerController.CUSTOMER_ID_PATH, customer.getId())
      .with(BeerControllerTest.jwtRequestPostProcessor)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(customerMap)))
      .andExpect(status().isNoContent());

    verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());
    assertThat(customerMap.get("customerName")).isEqualTo(customerArgumentCaptor.getValue().getCustomerName());
  }

  @Test
  void testDeleteCustomer() throws Exception {
    CustomerDTO customer = customerServiceImpl.listCustomers().get(0);

    given(customerService.deleteById(any(UUID.class))).willReturn(true);

    mockMvc.perform(delete(CustomerController.CUSTOMER_ID_PATH, customer.getId())
      .with(BeerControllerTest.jwtRequestPostProcessor)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    verify(customerService).deleteById(uuidArgumentCaptor.capture());

    assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
  }

  @Test
  void testUpdateCustomer() throws Exception {
    CustomerDTO customer = customerServiceImpl.listCustomers().get(0);

    given(customerService.updateCustomerById(any(UUID.class), any(CustomerDTO.class))).willReturn(Optional.of(customer));

    mockMvc.perform(put(CustomerController.CUSTOMER_ID_PATH, customer.getId())
      .with(BeerControllerTest.jwtRequestPostProcessor)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(customer)))
      .andExpect(status().isNoContent());

    verify(customerService).updateCustomerById(any(UUID.class), any(CustomerDTO.class));
  }

  @Test
  void testCreateNewCustomer() throws Exception {
    CustomerDTO customer = customerServiceImpl.listCustomers().get(0);
    customer.setVersion(null);
    customer.setId(null);

    given(customerService.saveNewCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.listCustomers().get(1));
    
    mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
      .with(BeerControllerTest.jwtRequestPostProcessor)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(customer)))
      .andExpect(status().isCreated())
      .andExpect(header().exists("Location"));
  }
 
  @Test
  void testListCustomers() throws Exception {
    given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());

    mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
      .with(BeerControllerTest.jwtRequestPostProcessor)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.length()", is(3)));
  }

  @Test
  void testGetCustomerByIdNotFound() throws Exception {
    given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

    mockMvc.perform(get(CustomerController.CUSTOMER_ID_PATH, UUID.randomUUID())
      .with(BeerControllerTest.jwtRequestPostProcessor))
      .andExpect(status().isNotFound());
  }

  @Test
  void testGetCustomerById() throws Exception {
    CustomerDTO testCustomer = customerServiceImpl.listCustomers().get(0);

    given(customerService.getCustomerById(testCustomer.getId())).willReturn(Optional.of(testCustomer));

    mockMvc.perform(get(CustomerController.CUSTOMER_ID_PATH, testCustomer.getId())
      .with(BeerControllerTest.jwtRequestPostProcessor)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
      .andExpect(jsonPath("$.customerName", is(testCustomer.getCustomerName())));
  }
}
