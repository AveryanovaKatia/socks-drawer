package ru.project.sock.mapper;

import ru.project.sock.dto.SocksFileDto;
import ru.project.sock.dto.SocksRequestDto;
import ru.project.sock.dto.SocksResponseDto;
import ru.project.sock.model.Sock;

public class SocksMapper {

    public static Sock toSock(final SocksRequestDto socksRequestDto) {

        final Sock sock = new Sock();

        sock.setColor(socksRequestDto.getColor());
        sock.setCottonPercentage(socksRequestDto.getCottonPercentage());
        sock.setAmount(socksRequestDto.getAmount());

        return sock;
    }

    public static SocksRequestDto toSocksRequestDto(final SocksFileDto socksFileDto) {

        final SocksRequestDto socksRequestDto = new SocksRequestDto();

        socksRequestDto.setColor(socksFileDto.getColor());
        socksRequestDto.setCottonPercentage(socksFileDto.getCottonPercentage());
        socksRequestDto.setAmount(socksFileDto.getAmount());

        return socksRequestDto;
    }

    public static SocksResponseDto toSocksResponseDto(final Sock sock) {

        final SocksResponseDto socksResponseDto = new SocksResponseDto();

        socksResponseDto.setId(sock.getId());
        socksResponseDto.setColor(sock.getColor());
        socksResponseDto.setCottonPercentage(sock.getCottonPercentage());
        socksResponseDto.setAmount(sock.getAmount());

        return socksResponseDto;
    }
}

