package com.ecommerce.customer.LIBRARY.Service;

import com.ecommerce.customer.LIBRARY.Model.User.Order;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelService {
    ByteArrayInputStream getOrderAsExcel(String orderStatus, List<Order> orders);
}
