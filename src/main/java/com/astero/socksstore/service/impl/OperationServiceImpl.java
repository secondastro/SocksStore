package com.astero.socksstore.service.impl;

import com.astero.socksstore.model.Operation.Operation;
import com.astero.socksstore.service.FileService;
import com.astero.socksstore.service.OperationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OperationServiceImpl implements OperationService {
    public static ArrayList<Operation> operationsList = new ArrayList<>();
    private final FileService fileService;

    public OperationServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void readOperationsFromFile() {
        String json = fileService.readOperationsFromFile();
        try {
            operationsList = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOperationToList(Operation operation) {
        operationsList.add(operation);
        try {
            String json = new ObjectMapper().writeValueAsString(operationsList);
            fileService.saveOperationToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
