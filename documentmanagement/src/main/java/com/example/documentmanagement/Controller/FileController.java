package com.example.documentmanagement.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.documentmanagement.Model.Document;
import com.example.documentmanagement.service.FileService;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String category = fileService.processFile(file);
        fileService.saveFileToCategory(file, category);
        return ResponseEntity.ok("File uploaded successfully");
    }
    @RequestMapping(method = RequestMethod.OPTIONS)
    public void handleOptions() {
        // This method is intentionally left empty to handle preflight requests
    }
    
    @GetMapping
    public ResponseEntity<Map<String, List<Document>>> getFilesByCategory() {
        Map<String, List<Document>> filesByCategory = fileService.getFilesByCategory();
        return ResponseEntity.ok(filesByCategory);
    }
}