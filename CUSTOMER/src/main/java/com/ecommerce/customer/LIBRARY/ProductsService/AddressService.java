package com.ecommerce.customer.LIBRARY.ProductsService;

import com.ecommerce.customer.LIBRARY.Dtos.AddressDto;
import com.ecommerce.customer.LIBRARY.Model.User.Address;
import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.Model.User.OrderAddress;

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
