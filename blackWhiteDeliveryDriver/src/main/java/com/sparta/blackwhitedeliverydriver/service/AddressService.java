package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.AddressIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.AddressRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.AddressResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Address;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.AddressRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressIdResponseDto createAddress(@Valid AddressRequestDto requestDto, User user) {
        Address address = Address.from(requestDto, user);
        addressRepository.save(address);

        return new AddressIdResponseDto(address.getId());
    }

    @Transactional
    public AddressIdResponseDto updateAddress(@Valid AddressRequestDto requestDto, UUID addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        checkDeletedAddress(address);

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException(ExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

        address.update(requestDto);
        addressRepository.save(address);

        return new AddressIdResponseDto(address.getId());
    }

    public List<AddressResponseDto> getAllAddresses(User user) {
        List<Address> addresses = addressRepository.findAllByUserAndNotDeleted(user);
        List<AddressResponseDto> addressResponseDtos = new ArrayList<>();

        for (Address address : addresses) {
            addressResponseDtos.add(AddressResponseDto.from(address));
        }

        return addressResponseDtos;
    }

    @Transactional
    public AddressIdResponseDto setCurrentAddress(UUID addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException(ExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

        user.updateCurrentAddress(address);
        userRepository.save(user);

        return new AddressIdResponseDto(address.getId());
    }

    @Transactional
    public AddressIdResponseDto deleteAddress(UUID addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        checkDeletedAddress(address);

        if (!address.getUser().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException(ExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

        address.setDeletedBy(user.getUsername());
        address.setDeletedDate(LocalDateTime.now());

        addressRepository.save(address);

        return new AddressIdResponseDto(address.getId());
    }

    private void checkDeletedAddress(Address address) {
        if (address.getDeletedDate() != null || address.getDeletedBy() != null) {
            throw new IllegalArgumentException(ExceptionMessage.ADDRESS_DELETED.getMessage());
        }
    }
}
