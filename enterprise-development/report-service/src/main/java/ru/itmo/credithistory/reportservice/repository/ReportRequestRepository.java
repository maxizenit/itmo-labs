package ru.itmo.credithistory.reportservice.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.credithistory.reportservice.entity.ReportRequest;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, UUID> {

  @Query(
      """
                  SELECT rr FROM ReportRequest rr
                  WHERE rr.customerInn = :customerInn
                    AND rr.status <> ru.itmo.credithistory.reportservice.enm.ReportRequestStatus.GENERATION_FAILED
                    AND rr.createdAt >= :minActualTime
                  ORDER BY rr.createdAt DESC
                  """)
  Optional<ReportRequest> findActualReportRequest(String customerInn, LocalDateTime minActualTime);
}
