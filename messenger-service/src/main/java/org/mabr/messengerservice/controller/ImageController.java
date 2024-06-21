package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.serivce.file.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping()
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) {
        var fileName = imageService.upload(file);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        var image = imageService.getImage(imageName);
        var header = new HttpHeaders();
        header.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(image, header, HttpStatus.OK);
    }

    @PostMapping("/{imageName}/delete")
    public ResponseEntity<?> delete(@PathVariable String imageName) {
        imageService.deleteImage(imageName);
        return ResponseEntity.ok("file was deleted successfully");
    }
}
