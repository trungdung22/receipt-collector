package com.demo.restapi.service;

import com.demo.restapi.model.Receipt;
import com.demo.restapi.payload.ReceiptResponse;
import com.demo.restapi.payload.ApiResponse;
import com.demo.restapi.payload.PagedResponse;
import com.demo.restapi.payload.request.ReceiptRequest;
import com.demo.restapi.security.UserPrincipal;
import org.springframework.http.ResponseEntity;

public interface ReceiptService {
    PagedResponse<ReceiptResponse> getAllReceipts(UserPrincipal currentUser, int page, int size);
    ResponseEntity<Receipt> addReceipt(ReceiptRequest receiptRequest, UserPrincipal currentUser);
    ResponseEntity<Receipt> getReceipt(Long id);
    ResponseEntity<ReceiptResponse> updateReceipt(Long id, ReceiptRequest newBill, UserPrincipal currentUser);
    ResponseEntity<ApiResponse> deleteReceipt(Long id, UserPrincipal currentUser);
    PagedResponse<Receipt> getUserReceipts(String username, int page, int size);
}
