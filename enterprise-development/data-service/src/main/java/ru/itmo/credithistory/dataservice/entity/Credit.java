package ru.itmo.credithistory.dataservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.credithistory.dataservice.enm.CreditType;

@Getter
@Setter
@Entity
public class Credit {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String externalId;

  @Column(nullable = false)
  private String customerInn;

  @Column(nullable = false)
  private UUID creditOrganizationId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CreditType type;

  @Column(nullable = false)
  private BigDecimal initialAmount;

  private BigDecimal remainingAmount;

  @Column(nullable = false)
  private LocalDateTime issuedAt;

  private LocalDateTime repaidAt;

  @Column(nullable = false)
  private Boolean active;

  @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Payment> payments;

  @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Overdue> overdue;
}
