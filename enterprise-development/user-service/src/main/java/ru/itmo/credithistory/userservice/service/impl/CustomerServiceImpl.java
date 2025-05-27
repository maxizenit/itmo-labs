package ru.itmo.credithistory.userservice.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.userservice.dto.rq.CreateCustomerRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCustomerRqDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.Customer;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.exception.CustomerNotFoundException;
import ru.itmo.credithistory.userservice.mapper.CustomerMapper;
import ru.itmo.credithistory.userservice.repository.CustomerRepository;
import ru.itmo.credithistory.userservice.service.CustomerService;
import ru.itmo.credithistory.userservice.service.UserService;

@Service
@RequiredArgsConstructor
@NullMarked
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final UserService userService;
  private final CustomerMapper customerMapper;

  @Override
  public Customer getCustomerByUserId(UUID userId) {
    return getCustomerByUserIdInternal(userId);
  }

  @Override
  public Customer getCustomerByUserIdInternal(UUID userId) {
    return customerRepository.findById(userId).orElseThrow(CustomerNotFoundException::new);
  }

  @Override
  public Customer getCustomerByInn(String inn) {
    return customerRepository.findByInn(inn).orElseThrow(CustomerNotFoundException::new);
  }

  @Transactional
  @Override
  public Customer createCustomer(CreateCustomerRqDto createDto) {
    User user =
        userService.createUser(createDto.getEmail(), createDto.getPassword(), UserRole.CUSTOMER);
    Customer customer = customerMapper.fromCreateDtoToEntity(createDto);
    customer.setUser(user);
    return customerRepository.save(customer);
  }

  @Transactional
  @Override
  public Customer updateCustomerByUserId(UUID userId, UpdateCustomerRqDto updateDto) {
    Customer customer = getCustomerByUserId(userId);
    customerMapper.updateEntityFromUpdateDto(customer, updateDto);
    return customerRepository.save(customer);
  }

  @Transactional
  @Override
  public void deleteCustomerByUserId(UUID userId) {
    if (!customerRepository.existsById(userId)) {
      throw new CustomerNotFoundException();
    }
    customerRepository.deleteById(userId);
  }
}
