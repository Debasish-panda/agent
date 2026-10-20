package com.democommerce.backend.tools;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class MockCrmProvider implements CrmProvider {
    @Override
    public OrderStatus getOrderStatus(String orderId) {
        if ("ORD-10001".equalsIgnoreCase(orderId)) {
            return new OrderStatus(orderId, "OUT_FOR_DELIVERY", LocalDate.now().plusDays(1),
                    "Order ORD-10001 is out for delivery and expected tomorrow.");
        }
        return new OrderStatus(orderId, "NOT_FOUND", null, "I couldn't find that order in the demo order system.");
    }
}
