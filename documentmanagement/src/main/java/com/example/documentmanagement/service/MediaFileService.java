package com.example.documentmanagement.service;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class MediaFileService {

    private static final Logger logger = LoggerFactory.getLogger(MediaFileService.class);

    @Autowired
    private TextSummarizationService textSummarizationService;

    public String processMedia(MultipartFile file) {
        try {
            String transcription = transcribeAudio(file);
            logger.info("Transcription: {}", transcription);
            String summary = textSummarizationService.summarizeText(transcription);
            logger.info("Summary: {}", summary);
            return summary;
        } catch (Exception e) {
            logger.error("Error processing media file", e);
            return "Error processing media file";
        }
    }

    private String transcribeAudio(MultipartFile file) throws IOException {
        SpeechClient speechClient = SpeechClient.create();
        ByteString audioBytes = ByteString.readFrom(file.getInputStream());

        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        RecognizeResponse response = speechClient.recognize(config, audio);
        StringBuilder transcription = new StringBuilder();
        for (SpeechRecognitionResult result : response.getResultsList()) {
            transcription.append(result.getAlternativesList().get(0).getTranscript());
        }
        speechClient.close();
        return transcription.toString();
    }
}
