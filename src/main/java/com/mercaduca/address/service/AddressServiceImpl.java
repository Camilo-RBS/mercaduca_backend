package com.mercaduca.address.service;
import com.mercaduca.address.dto.AddressDTOs;
import com.mercaduca.address.entity.Address;
import com.mercaduca.address.repository.AddressRepository;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    @Override @Transactional
    public AddressDTOs.AddressResponse createAddress(AddressDTOs.CreateAddressRequest req, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario","id",userId));
        if (req.isDefaultAddress()) addressRepository.clearDefaultForUser(userId);
        Address a = Address.builder().user(user).alias(req.getAlias()).fullName(req.getFullName())
                .street(req.getStreet()).city(req.getCity()).state(req.getState())
                .country(req.getCountry()).zipCode(req.getZipCode()).phone(req.getPhone())
                .defaultAddress(req.isDefaultAddress()).build();
        return toResponse(addressRepository.save(a));
    }
    @Override @Transactional(readOnly = true)
    public List<AddressDTOs.AddressResponse> getMyAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByDefaultAddressDesc(userId).stream().map(this::toResponse).toList(); }
    @Override @Transactional
    public AddressDTOs.AddressResponse updateAddress(Long id, AddressDTOs.CreateAddressRequest req, Long userId) {
        Address a = findAndValidate(id, userId);
        if (req.isDefaultAddress()) addressRepository.clearDefaultForUser(userId);
        a.setAlias(req.getAlias()); a.setFullName(req.getFullName()); a.setStreet(req.getStreet());
        a.setCity(req.getCity()); a.setState(req.getState()); a.setCountry(req.getCountry());
        a.setZipCode(req.getZipCode()); a.setPhone(req.getPhone()); a.setDefaultAddress(req.isDefaultAddress());
        return toResponse(addressRepository.save(a));
    }
    @Override @Transactional
    public void deleteAddress(Long id, Long userId) { addressRepository.delete(findAndValidate(id, userId)); }
    @Override @Transactional
    public void setDefault(Long id, Long userId) {
        addressRepository.clearDefaultForUser(userId);
        Address a = findAndValidate(id, userId); a.setDefaultAddress(true); addressRepository.save(a);
    }
    private Address findAndValidate(Long id, Long userId) {
        Address a = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dirección","id",id));
        if (!a.getUser().getId().equals(userId)) throw new ForbiddenException("No tienes acceso a esta dirección");
        return a;
    }
    private AddressDTOs.AddressResponse toResponse(Address a) {
        AddressDTOs.AddressResponse r = new AddressDTOs.AddressResponse();
        r.setId(a.getId()); r.setAlias(a.getAlias()); r.setFullName(a.getFullName());
        r.setStreet(a.getStreet()); r.setCity(a.getCity()); r.setState(a.getState());
        r.setCountry(a.getCountry()); r.setZipCode(a.getZipCode()); r.setPhone(a.getPhone());
        r.setDefaultAddress(a.isDefaultAddress()); r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
