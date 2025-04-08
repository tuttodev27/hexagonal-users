package com.mserv.hexagonal_users.infrastructure.adapter.persistence;

import com.mserv.hexagonal_users.domain.model.Phone;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")

@Getter
@Setter
@Data
@ToString
@EqualsAndHashCode
@Builder
public class UserEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String name;
   private String email;
   private String password;
   private LocalDateTime created;
   private LocalDateTime modified;
   private LocalDateTime lastLogin;
   private String token;
   private boolean active;

   @Version
   private Long version;

   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
   private List<PhoneEntity> phones = new ArrayList<>();

   public void prePersist() {
      LocalDateTime now = LocalDateTime.now();
      this.created = now;
      this.modified = now;
      this.lastLogin = now;
   }

   public void preUpdate() {
      this.modified = LocalDateTime.now();
   }
}

