package com.demo.restapi.service.impl;

import com.demo.restapi.exception.RestapiException;
import com.demo.restapi.exception.ResourceNotFoundException;
import com.demo.restapi.model.Media;
import com.demo.restapi.model.Receipt;
import com.demo.restapi.model.User;
import com.demo.restapi.payload.MediaResponse;
import com.demo.restapi.payload.ReceiptResponse;
import com.demo.restapi.payload.ApiResponse;
import com.demo.restapi.payload.PagedResponse;
import com.demo.restapi.payload.request.MediaRequest;
import com.demo.restapi.payload.request.ReceiptRequest;
import com.demo.restapi.repository.MediaRepository;
import com.demo.restapi.repository.ReceiptRepository;
import com.demo.restapi.repository.UserRepository;
import com.demo.restapi.security.UserPrincipal;
import com.demo.restapi.service.ReceiptService;
import com.demo.restapi.service.StorageService;
import com.demo.restapi.utils.AppUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.demo.restapi.utils.AppConstants.ID;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    private static final String CREATED_AT = "createdAt";

    private static final String RECEIPT_STR = "Bill";

    private static final String YOU_DON_T_HAVE_PERMISSION_TO_MAKE_THIS_OPERATION = "You don't have permission to make this operation";

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public PagedResponse<ReceiptResponse> getAllReceipts(UserPrincipal currentUser, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Receipt> receipts = receiptRepository.findByUserId(currentUser.getId(), pageable);
        if (receipts.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), receipts.getNumber(), receipts.getSize(), receipts.getTotalElements(),
                    receipts.getTotalPages(), receipts.isLast());
        }

        for (Receipt receipt : receipts.getContent()) {
            List<Media> mediaList = receipt.getMedia();
            for (Media media : mediaList) {
                media.setUrl(storageService.findByName(media.getKeyName()));
            }
        }

        List<ReceiptResponse> receiptResponse = Arrays.asList(modelMapper.map(receipts.getContent(), ReceiptResponse[].class));

        return new PagedResponse<>(receiptResponse, receipts.getNumber(), receipts.getSize(), receipts.getTotalElements(), receipts.getTotalPages(),
                receipts.isLast());
    }

    @Override
    public ResponseEntity<Receipt> addReceipt(ReceiptRequest receiptRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);

        Receipt receipt = new Receipt();
        List<MediaRequest> mediaRequests = receiptRequest.getMedias();
        modelMapper.map(receiptRequest, receipt);

        receipt.setUser(user);
        Receipt newReceipt = receiptRepository.save(receipt);
        for (MediaRequest mediaRequest : mediaRequests){
            Media media = new Media(mediaRequest.getFileName(), mediaRequest.getKeyName(), receipt);
            mediaRepository.save(media);
        }
        return new ResponseEntity<>(newReceipt, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Receipt> getReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(RECEIPT_STR, ID, id));
        return new ResponseEntity<>(receipt, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReceiptResponse> updateReceipt(Long id, ReceiptRequest newBill, UserPrincipal currentUser) {
        Receipt receipt = receiptRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(RECEIPT_STR, ID, id));
        User user = userRepository.getUser(currentUser);
        if (receipt.getUser().getId().equals(user.getId())) {
            receipt.setTitle(newBill.getTitle());
            Receipt updatedReceipt = receiptRepository.save(receipt);

            ReceiptResponse receiptResponse = new ReceiptResponse();

            modelMapper.map(updatedReceipt, receiptResponse);

            return new ResponseEntity<>(receiptResponse, HttpStatus.OK);
        }

        throw new RestapiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO_MAKE_THIS_OPERATION);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteReceipt(Long id, UserPrincipal currentUser) {
        return null;
    }

    @Override
    public PagedResponse<Receipt> getUserReceipts(String username, int page, int size) {
        return null;
    }
}
