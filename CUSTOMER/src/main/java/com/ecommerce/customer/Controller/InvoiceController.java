package com.ecommerce.customer.Controller;


import com.ecommerce.customer.LIBRARY.ProductsService.OrderService;
import com.ecommerce.customer.LIBRARY.Service.PdfService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/download")
@Controller
public class InvoiceController {

    PdfService pdfService;
    OrderService orderService;

    public InvoiceController(PdfService pdfService, OrderService orderService) {
        this.pdfService = pdfService;
        this.orderService = orderService;
    }

    @ResponseBody
    @PostMapping("/invoice")
    public ResponseEntity<InputStreamResource> createInvoice(Long orderId){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline;file = lcwd.pdf");
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfService.
                        createInvoiceForCustomer(orderService.findById(orderId))));

    }
}
