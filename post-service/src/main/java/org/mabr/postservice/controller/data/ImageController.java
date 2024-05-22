package org.mabr.postservice.controller.data;

import org.mabr.postservice.entity.data.Image;
import org.mabr.postservice.service.data.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("api/data/image")
@RequiredArgsConstructor
public class ImageController {

    private final DataService service;

    @PostMapping()
    public ResponseEntity<Image> uploadImage(@RequestParam MultipartFile file) {
        var image = service.saveImage(file);
        return ResponseEntity.ok(image);
    }

    @GetMapping("/{code}")
    public ResponseEntity<byte[]> getImage(@PathVariable String code) {
        var image = service.getImage(code);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .body(image.getData());
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable int id) {
        service.deleteImage(id);
        return ResponseEntity.ok("File successfully deleted");
    }
}
