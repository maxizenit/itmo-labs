package ru.itmo.credithistory.scoringservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.credithistory.scoringservice.enm.ScoringRequestStatus;

@Getter
@Setter
@Entity
public class ScoringRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String customerInn;

  private UUID reportRequestId;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ScoringRequestStatus status;

  private Integer result;

  @PrePersist
  public void prePersist() {
    if (status == null) status = ScoringRequestStatus.READY;
    if (createdAt == null) createdAt = LocalDateTime.now();
  }
}
