package com.ecommerce.admin.LIBRARY.ProductsService.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.AddressDto;
import com.ecommerce.admin.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.admin.LIBRARY.Model.User.Address;
import com.ecommerce.admin.LIBRARY.Model.User.Order;
import com.ecommerce.admin.LIBRARY.Model.User.OrderAddress;
import com.ecommerce.admin.LIBRARY.ProductsService.AddressService;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.AddressRepo;
import com.ecommerce.admin.LIBRARY.Service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    AddressRepo addressRepo;
    CustomerService customerService;

    public AddressServiceImpl(AddressRepo addressRepo, CustomerService customerService) {
        this.addressRepo = addressRepo;
        this.customerService = customerService;
    }

    @Transactional
    @Override
    public void saveAddress(String email, AddressDto addressDto) {
        try{
            if(addressDto.getIsDefault()!=null && addressDto.getIsDefault()){
                changeDefaultAddressForCustomer(email, addressDto.getId());
            }
            addressRepo.save(dtoToEntity(addressDto, email));
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException) throw e;
            throw new InvalidStateException("", "Unable to update address try again later");
        }
    }

    private void changeDefaultAddressForCustomer(String email, Long id) {
        try{
            addressRepo.uncheckDefaultAddressForCustomer(email);
            addressRepo.setAddressAsDefaultForCustomer(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to set Address as Default");
        }

    }

    public List<AddressDto> findAll() {
        try{
            List<Address> addresses = addressRepo.findAll();
            return addresses.stream()
                    .map(this::entityToDto) // Use method reference to map Address to AddressDto
                    .collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, l-41");
        }

    }

    @Override
    public List<Address> findAddressByCustomer(String email) {
        try{
            return addressRepo.findAddressByCustomer(customerService.findByEmail(email));
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, l-56");
        }
    }

    @Override
    public AddressDto findAddressById(Long id) {
        try{
            Optional<Address> optionalAddress = addressRepo.findById(id);
            if(optionalAddress.isEmpty())
                throw new InvalidStateException("","Unable to find address, l-44");
            return entityToDto(optionalAddress.get());
        }catch (Exception e){
            if(e instanceof InvalidStateException) throw e;
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, l-69");
        }

    }

    @Override
    public boolean existsById(Long id) {
        try{
            return addressRepo.existsById(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, l-81");
        }
    }

    @Override
    public OrderAddress addressDtoToOrderAddress(AddressDto address, Order order) {

        return new OrderAddress(address.getId(), address.getRecipientName(),
                address.getHouseName(), address.getAddressLine(),
                address.getPhoneNumber(), address.getZipcode(), address.getState(),
                address.getDistrict(), address.getCity(), address.getTypeOfAddress(),
                address.getNotes(), order);
    }

    @Override
    public AddressDto findDefaultAddressByCustomerEmail(String customerEmail) {
        try{
            Address address = addressRepo.findDefaultAddressByCustomerEmail(customerEmail);
            if(address==null){
                throw new InvalidStateException("", "No default address set");
            }
            return entityToDto(address);
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException) throw e;
            throw new InvalidStateException("", "Couldn't complete operation!");
        }
    }

    @Transactional
    @Override
    public boolean addressBelongsToCustomer(Long id, String name) {
        return addressRepo.existsById(id) &&
                addressRepo.addressBelongsToCustomer(id, name);
    }


    private AddressDto entityToDto(Address address) {
        AddressDto addressDto = new AddressDto();

        addressDto.setIsDefault(address.getIsDefault());
        addressDto.setAddressLine(address.getAddressLine());
        addressDto.setId(address.getId());
        addressDto.setCity(address.getCity());
        addressDto.setTypeOfAddress(address.getTypeOfAddress());
        addressDto.setDistrict(address.getDistrict());
        addressDto.setNotes(address.getNotes());
        addressDto.setHouseName(address.getHouseName());
        addressDto.setPhoneNumber(address.getPhoneNumber());
        addressDto.setRecipientName(address.getRecipientName());
        addressDto.setZipcode(address.getZipcode());
        addressDto.setState(address.getState());

        return addressDto;
    }

    public Address dtoToEntity(AddressDto addressDto, String email){
        try{
            return new Address(addressDto.getId(),
                    addressDto.getRecipientName(),
                    addressDto.getHouseName(),
                    addressDto.getAddressLine(),
                    addressDto.getPhoneNumber(),
                    addressDto.getZipcode(),
                    addressDto.getState(),
                    addressDto.getDistrict(),
                    addressDto.getCity(),
                    addressDto.getTypeOfAddress(),
                    addressDto.getNotes(),
                    addressDto.getIsDefault(),
                    customerService.findByEmail(email));
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, l-129");
        }
    }

}
