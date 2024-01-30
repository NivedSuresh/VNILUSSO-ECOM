package com.ecommerce.admin.LIBRARY.Service.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.StatisticsDto;
import com.ecommerce.admin.LIBRARY.Model.User.Order;
import com.ecommerce.admin.LIBRARY.Service.PdfService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public ByteArrayInputStream createInvoiceForCustomer(Order order) {
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 17, Color.BLACK);
            Font smallerTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17, Color.BLACK);

            Paragraph title = new Paragraph("Invoice For Vnilusso Order "+order.getId()+" (Payment Status : "+order.getPayment().getStatus()+")", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            StringBuilder orderBasics = new StringBuilder();
            orderBasics.append("\n\n Order Total : $").append(order.getTotalPrice())
                    .append("\n Payment Method : ").append(order.getPaymentMethod())
                    .append("\n Order Status : ").append(order.getOrderStatus())
                    .append("\n Order Date  : ").append(order.getOrderDate())
                    .append("\n Delivery Date : ").append(order.getDeliveryDate())
                    .append("\n Coupon Used : ").append(order.getCoupon()==null?"Not Used":order.getCoupon().getCoupon());
            document.add(new Paragraph(orderBasics.toString(), paragraphFont));


            document.add(new Paragraph("\n\nPayment Details : ", smallerTitle));
            document.add(new Paragraph(order.getPayment().toString()+'\n'));

            document.add(new Paragraph("\n\n Order Items : ", smallerTitle));
            document.add(new Paragraph(order.getOrderItems().toString()));

            document.close();
            out.close();
            return new ByteArrayInputStream(out.toByteArray());
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public ByteArrayInputStream getSalesReport(Double amountFromDeliveredOrdersPastReturn,
                                               StatisticsDto statisticsDto, Double revenue) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        StringBuilder sb = new StringBuilder();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
        Font smallerTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK);

        document.addTitle("Sales Report");
        Paragraph title = new Paragraph("Sales Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);


        StringBuilder content = new StringBuilder();
        content.append("\n\nOrders : ").append(statisticsDto.getTotalOrders()).append("\n");
        content.append("Categories : ").append(statisticsDto.getTotalCategories()).append("\n");
        content.append("Products : ").append(statisticsDto.getTotalProducts()).append("\n");
        content.append("Revenue : $").append(revenue).append("\n");
        content.append("Amount Gained from Orders Past Return Date : $").append(amountFromDeliveredOrdersPastReturn).append("\n\n");
        Paragraph paragraph = new Paragraph(content.toString(), smallerTitle);
        document.add(paragraph);


        Paragraph mostSoldProducts = new Paragraph("\nMost Sold Products\n", smallerTitle);
        mostSoldProducts.setAlignment(Element.ALIGN_CENTER);
        document.add(mostSoldProducts);
        for(StatisticsDto.MostSoldProducts product : statisticsDto.getMostSoldProducts()){
            sb.append(product.getProduct().getName()).append("   -  Sales : $")
                    .append(product.getSales()).append("     -      Count : ")
                    .append(product.getCount()).append("\n");
        }
        Paragraph para = new Paragraph(sb.toString(), paragraphFont);
        para.setAlignment(Element.ALIGN_CENTER);
        document.add(para);


        Paragraph salesPerCategoryP = new Paragraph("\n\nSales Per Category\n", smallerTitle);
        salesPerCategoryP.setAlignment(Element.ALIGN_CENTER);
        document.add(salesPerCategoryP);

        sb = new StringBuilder();

        for(String category : statisticsDto.getSalesPerCategory().keySet()){
            sb.append(category).append(" - $").append(statisticsDto.getSalesPerCategory().get(category)).append("\n");
        }

        paragraph = new Paragraph(sb.toString(), paragraphFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        para = new Paragraph("\n\nOrder Status\n", smallerTitle);
        para.setAlignment(Element.ALIGN_CENTER);
        document.add(para);
        sb = new StringBuilder();
        for(String orderDStatus : statisticsDto.getOrderStatusCount().keySet()){
            sb.append(orderDStatus).append(" : ").
                    append(statisticsDto.getOrderStatusCount().get(orderDStatus)).append("\n");
        }
        para = new Paragraph(sb.toString(), paragraphFont);
        para.setAlignment(Element.ALIGN_CENTER);
        document.add(para);

        document.close();

        try{
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
