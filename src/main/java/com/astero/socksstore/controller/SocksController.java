package com.astero.socksstore.controller;

import com.astero.socksstore.model.Socks;
import com.astero.socksstore.service.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/socks")
@Tag(name = "Учет на складе носков", description = "Операции добавления, отпуска, списания и получения остатка")
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
    @Operation(
            summary = "Получение остатка товара по параметрам запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Товара с такими параметрами нет"

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка на сервере, попробуйте позже"
            )

    })
    public ResponseEntity<String> getQuantity(@RequestParam
                                              @Parameter(
                                                      description = "BLACK,WHITE,YELLOW, RED, BLUE")
                                              String color,
                                              @Parameter(
                                                      description = "от 32 до 46 (включительно)")
                                              int size,
                                              @Parameter(
                                                      description = "Мин. процент хлопка"
                                              )
                                              int cottonMin,
                                              @Parameter(
                                                      description = "Макс. процент хлопка"
                                              )
                                              int cottonMax) {
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
    @Operation(
            summary = "Добавление количества товара по передаваемому телу запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Товара с такими параметрами нет"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка на сервере, попробуйте позже"
            )
    })
    public ResponseEntity<String> addSocks(@RequestBody Socks socks) {
        try {
            int i = socksService.addSocks(socks);
            return ResponseEntity.ok("Добавлено. На складе " + i + " пар носков с данными параметрами");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage() + '\n' + s);
        }
    }

    @PutMapping("/sell")
    @Operation(
            summary = "Отпуск количества товара по передаваемому телу запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Товара с такими параметрами нет"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка на сервере, попробуйте позже"
            )
    })
    public ResponseEntity<String> sellSocks(@RequestBody Socks socks) {
        try {
            int i = socksService.removeSocks(socks);
            return ResponseEntity.ok("Продано " + socks.getQuantity() + " пар носков. Осталось " + i + " пар с такими параметрами");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage() + '\n' + s);
        }
    }

    @DeleteMapping("/write_off")
    @Operation(
            summary = "Списание количества товара по передаваемому телу запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар найден"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Товара с такими параметрами нет"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка на сервере, попробуйте позже"
            )
    })

    public ResponseEntity<String> writeOffSocks(@RequestBody Socks socks) {
        try {
            int i = socksService.removeSocks(socks);
            return ResponseEntity.ok("Списано " + socks.getQuantity() + " пар носков. Осталось " + i + " пар с такими параметрами");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage() + '\n' + s);
        }
    }
}
