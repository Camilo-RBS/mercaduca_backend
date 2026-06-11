package com.mercaduca.address.service;
import com.mercaduca.address.dto.AddressDTOs;
import java.util.List;
public interface AddressService {
    AddressDTOs.AddressResponse createAddress(AddressDTOs.CreateAddressRequest request, Long userId);
    List<AddressDTOs.AddressResponse> getMyAddresses(Long userId);
    AddressDTOs.AddressResponse updateAddress(Long addressId, AddressDTOs.CreateAddressRequest request, Long userId);
    void deleteAddress(Long addressId, Long userId);
    void setDefault(Long addressId, Long userId);
}
