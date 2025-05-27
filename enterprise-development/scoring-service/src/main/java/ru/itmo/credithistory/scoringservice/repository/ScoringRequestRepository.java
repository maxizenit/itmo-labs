package ru.itmo.credithistory.scoringservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.credithistory.scoringservice.enm.ScoringRequestStatus;
import ru.itmo.credithistory.scoringservice.entity.ScoringRequest;

public interface ScoringRequestRepository extends JpaRepository<ScoringRequest, UUID> {

  List<ScoringRequest> findAllByStatusOrderByCreatedAt(
      ScoringRequestStatus status, Pageable pageable);

  @Query(
      """
              SELECT sr FROM ScoringRequest sr
              WHERE sr.customerInn = :customerInn
                AND sr.status <> ru.itmo.credithistory.scoringservice.enm.ScoringRequestStatus.CALCULATION_FAILED
                AND sr.createdAt >= :minActualTime
              ORDER BY sr.createdAt DESC
              """)
  Optional<ScoringRequest> findActualScoringRequest(
      String customerInn, LocalDateTime minActualTime);
}
