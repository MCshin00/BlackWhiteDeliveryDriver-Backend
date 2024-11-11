package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.AddressRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.AddressResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Address;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.repository.AddressRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public void createAddress(@Valid AddressRequestDto requestDto, User user) {
        Address address = new Address(requestDto, user);
        addressRepository.save(address);
    }

    @Transactional
    public void updateAddress(@Valid AddressRequestDto requestDto, Long addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException("해당 주소가 존재하지 않습니다."));

        if (!address.getUser().equals(user)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        address.setZipNum(requestDto.getZipNum());
        address.setCity(requestDto.getCity());
        address.setDistrict(requestDto.getDistrict());
        address.setStreetName(requestDto.getStreetName());
        address.setStreetNum(requestDto.getStreetNum());
        address.setDetailAddr(requestDto.getDetailAddr());

        addressRepository.save(address);
    }

    public List<AddressResponseDto> getAllAddresses(User user) {
        List<Address> addresses = addressRepository.findAllByUser(user);
        List<AddressResponseDto> addressResponseDtos = new ArrayList<>();

        for (Address address : addresses) {
            addressResponseDtos.add(new AddressResponseDto(address));
        }

        return addressResponseDtos;
    }

    @Transactional
    public void setCurrentAddress(Long addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException("해당 주소가 존재하지 않습니다."));

        if (!address.getUser().equals(user)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        user.setCurrentAddress(address);
    }
}
