package com.mserv.hexagonal_users.infrastructure.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "phones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    String number;
    String cityCode;
    String countryCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
