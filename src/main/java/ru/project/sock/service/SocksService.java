package ru.project.sock.service;

import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import ru.project.sock.dto.SocksRequestDto;
import ru.project.sock.dto.SocksResponseDto;

import java.util.List;

public interface SocksService {

    SocksResponseDto save(final SocksRequestDto socksRequestDto);

    SocksResponseDto release(final SocksRequestDto socksRequestDto);

    List<SocksResponseDto> findByFilters(final String color,
                                         final String operator,
                                         final Integer cottonPercentage,
                                         final Integer minCottonPercentage,
                                         final Integer maxCottonPercentage,
                                         final Sort sort);

    SocksResponseDto update(final Long id,
                            final SocksRequestDto socksRequestDto);

    void saveFromFile(final MultipartFile file);
}

