package ru.itmo.credithistory.dataservice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.dataservice.enm.CreditApplicationStatus;
import ru.itmo.credithistory.dataservice.enm.CreditType;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;

@Component
@NullMarked
public class TestUtils {

  public CreditApplication getTestCreditApplication() {
    CreditApplication creditApplication = new CreditApplication();
    creditApplication.setCustomerInn("123456789012");
    creditApplication.setCreditOrganizationId(UUID.randomUUID());
    creditApplication.setCreditType(CreditType.DEFAULT);
    creditApplication.setAmount(BigDecimal.valueOf(25000));
    creditApplication.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 30));
    creditApplication.setStatus(CreditApplicationStatus.CREATED);
    return creditApplication;
  }
}
