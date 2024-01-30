package com.ecommerce.admin.LIBRARY.Service.Impl;

import com.ecommerce.admin.LIBRARY.Model.User.Order;
import com.ecommerce.admin.LIBRARY.Model.User.OrderItem;
import com.ecommerce.admin.LIBRARY.Service.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final String[] HEADERS = {
            "order_id", "order_date", "delivery_date",
            "order_status", "payment_method", "quantity", "tax",
            "total_price", "recipient_name", "deducted_from_wallet",
            "payment_status", "payment_amount", "coupon", "order_items"
    };


    public ByteArrayInputStream getOrderAsExcel(String orderStatus, List<Order> orders){
        try{
            return getOrdersAsExcelHelper(orderStatus, orders);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    public ByteArrayInputStream getOrdersAsExcelHelper(String orderStatus, List<Order> orders) throws IOException {
        try (Workbook workbook = new HSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("orders_data");
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);

            for (int i = 0; i < HEADERS.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (Order order : orders) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowIndex);
                dataRow.createCell(0).setCellValue(order.getId());
                dataRow.createCell(1).setCellValue(order.getOrderDate().toString());
                dataRow.createCell(2).setCellValue(order.getDeliveryDate().toString());
                dataRow.createCell(3).setCellValue(order.getOrderStatus());
                dataRow.createCell(4).setCellValue(order.getPaymentMethod());
                dataRow.createCell(5).setCellValue(order.getQuantity());
                dataRow.createCell(6).setCellValue(order.getTax());
                dataRow.createCell(7).setCellValue(order.getTotalPrice());
                dataRow.createCell(8).setCellValue(order.getOrderAddress().getRecipientName());
                dataRow.createCell(9).setCellValue(order.getDeductedFromWallet() != null ? order.getDeductedFromWallet() : 0.0);
                dataRow.createCell(10).setCellValue(order.getPayment().getStatus());
                dataRow.createCell(11).setCellValue(order.getPayment().getAmount());
                dataRow.createCell(12).setCellValue(order.getCoupon() == null ? "Not Used" : order.getCoupon().getCoupon());

                StringBuilder orderItems = new StringBuilder();
                for(OrderItem orderItem : order.getOrderItems()){
                    orderItems.append(orderItem.getProduct().getName()).append(" ");
                }
                dataRow.createCell(13).setCellValue(orderItems.toString());

                rowIndex++;
            }
            workbook.write(output);
            return new ByteArrayInputStream(output.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
