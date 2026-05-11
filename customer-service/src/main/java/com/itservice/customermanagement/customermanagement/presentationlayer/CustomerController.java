package com.itservice.customermanagement.customermanagement.presentationlayer;


import com.itservice.customermanagement.customermanagement.businesslayer.CustomerService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll(){
        List<CustomerResponseDTO> response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getByCustomerId(@PathVariable String customerId) {
        CustomerResponseDTO response = customerService.getByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponseDTO);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable String customerId, @RequestBody CustomerRequestDTO customerRequestDTO)
    {
        CustomerResponseDTO customerResponseDTO = customerService.updateCustomer(customerId, customerRequestDTO);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}

//@PutMapping("/{assetId}")
//public ResponseEntity<AssetResponseDTO> updateAsset(@PathVariable String assetId, @RequestBody AssetRequestDTO assetRequestDTO) {
//    AssetResponseDTO assetResponseDTO = assetService.updateAsset(assetId, assetRequestDTO);
//    return ResponseEntity.ok(assetResponseDTO);
//}




