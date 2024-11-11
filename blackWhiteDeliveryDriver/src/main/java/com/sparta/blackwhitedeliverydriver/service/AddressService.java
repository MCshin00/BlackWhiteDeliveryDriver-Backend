package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.AddressRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.AddressResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Address;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.AddressRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public void createAddress(@Valid AddressRequestDto requestDto,String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Address address = new Address(requestDto, user);
        addressRepository.save(address);
    }

    @Transactional
    public void updateAddress(@Valid AddressRequestDto requestDto, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주소를 찾을 수 없습니다."));

        address.setZipNum(requestDto.getZipNum());
        address.setCity(requestDto.getCity());
        address.setDistrict(requestDto.getDistrict());
        address.setStreetName(requestDto.getStreetName());
        address.setStreetNum(requestDto.getStreetNum());
        address.setDetailAddr(requestDto.getDetailAddr());

        addressRepository.save(address);
    }

    public List<AddressResponseDto> getAllAddresses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        List<Address> addresses = addressRepository.findAllByUser(user);
        List<AddressResponseDto> addressResponseDtos = new ArrayList<>();

        for (Address address : addresses) {
            addressResponseDtos.add(new AddressResponseDto(address));
        }

        return addressResponseDtos;
    }
}
