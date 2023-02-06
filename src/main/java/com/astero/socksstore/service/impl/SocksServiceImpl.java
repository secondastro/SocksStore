package com.astero.socksstore.service.impl;

import com.astero.socksstore.model.Color;
import com.astero.socksstore.model.Operation.Operation;
import com.astero.socksstore.model.Operation.OperationType;
import com.astero.socksstore.model.Socks;
import com.astero.socksstore.service.FileService;
import com.astero.socksstore.service.OperationService;
import com.astero.socksstore.service.SocksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SocksServiceImpl implements SocksService {

    private static HashMap<Color, HashMap<Integer, HashMap<Integer, Integer>>> socksMap;

    private final FileService fileService;

    private final OperationService operationService;

    public SocksServiceImpl(FileService fileService, OperationService operationService) {
        this.fileService = fileService;
        this.operationService = operationService;
    }

    @PostConstruct
    public void init() {
        try {
            readFromFile();
            operationService.readOperationsFromFile();
        } catch (Exception e) {
            System.out.println("Файл еще не создан");
            e.printStackTrace();
        }
    }

    @Override
    public int addSocks(Socks socks) {
        String color = socks.getColor().name();
        int size = socks.getSize();
        int cottonPart = socks.getCottonPart();
        int quantity = socks.getQuantity();


        HashMap<Integer, Integer> countMap = getCountMap(color, size, cottonPart, quantity);
        if (countMap.containsKey(cottonPart)) {
            int newCount = countMap.get(cottonPart) + quantity;
            countMap.put(cottonPart, newCount);
        } else {
            countMap.put(cottonPart, quantity);
        }

        HashMap<Integer, HashMap<Integer, Integer>> sizeMap = socksMap.getOrDefault(socks.getColor(), new HashMap<>());
        sizeMap.put(size, countMap);
        socksMap.put(socks.getColor(), sizeMap);
        saveToFile();
        operationService.addOperationToList(new Operation(OperationType.ADD, LocalDateTime.now().toString(), socks));
        return countMap.get(cottonPart);
    }


    @Override
    public int removeSocks(Socks socks) {
        String color = socks.getColor().name();
        int size = socks.getSize();
        int cottonPart = socks.getCottonPart();
        int quantity = socks.getQuantity();

        int newCount = removeSocksFromMap(color, size, cottonPart, quantity);
        socksMap.get(socks.getColor()).get(size).put(cottonPart, newCount);
        saveToFile();
        operationService.addOperationToList(new Operation(OperationType.SELL, LocalDateTime.now().toString(), socks));
        return newCount;
    }

    @Override
    public int writeOffSocks(Socks socks) {
        String color = socks.getColor().name();
        int size = socks.getSize();
        int cottonPart = socks.getCottonPart();
        int quantity = socks.getQuantity();

        int newCount = removeSocksFromMap(color, size, cottonPart, quantity);
        socksMap.get(socks.getColor()).get(size).put(cottonPart, newCount);
        saveToFile();
        operationService.addOperationToList(new Operation(OperationType.WRITE_OFF, LocalDateTime.now().toString(), socks));
        return newCount;
    }


    @Override
    public int getQuantity(String color, int size, int cottonMin, int cottonMax) {
        HashMap<Integer, Integer> countMap = getCountMap(color, size, 1, 1);
        int count = 0;
        for (int i : countMap.keySet()) {
            if (i >= cottonMin && i <= cottonMax) {
                count += countMap.get(i);
            }
        }
        return count;
    }

    @Override
    public void readFromFile() {
        String json = fileService.readFromFile();
        try {
            socksMap = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(socksMap);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<Integer, Integer> getCountMap(String color, int size, int cottonPart, int quantity) {
        checkParams(color, size, cottonPart, quantity);
        Color localColor = Color.valueOf(color);
        HashMap<Integer, HashMap<Integer, Integer>> sizeMap = socksMap.getOrDefault(localColor, new HashMap<>());
        return sizeMap.getOrDefault(size, new HashMap<>());
    }

    private int removeSocksFromMap(String color, int size, int cottonPart, int quantity) {
        HashMap<Integer, Integer> countMap = getCountMap(color, size, cottonPart, quantity);
        if (!countMap.containsKey(cottonPart)) {
            throw new IllegalArgumentException("Носков с такими параметрами нет");
        }
        if (quantity > countMap.get(cottonPart)) {
            throw new IllegalArgumentException("На складе нет такого количества носков с заданными параметрами");
        }
        return countMap.get(cottonPart) - quantity;

    }

    private static void checkParams(String color, int size, int cottonPart, int quantity) {
        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("Не указан цвет носков");
        }
        try {
            Color.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Уточните цвет товара (RED/BLACK/WHITE/YELLOW/BLUE)");
        }
        if (size < 32 || size > 46) {
            throw new IllegalArgumentException("Уточните размер (от 32 до 46)");
        }
        if (cottonPart < 0 || cottonPart > 100) {
            throw new IllegalArgumentException("Уточните процент хлопка");
        }
        if (quantity < 1 || quantity > 1000) {
            throw new IllegalArgumentException("Уточните количество единиц товара (от 1 до 100000");
        }
    }
}

