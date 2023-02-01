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
    @Value("${application.file.path}")
    private String filePath;

    @Value("${application.file.name}")
    private String fileName;

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
    public boolean saveToFile(String json) {
        try {
            cleanDataFile();
            Files.writeString(Path.of(filePath, fileName), json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public File getDataFile() {
        return new File(filePath + "/" + fileName);
    }

    private boolean cleanDataFile() {
        Path path = Path.of(filePath, fileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

