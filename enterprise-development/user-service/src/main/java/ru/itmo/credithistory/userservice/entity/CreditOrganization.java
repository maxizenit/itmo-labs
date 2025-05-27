package ru.itmo.credithistory.userservice.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.itmo.credithistory.userservice.enm.CreditOrganizationType;

@Getter
@Setter
@Entity
public class CreditOrganization {

  @Id private UUID userId;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, unique = true)
  private String inn;

  @Column(nullable = false)
  private String shortName;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CreditOrganizationType type;
}
