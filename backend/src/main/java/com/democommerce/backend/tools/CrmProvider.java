package com.democommerce.backend.tools;

import java.time.LocalDate;

public interface CrmProvider {
    OrderStatus getOrderStatus(String orderId);

    record OrderStatus(String orderId, String status, LocalDate expectedDelivery, String message) {}
}
