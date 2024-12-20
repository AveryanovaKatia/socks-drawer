package ru.project.sock.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import ru.project.exception.FileProcessingException;
import ru.project.exception.NotFoundException;
import ru.project.exception.ValidationException;
import ru.project.sock.dto.SocksFileDto;
import ru.project.sock.dto.SocksRequestDto;
import ru.project.sock.dto.SocksResponseDto;
import ru.project.sock.mapper.SocksMapper;
import ru.project.sock.model.Sock;
import ru.project.sock.repository.SocksRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocksServiceImpl implements SocksService {

    SocksRepository socksRepository;

    @Override
    public SocksResponseDto save(final SocksRequestDto socksRequestDto) {

        final Sock sock = validAmountInBD(socksRequestDto);
        log.info("Запрос на сохранение прихода носков в базе данных");
        socksRepository.save(sock);

        log.info("Регистрация прихода носков выполнена успешно");
        return SocksMapper.toSocksResponseDto(sock);
    }

    @Override
    public SocksResponseDto release(final SocksRequestDto socksRequestDto) {

        log.info("Уточнение, есть ли на складе достаточное количество запрошенных носков");
        final Sock sock = socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                socksRequestDto.getColor(), socksRequestDto.getCottonPercentage())
                .orElseThrow(() -> new NotFoundException("В базе нет таких носков"));
        if (socksRequestDto.getAmount() > sock.getAmount()) {
            throw new ValidationException("На складе нет такого количества запрошенных носков");
        }
        sock.setAmount(sock.getAmount() - socksRequestDto.getAmount());

        log.info("Запрос на уменьшение количества носков в базе данных");
        socksRepository.save(sock);

        log.info("Регистрация отпуска носков выполнена успешно");
        return SocksMapper.toSocksResponseDto(sock);
    }

    @Override
    public List<SocksResponseDto> findByFilters(final String color,
                              final String operator,
                              final Integer cottonPercentage,
                              final Integer minCottonPercentage,
                              final Integer maxCottonPercentage,
                              final Sort sort) {

        int minCottonPercentageFilter = 0;
        int maxCottonPercentageFilter = 100;

        if (Objects.nonNull(operator) && Objects.equals(operator, "equals") && Objects.nonNull(cottonPercentage)) {
            minCottonPercentageFilter = cottonPercentage;
            maxCottonPercentageFilter = cottonPercentage;
        }
        if (Objects.nonNull(operator) && Objects.equals(operator, "lessThan") && Objects.nonNull(cottonPercentage)) {
            maxCottonPercentageFilter = cottonPercentage;
        }
        if (Objects.nonNull(operator) && Objects.equals(operator, "moreThan") && Objects.nonNull(cottonPercentage)) {
            minCottonPercentageFilter = cottonPercentage;
        }
        if (Objects.nonNull(minCottonPercentage)) {
            minCottonPercentageFilter = minCottonPercentage;
        }
        if (Objects.nonNull(maxCottonPercentage)) {
            maxCottonPercentageFilter = maxCottonPercentage;
        }
        log.info("6");

        List<Sock> socks;
        Optional<Long> count;

        log.info("Находим носки в по заданным фильтрам в базе данных");
        if (Objects.isNull(color)) {
            count = socksRepository.findByCottonPercentage(minCottonPercentageFilter, maxCottonPercentageFilter);
            socks = socksRepository.findByCottonPercentageBetween(minCottonPercentageFilter,
                    maxCottonPercentageFilter, sort);
        } else {
            count = socksRepository.findByColorAndCottonPercentage(color,
                    minCottonPercentageFilter, maxCottonPercentageFilter);
            socks = socksRepository.findByColorIgnoreCaseAndCottonPercentageBetween(color,
                    minCottonPercentageFilter, maxCottonPercentageFilter, sort);
        }

        if (socks.isEmpty()) {
            log.info("В базе данных нет носков по заданным критериям");
            return new ArrayList<>();
        }

        log.info("В базе данных найдено {} носков по заданным критериям", count.get());
        return socks.stream()
                .map(SocksMapper::toSocksResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public SocksResponseDto update(final Long id,
                                   final SocksRequestDto socksRequestDto) {
        final Sock sock = socksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("В базе нет таких носков"));

        if(!socksRequestDto.getColor().isBlank()) {
            log.info("Обновляем цвет носков");
            sock.setColor(socksRequestDto.getColor());
        }
        if(Objects.nonNull(socksRequestDto.getCottonPercentage())) {
            log.info("Обновляем содержание хлопка в данных носках");
            sock.setCottonPercentage(socksRequestDto.getCottonPercentage());
        }
        if(Objects.nonNull(socksRequestDto.getAmount())) {
            log.info("Обновляем количество носков");
            sock.setAmount(socksRequestDto.getAmount());
        }
        log.info("Запрос на сохранение обновлений носков в базе данных");
        socksRepository.save(sock);

        log.info("Обновление носков успешно выполнено");
        return SocksMapper.toSocksResponseDto(sock);
    }

    @Override
    public void saveFromFile(final MultipartFile file) {

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<SocksFileDto> csvToBean = new CsvToBeanBuilder<SocksFileDto>(reader)
                    .withType(SocksFileDto.class)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            log.info("Файл успешно прочитан. Начинаем проверку данных, поступивших из файла");

            socksRepository.saveAll(
                    csvToBean.parse().stream()
                            .map(SocksMapper::toSocksRequestDto)
                            .map(this::validAmountInBD)
                            .collect(Collectors.toList())
            );
            log.info("Данные из файла успешно сохранены");

        } catch (IOException exception) {
            throw new FileProcessingException("Во время обработки файла произошла ошибка");
        }
    }

        private Sock validAmountInBD(final SocksRequestDto socksRequestDto) {
            log.info("Запрос на получение прежнего количества запрошенных носков");
            final Optional<Sock> oldSock = socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                    socksRequestDto.getColor(), socksRequestDto.getCottonPercentage());
            final Sock sock;
            if (oldSock.isPresent()) {
                sock = oldSock.get();
                sock.setAmount(sock.getAmount() + socksRequestDto.getAmount());
            } else {
                log.info("В базе еще не было таких носков, добавляем новую позицию");
                sock = SocksMapper.toSock(socksRequestDto);
            }
            return sock;
        }
}

