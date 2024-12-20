package ru.project.sock.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.project.group.CreateGroup;
import ru.project.sock.dto.SocksRequestDto;
import ru.project.sock.dto.SocksResponseDto;
import ru.project.sock.service.SocksService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocksController {

    SocksService socksService;

    @PostMapping("/income")
    @Operation(summary = "Регистрация прихода носков")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SocksResponseDto> save(
            @Validated(CreateGroup.class) @Valid @RequestBody SocksRequestDto socksRequestDto) {
        log.info("Запрос на регистрацию прихода носков");
        return ResponseEntity.status(HttpStatus.CREATED).body(socksService.save(socksRequestDto));
    }

    @PostMapping("/outcome")
    @Operation(summary = "Регистрация отпуска носков")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SocksResponseDto> release(
            @Validated(CreateGroup.class) @Valid @RequestBody SocksRequestDto socksRequestDto) {
        log.info("Запрос на регистрацию отпуска носков");
        return ResponseEntity.status(HttpStatus.CREATED).body(socksService.release(socksRequestDto));
    }

    @GetMapping
    @Operation(summary = "Получение подборки носков по определенным фильтрам")
    public ResponseEntity<List<SocksResponseDto>> findByFilters(@RequestParam(required = false) String color,
                                                                @RequestParam(required = false, defaultValue = "equal") String operator,
                                                                @RequestParam(required = false) Integer cottonPercentage,
                                                                @RequestParam(required = false) Integer minCottonPercentage,
                                                                @RequestParam(required = false) Integer maxCottonPercentage,
                                                                @RequestParam(required = false, defaultValue = "cottonPercentage") String sortParam) {
        log.info("Запрос на получение подборки носков по определенным фильтрам");
        if (sortParam.isBlank() || sortParam.isEmpty()) {
            sortParam = "amount";
        }
        if (sortParam.isBlank()) {
        }
        final Sort sort = Sort.by(Sort.Direction.DESC, sortParam);

        return ResponseEntity.status(HttpStatus.OK).body(socksService.findByFilters(color, operator,
                cottonPercentage, minCottonPercentage, maxCottonPercentage, sort));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление данных носков")
    public ResponseEntity<SocksResponseDto> update(@PathVariable @Positive Long id,
                                                   @Valid @RequestBody SocksRequestDto socksRequestDto) {
        log.info("Запрос на обновление данных носков");

        return ResponseEntity.status(HttpStatus.OK).body(socksService.update(id, socksRequestDto));
    }

    @PostMapping("/batch")
    @Operation(summary = "Загрузка партий носков из файла")
    public ResponseEntity<String> saveFromFile(@RequestParam("file") MultipartFile file) {
        log.info("Запрос на загрузку партий носков из файла");
        socksService.saveFromFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body("Партии носков успешно загружены из файла");
    }
}
