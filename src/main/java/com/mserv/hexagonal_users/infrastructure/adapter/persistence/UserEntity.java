package com.mserv.hexagonal_users.infrastructure.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class UserEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   UUID id;
   String name;
   String email;
   String password;
   LocalDateTime created;
   LocalDateTime modified;
   LocalDateTime lastLogin;
   String token;
   boolean isActive;

   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  List<PhoneEntity> phones = new ArrayList<>();


}

