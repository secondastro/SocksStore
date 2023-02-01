package com.astero.socksstore.controller;

import com.astero.socksstore.model.Socks;
import com.astero.socksstore.service.FileService;
import com.astero.socksstore.service.SocksService;
import io.swagger.v3.core.util.Json;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/socks")
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    String s = """
            Необходимый формат данных: 
            Цвет: RED/BLACK/YELLOW/WHITE/BLUE
            Размер: 32-46
            Процент содержания хлопка: от 0 до 100
            Количество: от 0 до 1000""";


    @GetMapping(value = "/get")
    public ResponseEntity<String> getQuantity(@RequestParam String color, int size, int cottonMin, int cottonMax) {
        try {
            int i = socksService.getQuantity(color, size, cottonMin, cottonMax);
            return ResponseEntity.ok("Осталось " + i + " пар носков с данными параметрами");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body("Некорректно введены параметры запроса" + '\n' + e.getMessage() + '\n' + s);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Ошибка на сервере");
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<String> addSocks(@RequestBody Socks socks) {
        try {
            int i = socksService.addSocks(socks);
            return ResponseEntity.ok("Добавлено. На складе " + i + " пар носков с данными параметрами");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage() + '\n' + s);
        }
    }

    @PutMapping("/sell")
    public ResponseEntity<String> sellSocks(@RequestBody Socks socks) {
        try {
            int i = socksService.removeSocks(socks);
            return ResponseEntity.ok("Продано " + socks.getQuantity() + " пар носков. Осталось " + i + " пар с такими параметрами");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()+ '\n' + s);
        }
    }

    @DeleteMapping("/write_off")
    public ResponseEntity<String> writeOffSocks(@RequestBody Socks socks) {
        try {
            int i = socksService.removeSocks(socks);
            return ResponseEntity.ok("Списано " + socks.getQuantity() + " пар носков. Осталось " + i + " пар с такими параметрами");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage() + '\n' + s);
        }
    }
}
