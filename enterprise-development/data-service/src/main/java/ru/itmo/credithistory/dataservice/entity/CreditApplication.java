package ru.itmo.credithistory.dataservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.credithistory.dataservice.enm.CreditApplicationStatus;
import ru.itmo.credithistory.dataservice.enm.CreditType;

@Getter
@Setter
@Entity
public class CreditApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String customerInn;

  @Column(nullable = false)
  private UUID creditOrganizationId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CreditType creditType;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CreditApplicationStatus status;
}
