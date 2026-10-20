package com.democommerce.backend.customers;

import com.democommerce.backend.ApiDtos;
import com.democommerce.backend.AppUser;
import com.democommerce.backend.Customer;
import com.democommerce.backend.CustomerRepository;
import com.democommerce.backend.SensitiveDataRedactor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository customers;
    private final SensitiveDataRedactor redactor;
    public CustomerController(CustomerRepository customers, SensitiveDataRedactor redactor) {
        this.customers = customers;
        this.redactor = redactor;
    }

    @GetMapping
    public List<ApiDtos.CustomerView> list(@AuthenticationPrincipal AppUser user) {
        return customers.findAllByTenantIdOrderByCreatedAtDesc(user.tenantId).stream().map(this::view).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.CustomerView create(@AuthenticationPrincipal AppUser user, @Valid @RequestBody ApiDtos.CustomerRequest request) {
        Customer customer = new Customer();
        customer.tenantId = user.tenantId;
        customer.name = redactor.redact(request.name());
        customer.email = request.email();
        customer.phone = request.phone();
        customer.createdAt = Instant.now();
        return view(customers.save(customer));
    }

    private ApiDtos.CustomerView view(Customer customer) {
        return new ApiDtos.CustomerView(customer.id, customer.name, customer.email, customer.phone, customer.createdAt);
    }
}
