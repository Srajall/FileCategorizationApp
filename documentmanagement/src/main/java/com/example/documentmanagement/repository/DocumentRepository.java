package com.example.documentmanagement.repository;

import com.example.documentmanagement.Model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}