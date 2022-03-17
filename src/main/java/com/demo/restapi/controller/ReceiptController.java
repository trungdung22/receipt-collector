package com.demo.restapi.controller;

import com.demo.restapi.exception.ResponseEntityErrorException;
import com.demo.restapi.model.Receipt;
import com.demo.restapi.payload.MediaResponse;
import com.demo.restapi.payload.ReceiptResponse;
import com.demo.restapi.payload.ApiResponse;
import com.demo.restapi.payload.PagedResponse;
import com.demo.restapi.payload.request.ReceiptRequest;
import com.demo.restapi.security.CurrentUser;
import com.demo.restapi.security.UserPrincipal;
import com.demo.restapi.service.MediaService;
import com.demo.restapi.service.ReceiptService;
import com.demo.restapi.utils.AppConstants;
import com.demo.restapi.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private MediaService mediaService;

    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }

    @GetMapping
    public PagedResponse<ReceiptResponse> getAllReceipts(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return receiptService.getAllReceipts(currentUser, page, size);
    }

    @PostMapping
    public ResponseEntity<Receipt> addReceipt(@Valid @RequestBody ReceiptRequest receiptRequest, @CurrentUser UserPrincipal currentUser) {
        return receiptService.addReceipt(receiptRequest, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceipt(@PathVariable(name = "id") Long id) {
        return receiptService.getReceipt(id);
    }

    @GetMapping("/{id}/medias")
    public ResponseEntity<PagedResponse<MediaResponse>> getAllMediasByReceipt(@PathVariable(name = "id") Long id,
                                                                              @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                              @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

        PagedResponse<MediaResponse> response = mediaService.getAllMediasByReceipt(id, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
