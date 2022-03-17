package com.demo.restapi.controller;

import com.demo.restapi.payload.MediaUploadResponse;
import com.demo.restapi.payload.PagedResponse;
import com.demo.restapi.payload.request.MediaRequest;
import com.demo.restapi.payload.MediaResponse;
import com.demo.restapi.security.CurrentUser;
import com.demo.restapi.security.UserPrincipal;
import com.demo.restapi.service.MediaService;
import com.demo.restapi.service.StorageService;
import com.demo.restapi.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/medias")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public PagedResponse<MediaResponse> getAllPhotos(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return mediaService.getAllMedias(page, size);
    }

    @PostMapping
    public ResponseEntity<MediaResponse> addMedia(@Valid @RequestBody MediaRequest mediaRequest,
                                                  @CurrentUser UserPrincipal currentUser) {
        MediaResponse mediaResponse = mediaService.addMedia(mediaRequest, currentUser);

        return new ResponseEntity< >(mediaResponse, HttpStatus.OK);
    }

    @GetMapping("/get-file-url")
    public ResponseEntity<Object> getFileUrl(@RequestParam("fileName") String fileName) {
        return new ResponseEntity<>(storageService.findByName(fileName), HttpStatus.OK);
    }

    @GetMapping("/get-presign-url")
    public ResponseEntity<MediaUploadResponse> generatePresignUrl(@RequestParam("extension") String extension) {
        return new ResponseEntity<>(storageService.generatePresignedUrl(extension), HttpStatus.OK);
    }

}
