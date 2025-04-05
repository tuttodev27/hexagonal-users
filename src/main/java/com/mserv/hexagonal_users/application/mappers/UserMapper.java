package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneResponseDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final AuthService authService;

    public UserMapper(AuthService authService) {
        this.authService = authService;
    }

    public User userToDomain(UserRequestDTO request) {
        UUID userId = UUID.randomUUID();
        LocalDateTime currentDate = LocalDateTime.now();
        String token = authService.generateToken(request.getEmail());

        User user = new User(
                userId,
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                mapPhonesToDomain(request.getPhones()),
                currentDate,
                currentDate,
                currentDate,
                token,
                true
        );

        return user;
    }

    public UserResponseDTO domainToUserResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                mapPhonesToResponse(user.getPhones()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }

    private List<Phone> mapPhonesToDomain(List<PhoneRequestDTO> phonesRequest) {
        if (phonesRequest == null || phonesRequest.isEmpty()) {
            return Collections.emptyList();
        }
        return phonesRequest.stream()
                .map(phoneDTO -> new Phone(
                        phoneDTO.getNumber(),
                        phoneDTO.getCityCode(),
                        phoneDTO.getCountryCode()
                ))
                .collect(Collectors.toList());
    }

    private List<PhoneResponseDTO> mapPhonesToResponse(List<Phone> phones) {
        if (phones == null) return Collections.emptyList();
        return phones.stream()
                .map(this::phoneDomainToResponse)
                .collect(Collectors.toList());
    }

    private PhoneResponseDTO phoneDomainToResponse(Phone phone) {
        return new PhoneResponseDTO(
                phone.getNumber(),
                phone.getCityCode(),
                phone.getCountryCode()
        );
    }
}
