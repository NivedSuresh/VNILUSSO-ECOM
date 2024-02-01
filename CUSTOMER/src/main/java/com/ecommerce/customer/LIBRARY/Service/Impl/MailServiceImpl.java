package com.ecommerce.customer.LIBRARY.Service.Impl;

import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.Model.Utils.MailStructure;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CustomerRepository;
import com.ecommerce.customer.LIBRARY.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MailServiceImpl implements MailService {
    JavaMailSender mailSender;
    CustomerRepository customerRepository;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String mail, MailStructure mailStructure) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Vnilusso");
            message.setSubject(mailStructure.getSubject());
            message.setText(mailStructure.getSource());
            message.setTo(mail);

            mailSender.send(message);
        } catch (Exception e) {
            throw new MailSendException("Error sending email: " + e.getMessage());
        }
    }

    public MailStructure otpMailStructure(String code) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("OTP");

            StringBuilder source = new StringBuilder();
            source.append("Your otp code for verification is ").append(code).append(".");

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating OTP email: " + e.getMessage());
        }
    }

    public MailStructure resetPasswordMailStructure(String tokenCode, String username, boolean forgot) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("Token for Reset-Password");

            StringBuilder source = new StringBuilder();
            source.append("Dear ").append(username).append(",\n\n");

            if (customerRepository.existsByEmail(username) && !forgot) {
                source.append("To reset your password, click the following link:\n")
                        .append("http://localhost:8081/profile/reset-password?token=").append(tokenCode)
                        .append("&email=").append(username).append("\n\n")
                        .append("If you didn't request this password reset, please ignore this email.");
            } else if (customerRepository.existsByEmail(username) && forgot) {
                source.append("To reset your password, click the following link:\n")
                        .append("http://localhost:8081/reset-password?token=").append(tokenCode)
                        .append("&email=").append(username).append("\n\n")
                        .append("If you didn't request this password reset, please ignore this email.");
            } else {
                source.append("To reset your password, click the following link:\n")
                        .append("http://localhost:8080/admin/reset-password?token=").append(tokenCode)
                        .append("\n\n If you didn't request this password reset, please ignore this email.");
            }

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating Reset Password email: " + e.getMessage());
        }
    }

    @Override
    public MailStructure orderPlacedMailStructure(Order order) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("Order Placed");

            StringBuilder source = new StringBuilder();
            source.append("An order was placed for $").append(order.getTotalPrice()).append(".\n\n")
                    .append("We will let you know once the order is accepted. \n")
                    .append("Thank you for shopping with us.");

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating Order Placed email: " + e.getMessage());
        }
    }

    @Override
    public MailStructure orderShippedMailStructure(Order order) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("Order Shipped");

            StringBuilder source = new StringBuilder();
            source.append("Your order with order ID ").append(order.getId()).append(" was Shipped by VNILUSSO.").append("\n\n")
                    .append("The order will be delivered by our delivery executive by ").append(order.getDeliveryDate().toString()).append(".\n")
                    .append("Thank you for shopping with us.");

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating Order Shipped email: " + e.getMessage());
        }
    }

    @Override
    public MailStructure orderDeliveredMailStructure(Order order) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("Order Delivered");

            StringBuilder source = new StringBuilder();
            source.append("Your order with order ID ").append(order.getId()).append(" was Delivered by VNILUSSO.").append("\n\n")
                    .append("The order was delivered by our delivery executive today ie ").append(new Date().toLocaleString()).append(".\n")
                    .append("Thank you for shopping with us.");

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating Order Delivered email: " + e.getMessage());
        }
    }

    @Override
    public MailStructure orderCancelledMailStructure(Order order) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("Order cancelled");

            StringBuilder source = new StringBuilder();
            source.append("Your order with order ID ").append(order.getId()).append(" was cancelled.").append("\n\n")
                    .append("Your amount if paid will be refunded. Hope you'll shop with us in the future, thank you!");

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating Order Cancelled email: " + e.getMessage());
        }
    }

    public MailStructure orderAcceptedMailStructure(Order order) {
        try {
            MailStructure mailStructure = new MailStructure();
            mailStructure.setSubject("Order Accepted");

            StringBuilder source = new StringBuilder();
            source.append("Your order with order ID ").append(order.getId()).append(" was accepted by VNILUSSO.").append("\n\n")
                    .append("The order will be delivered by our delivery executive by ").append(order.getDeliveryDate().toString()).append(".\n")
                    .append("Thank you for shopping with us.");

            mailStructure.setSource(source.toString());
            return mailStructure;
        } catch (Exception e) {
            throw new MailSendException("Error creating Order Accepted email: " + e.getMessage());
        }
    }

    @Override
    public MailStructure referalMailStructure(String principalEmail, String token) {
        MailStructure mailStructure = new MailStructure();
        mailStructure.setSubject("Invite to Join Vnilusso, shop Jackets , Jerseys and Sneakers!\n");
        StringBuilder sb = new StringBuilder();
        sb.append("This is an Invite to join Vnilusso an Online shopping platform.\n")
                .append("You are Invited by ").append(principalEmail).append(" and your Invite Link will be valid for 24 hours.\n")
                .append("http://localhost:8081/signup?token=").append(token);
        mailStructure.setSource(sb.toString());
        return mailStructure;
    }
}