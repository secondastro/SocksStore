package com.astero.socksstore.service.impl;

import com.astero.socksstore.model.Color;
import com.astero.socksstore.model.Socks;
import com.astero.socksstore.service.FileService;
import com.astero.socksstore.service.SocksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class SocksServiceImpl implements SocksService {

    private static HashMap<Color, HashMap<Integer, ArrayList<Integer>>> socksMap;

    private FileService fileService;

    public SocksServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        try {
            readFromFile();
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


        ArrayList<Integer> socksList = getList(color, size, cottonPart, quantity);
        for (int i = 0; i < quantity; i++) {
            socksList.add(cottonPart);
        }
        HashMap<Integer, ArrayList<Integer>> tempMap = socksMap.getOrDefault(socks.getColor(), new HashMap<>());
        tempMap.put(size, socksList);
        socksMap.put(socks.getColor(), tempMap);
        saveToFile();
        return socksList.size();
    }


    @Override
    public int removeSocks(Socks socks) {
        String color = socks.getColor().name();
        int size = socks.getSize();
        int cottonPart = socks.getCottonPart();
        int quantity = socks.getQuantity();

        ArrayList<Integer> socksList = removeSocksFromList(color, size, cottonPart, quantity);
        socksMap.get(socks.getColor()).put(size, socksList);
        saveToFile();
        return socksList.size();
    }


    @Override
    public int getQuantity(String color, int size, int cottonMin, int cottonMax) {
        ArrayList<Integer> socksList = getList(color, size, 1, 1);
        int counter = 0;
        for (Integer i : socksList) {
            if (i >= cottonMin && i <= cottonMax) {
                counter++;
            }
        }
        return counter;
    }



    private void readFromFile() {
        String json = fileService.readFromFile();
        try {
            HashMap<Color, HashMap<Integer, ArrayList<Integer>>> hashMap =
                    new ObjectMapper().readValue(json, new TypeReference<>() {
                    });
            socksMap = hashMap;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void addToFile() {

    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(socksMap);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private ArrayList<Integer> getList(String color, int size, int cottonPart, int quantity) {
        checkParams(color, size, cottonPart, quantity);
        Color localColor = Color.valueOf(color);
        HashMap<Integer, ArrayList<Integer>> sizeMap = socksMap.getOrDefault(localColor, new HashMap<>());
        return sizeMap.getOrDefault(size, new ArrayList<>());
    }

    private ArrayList<Integer> removeSocksFromList(String color, int size, int cottonPart, int quantity) {
        ArrayList<Integer> socksList = getList(color, size, cottonPart, quantity);
        if (!socksList.contains(cottonPart)) {
            throw new IllegalArgumentException("Носков с такими параметрами нет");
        }
        int equals = 0;
        for (Integer integer : socksList) {
            if (integer == cottonPart) {
                equals += 1;
            }
            if (equals == quantity) {
                break;
            }
        }
        if (equals < quantity) {
            throw new IllegalArgumentException("На складе нет такого количества носков с заданными параметрами");
        }
        for (int i = 0; i < quantity; i++) {
            if (socksList.get(i) == cottonPart) {
                socksList.remove(i);
            }
        }
        return socksList;
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

