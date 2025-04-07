package com.mserv.hexagonal_users.infrastructure.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;




@Getter
@Setter

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneResponseDTO {
   private Long id;
   private String number;
   private String cityCode;
   private String countryCode;
}
