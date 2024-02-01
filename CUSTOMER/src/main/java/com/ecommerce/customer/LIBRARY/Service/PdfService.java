package com.ecommerce.customer.LIBRARY.Service;

import com.ecommerce.customer.LIBRARY.Dtos.StatisticsDto;
import com.ecommerce.customer.LIBRARY.Model.User.Order;

import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream createInvoiceForCustomer(Order order);

    public ByteArrayInputStream getSalesReport(Double amountFromDeliveredOrdersPastReturn,
                                               StatisticsDto statisticsDto, Double revenue);
}
