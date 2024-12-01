package com.example.documentmanagement.service;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TextSummarizationService {

    private static final Logger logger = LoggerFactory.getLogger(TextSummarizationService.class);

    private SentenceModel sentenceModel;
    private TokenizerModel tokenizerModel;

    public TextSummarizationService() {
        logger.info("Initializing TextSummarizationService");
        try (FileInputStream sentenceModelStream = new FileInputStream("src/main/resources/models/opennlp-en-ud-ewt-sentence-1.2-2.5.0.bin");
             FileInputStream tokenizerModelStream = new FileInputStream("src/main/resources/models/opennlp-en-ud-ewt-tokens-1.2-2.5.0.bin")) {
            sentenceModel = new SentenceModel(sentenceModelStream);
            tokenizerModel = new TokenizerModel(tokenizerModelStream);
            logger.info("Models loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load OpenNLP models", e);
            throw new RuntimeException("Failed to load OpenNLP models", e);
        }
    }

    public String summarizeText(MultipartFile file) throws IOException {
        String text;
        if (file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            text = extractTextFromDocx(file);
        } else {
            text = new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        logger.info("Text to be summarized: {}", text);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);

        String[] sentences = sentenceDetector.sentDetect(text);
        StringBuilder summary = new StringBuilder();

        for (int i = 0; i < Math.min(3, sentences.length); i++) {
            summary.append(sentences[i]).append(" ");
        }

        String result = summary.toString().trim();
        logger.info("Generated summary: {}", result);
        return result;
    }

    private String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }
}