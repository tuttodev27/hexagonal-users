package com.mserv.hexagonal_users.infrastructure.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneRequestDTO {

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(min = 7, max = 15, message = "El número debe contener entre 7 y 15 dígitos")
    @Pattern(regexp = "^[0-9]+$", message ="El número de teléfono debe contener solo dígitos")
    String number;

    @NotBlank(message = "El código del país es obligatorio")
    @Size(min = 1, max = 4, message = "El código del país debe contener entre 1 y 4 dígitos")
    @Pattern(regexp = "^[0-9]+$", message ="El código del país debe contener solo dígitos")
    String countryCode;

    @NotBlank(message = "El código de la ciudad es obligatorio")
    @Pattern(regexp = "\\d{1,4}", message = "El código de la ciudad debe contener entre 1 y 4 dígitos")
    String cityCode;
}
