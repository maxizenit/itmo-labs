package ru.itmo.credithistory.userservice.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Employee {

  @Id private UUID userId;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String firstName;

  private String middleName;
}
