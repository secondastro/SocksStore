package com.astero.socksstore.service.impl;

import com.astero.socksstore.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileServiceImpl implements FileService {
    @Value("${application.socks.file.path}")
    private String filePath;

    @Value("${application.socks.file.name}")
    private String fileName;


    @Value("${application.operations.file.name}")
    private String operationsFileName;

    @Override
    public String readFromFile() {
        Path path = Path.of(filePath, fileName);
        try {
            if (!Files.exists(path)) {
                Files.writeString(path, "{}");
            }
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public String readOperationsFromFile() {
        Path path = Path.of(filePath, operationsFileName);
        try {
            if (!Files.exists(path)) {
                Files.writeString(path, "{}");
            }
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void saveToFile(String json) {
        try {
            cleanDataFile();
            Files.writeString(Path.of(filePath, fileName), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getSocksFile() {
        return new File(filePath + "/" + fileName);
    }

    @Override
    public File getOperationFile() {
        return new File(filePath + "/" + operationsFileName);
    }

    @Override
    public void cleanDataFile() {
        Path path = Path.of(filePath, fileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void cleanOperationsFile() {
        Path path = Path.of(filePath, operationsFileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveOperationToFile(String json) {
        try {
            cleanOperationsFile();
            Files.writeString(Path.of(filePath, operationsFileName),  json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

