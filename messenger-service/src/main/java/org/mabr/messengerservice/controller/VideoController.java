package org.mabr.messengerservice.controller;

import lombok.RequiredArgsConstructor;
import org.mabr.messengerservice.serivce.file.Range;
import org.mabr.messengerservice.serivce.file.VideoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpHeaders.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;
    private final Integer defaultChunkSize = 3145728;
    public static final String ACCEPTS_RANGES_VALUE = "bytes";

    @PostMapping
    public ResponseEntity<String> save(@RequestParam MultipartFile file) {
        var uuid = videoService.save(file);
        return ResponseEntity.ok(uuid);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<byte[]> fetchChunk(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
            @PathVariable String uuid) {

        var parsedRange = Range.parseHttpRangeString(range, defaultChunkSize);
        var chunkWithMetadata = videoService.fetchChunk(uuid, parsedRange);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, chunkWithMetadata.metadata().getHttpContentType())
                .header(ACCEPT_RANGES, ACCEPTS_RANGES_VALUE)
                .header(CONTENT_LENGTH, calculateContentLengthHeader(parsedRange, chunkWithMetadata.metadata().getSize()))
                .header(CONTENT_RANGE, constructContentRangeHeader(parsedRange, chunkWithMetadata.metadata().getSize()))
                .body(chunkWithMetadata.chunk());
    }

    private String calculateContentLengthHeader(Range range, long fileSize) {
        return String.valueOf(range.getRangeEnd(fileSize) - range.getRangeStart() + 1);
    }

    private String constructContentRangeHeader(Range range, long fileSize) {
        return "bytes" + range.getRangeStart() + "-" + range.getRangeEnd(fileSize) + "/" + fileSize;
    }
}
