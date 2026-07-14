package com.ai.rag_mini_project.helper;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentLoader implements ApplicationRunner {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Value("classpath:Company_Policy_Handbook_No_FAQ.pdf")
    public Resource companyPolicyPDF;

    public DocumentLoader(VectorStore vectorStore,
                          JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadPDFToVectorDB();
    }

    public void loadPDFToVectorDB() {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_store",
                Integer.class
        );

        if(count != null && count > 0) {
            System.out.println("PDF already indexed. Skipping...");
            return;
        }

        //1 - Load the text

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(companyPolicyPDF,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .build());

        List<Document> documents = pdfReader.read();

        documents.forEach(
                document -> System.out.println(document)
        );


        //2 - Text Splitting
        TokenTextSplitter splitter = TokenTextSplitter
                .builder()
                .withChunkSize(50)
                .withKeepSeparator(true)
                .build();

        List<Document> splittedDocuments = splitter.split(documents);

        //3 - load the data into the VectorDB
        vectorStore.add(splittedDocuments);

        System.out.println("PDF indexed successfully.");

    }
}
