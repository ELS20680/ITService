package com.itservice.apigatewa.apigateway.presentationlayer.customer;

import com.itservice.apigatewa.apigateway.businesslogiclayer.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping(
            value = "",
            //sends json data
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<CustomerResponseDTO>> getAll(){
        return ResponseEntity.ok().body(customerService.getAllCustomers());
    }

    @GetMapping(
            value = "/{customerId}",
            //sends json data
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable String customerId){
        return ResponseEntity.ok().body(customerService.getCustomerById(customerId));
    }

    @PostMapping(
            value = "",
            //sends json data
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequestDTO));
    }

    @PutMapping(
            value = "/{customerId}",
            //sends json data
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@RequestBody CustomerRequestDTO customerRequestDTO, @PathVariable String customerId){
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerRequestDTO));
    }

    @DeleteMapping(
            value = "/{customerId}"
    )
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
