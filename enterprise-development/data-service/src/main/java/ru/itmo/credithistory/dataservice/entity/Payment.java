package ru.itmo.credithistory.dataservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "credit_id", nullable = false)
  private Credit credit;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDateTime paidAt;
}
