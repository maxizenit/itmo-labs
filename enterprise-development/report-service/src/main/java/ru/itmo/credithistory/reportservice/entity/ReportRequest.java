package ru.itmo.credithistory.reportservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.credithistory.reportservice.enm.ReportRequestStatus;

@Getter
@Setter
@Entity
public class ReportRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String customerInn;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReportRequestStatus status;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  private byte[] report;

  @PrePersist
  public void prePersist() {
    if (status == null) status = ReportRequestStatus.READY;
    if (createdAt == null) createdAt = LocalDateTime.now();
  }
}
