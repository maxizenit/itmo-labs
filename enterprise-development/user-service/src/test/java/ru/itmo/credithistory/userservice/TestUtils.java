package ru.itmo.credithistory.userservice;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.userservice.enm.CreditOrganizationType;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;
import ru.itmo.credithistory.userservice.entity.Customer;
import ru.itmo.credithistory.userservice.entity.Employee;
import ru.itmo.credithistory.userservice.entity.User;

@Component
@RequiredArgsConstructor
@NullMarked
public class TestUtils {

  private final PasswordEncoder passwordEncoder;

  public String getRandomEmail() {
    return UUID.randomUUID() + "@example.com";
  }

  public User getTestUser() {
    User user = new User();
    user.setEmail(getRandomEmail());
    user.setPassword(passwordEncoder.encode("password"));
    user.setRole(UserRole.CUSTOMER);
    return user;
  }

  public CreditOrganization getTestCreditOrganization() {
    User user = getTestUser();
    user.setRole(UserRole.CREDIT_ORGANIZATION);

    CreditOrganization creditOrganization = new CreditOrganization();
    creditOrganization.setUser(user);
    creditOrganization.setInn("1234567890");
    creditOrganization.setShortName("Банк");
    creditOrganization.setFullName("Банк Тестовый");
    creditOrganization.setType(CreditOrganizationType.BANK);
    return creditOrganization;
  }

  public Customer getTestCustomer() {
    User user = getTestUser();

    Customer customer = new Customer();
    customer.setUser(user);
    customer.setInn("123456789012");
    customer.setLastName("Клиентов");
    customer.setFirstName("Клиент");
    customer.setMiddleName("Клиентович");
    customer.setBirthdate(LocalDate.of(2000, 1, 1));
    return customer;
  }

  public Employee getTestEmployee() {
    User user = getTestUser();
    user.setRole(UserRole.EMPLOYEE);

    Employee employee = new Employee();
    employee.setUser(user);
    employee.setLastName("Сотрудников");
    employee.setFirstName("Сотрудник");
    employee.setMiddleName("Сотрудникович");
    return employee;
  }
}
