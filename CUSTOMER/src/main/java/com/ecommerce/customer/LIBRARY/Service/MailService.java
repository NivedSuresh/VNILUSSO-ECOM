package com.ecommerce.customer.LIBRARY.Service;

import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.Model.Utils.MailStructure;

public interface MailService {
    public void sendMail(String mail, MailStructure mailStructure);
    public MailStructure otpMailStructure(String code);
    MailStructure resetPasswordMailStructure(String tokenCode, String username, boolean forgot);
    MailStructure orderPlacedMailStructure(Order order);
    MailStructure orderShippedMailStructure(Order order);
    MailStructure orderDeliveredMailStructure(Order order);
    MailStructure orderCancelledMailStructure(Order order);
    MailStructure orderAcceptedMailStructure(Order order);

    MailStructure referalMailStructure(String principalEmail, String token);
}
