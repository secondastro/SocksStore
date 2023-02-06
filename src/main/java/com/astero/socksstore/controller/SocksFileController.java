package com.astero.socksstore.controller;

import com.astero.socksstore.service.FileService;
import com.astero.socksstore.service.SocksService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/socks")
public class SocksFileController {
    private final FileService fileService;
    private final SocksService socksService;

    public SocksFileController(FileService fileService, SocksService socksService) {
        this.fileService = fileService;
        this.socksService = socksService;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadDataFile(@RequestParam MultipartFile file) {
        fileService.cleanDataFile();
        File localFile = fileService.getSocksFile();
        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            IOUtils.copy(file.getInputStream(), fos);
            socksService.readFromFile();
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile() throws FileNotFoundException {
        File localFile = fileService.getSocksFile();
        if (localFile.length() != 0) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(localFile));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Socks.json\"")
                    .contentLength(localFile.length())
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
