package com.ecommerce.admin.LIBRARY.Service;

import com.ecommerce.admin.LIBRARY.Dtos.StatisticsDto;
import com.ecommerce.admin.LIBRARY.Model.User.Order;

import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream createInvoiceForCustomer(Order order);

    public ByteArrayInputStream getSalesReport(Double amountFromDeliveredOrdersPastReturn,
                                               StatisticsDto statisticsDto, Double revenue);
}
