package ru.itmo.credithistory.scoringservice.dto;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import ru.itmo.credithistory.scoringservice.entity.ScoringRequest;

@Getter
@Builder
@NullMarked
public class GetOrCreateScoringRequestResultDto {

  private final ScoringRequest scoringRequest;
  private final boolean isCreated;
}
