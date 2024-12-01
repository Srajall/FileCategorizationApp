package com.example.documentmanagement.service;

import com.example.documentmanagement.Model.Document;
import com.example.documentmanagement.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TextSummarizationService textSummarizationService;

    public String processFile(MultipartFile file) {
        String category = "Unknown";
        try {
            if (isTextFile(file)) {
                category = summarizeText(file);
            } else if (isAudioOrVideoFile(file)) {
                category = transcribeAudioOrVideo(file);
            }
            saveFileToCategory(file, category);
        } catch (IOException e) {
            logger.error("Error processing file", e);
        }
        return category;
    }

    public String summarizeText(MultipartFile file) throws IOException {
        String summary = textSummarizationService.summarizeText(file);
        logger.info("Generated summary: {}", summary);
        String category = categorizeContent(summary);
        saveDocumentMetadata(file, summary, category); // Save the actual summary and category
        return category;
    }

    public String transcribeAudioOrVideo(MultipartFile file) {
        // Placeholder for actual transcription logic
        String transcription = "Sample transcription";
        logger.info("Generated transcription: {}", transcription);
        String category = categorizeContent(transcription);
        saveDocumentMetadata(file, transcription, category); // Save the actual transcription and category
        return category;
    }

    public String categorizeContent(String content) {
        // Logic to determine category based on content
        logger.info("Categorizing content: {}", content);
        return "General";
    }

    public void saveFileToCategory(MultipartFile file, String category) {
        try {
            Path categoryPath = Paths.get("uploads/" + category);
            if (!Files.exists(categoryPath)) {
                Files.createDirectories(categoryPath);
            }
            Path filePath = categoryPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File saved to category: {}", category);
        } catch (IOException e) {
            logger.error("Error saving file to category", e);
        }
    }

    public void saveDocumentMetadata(MultipartFile file, String summary, String category) {
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setSummary(summary); // Save the actual summary
        document.setCategory(category);
        documentRepository.save(document);
        logger.info("Document metadata saved: fileName={}, summary={}, category={}", file.getOriginalFilename(), summary, category);
    }

    private boolean isTextFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("text/plain") || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private boolean isAudioOrVideoFile(MultipartFile file) {
        return file.getContentType().startsWith("audio/") || file.getContentType().startsWith("video/");
    }

	public Map<String, List<Document>> getFilesByCategory() {
		List<Document> documents = documentRepository.findAll();
        return documents.stream().collect(Collectors.groupingBy(Document::getCategory));
	}
}