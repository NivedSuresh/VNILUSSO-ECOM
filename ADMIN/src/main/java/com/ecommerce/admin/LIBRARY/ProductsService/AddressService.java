package com.ecommerce.admin.LIBRARY.ProductsService;

import com.ecommerce.admin.LIBRARY.Dtos.AddressDto;
import com.ecommerce.admin.LIBRARY.Model.User.Address;
import com.ecommerce.admin.LIBRARY.Model.User.Order;
import com.ecommerce.admin.LIBRARY.Model.User.OrderAddress;

import java.util.List;

public interface AddressService {

    void saveAddress(String email, AddressDto addressDto);

    List<AddressDto> findAll();

    List<Address> findAddressByCustomer(String name);

    AddressDto findAddressById(Long id);

    boolean existsById(Long id);

    OrderAddress addressDtoToOrderAddress(AddressDto address, Order order);

    AddressDto findDefaultAddressByCustomerEmail(String customerEmail);

    boolean addressBelongsToCustomer(Long id, String name);
}
