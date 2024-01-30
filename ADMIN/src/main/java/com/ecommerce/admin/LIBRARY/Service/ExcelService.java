package com.ecommerce.admin.LIBRARY.Service;

import com.ecommerce.admin.LIBRARY.Model.User.Order;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelService {
    ByteArrayInputStream getOrderAsExcel(String orderStatus, List<Order> orders);
}
