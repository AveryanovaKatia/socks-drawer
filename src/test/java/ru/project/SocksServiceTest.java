package ru.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.project.exception.NotFoundException;
import ru.project.exception.ValidationException;
import ru.project.sock.dto.SocksRequestDto;
import ru.project.sock.dto.SocksResponseDto;
import ru.project.sock.model.Sock;
import ru.project.sock.repository.SocksRepository;
import ru.project.sock.service.SocksServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SocksServiceTest {

    @InjectMocks
    private SocksServiceImpl socksService;

    @Mock
    private SocksRepository socksRepository;

    private SocksRequestDto socksRequestDto;
    private Sock sock;
    private List<Sock> socks;

    @BeforeEach
    public void setUp() {

        socksRequestDto = new SocksRequestDto("black", 20, 100L);
        sock = new Sock(1L, "black", 20, 100L);
        final Sock sock1 = new Sock(1L, "black", 100, 100L);
        final Sock sock2 = new Sock(2L, "white", 80, 100L);
        socks = new ArrayList<>();
        socks.add(sock1);
        socks.add(sock2);
    }

    @Test
    @DisplayName("SocksService_saveExistingSock")
    void testSaveExistingSock() {

        when(socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                socksRequestDto.getColor(), socksRequestDto.getCottonPercentage())).thenReturn(Optional.of(sock));

        final SocksResponseDto socksResponseDto = socksService.save(socksRequestDto);

        assertEquals("black", socksResponseDto.getColor());
        assertEquals(200, sock.getAmount());
    }

    @Test
    @DisplayName("SocksService_saveNewSock")
    void testSaveNewSock() {

        when(socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                socksRequestDto.getColor(), socksRequestDto.getCottonPercentage())).thenReturn(Optional.empty());

        when(socksRepository.save(any(Sock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final SocksResponseDto socksResponseDto = socksService.save(socksRequestDto);

        assertEquals("black", socksResponseDto.getColor());
        assertEquals(100, socksResponseDto.getAmount());
    }

    @Test
    @DisplayName("SocksService_releaseSuccess")
    void testReleaseSuccess() {
        when(socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                socksRequestDto.getColor(), socksRequestDto.getCottonPercentage())).thenReturn(Optional.of(sock));

        SocksResponseDto response = socksService.release(socksRequestDto);

        assertEquals("black", response.getColor());
        assertEquals(0, response.getAmount());

        verify(socksRepository).save(sock);
        assertEquals(0, sock.getAmount());
    }

    @Test
    @DisplayName("SocksService_releaseInsufficientStock")
    void testReleaseInsufficientStock() {
        when(socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                socksRequestDto.getColor(), socksRequestDto.getCottonPercentage())).thenReturn(Optional.of(sock));

        socksRequestDto.setAmount(150L);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            socksService.release(socksRequestDto);
        });

        assertEquals("На складе нет такого количества запрошенных носков", exception.getMessage());
        verify(socksRepository, never()).save(any(Sock.class));
    }

    @Test
    @DisplayName("SocksService_releaseSockNotFound")
    void testReleaseSockNotFound() {
        when(socksRepository.findByColorIgnoreCaseAndCottonPercentage(
                socksRequestDto.getColor(), socksRequestDto.getCottonPercentage())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            socksService.release(socksRequestDto);
        });

        assertEquals("В базе нет таких носков", exception.getMessage());
        verify(socksRepository, never()).save(any(Sock.class));
    }

    @Test
    @DisplayName("SocksService_findByColorAndCottonPercentage")
    void testFindByColorAndCottonPercentage() {
        final String color = "black";
        final String operator = "equals";
        final Integer cottonPercentage = 100;
        final Sort sort = Sort.by("color");

        when(socksRepository.findByColorAndCottonPercentage(color, 100, 100))
                .thenReturn(Optional.of(1L));
        when(socksRepository
                .findByColorIgnoreCaseAndCottonPercentageBetween(color,
                        100, 100, sort)).thenReturn(socks);

        final List<SocksResponseDto> result = socksService
                .findByFilters(color, operator, cottonPercentage, null, null, sort);

        assertEquals(2, result.size());
        assertEquals("black", result.get(0).getColor());
        assertEquals(100, result.get(0).getAmount());
    }

    @Test
    @DisplayName("SocksService_findWithoutColor")
    void testFindWithoutColor() {
        final String operator = "moreThan";
        final Integer cottonPercentage = 50;
        final Sort sort = Sort.by("color");

        when(socksRepository.findByCottonPercentage(50, 100)).thenReturn(Optional.of(2L));
        when(socksRepository.findByCottonPercentageBetween(50, 100, sort))
                .thenReturn(socks);

        final List<SocksResponseDto> result = socksService
                .findByFilters(null, operator, cottonPercentage,
                        null, null, sort);

        assertEquals(2, result.size());
        assertEquals("black", result.get(0).getColor());
        assertEquals(100, result.get(0).getAmount());
    }

    @Test
    @DisplayName("SocksService_noSocksFound")
    void testNoSocksFound() {
        final String color = "red";
        final String operator = "equals";
        final Integer cottonPercentage = 100;
        final Sort sort = Sort.by("color");

        when(socksRepository.findByColorAndCottonPercentage(color, 100, 100))
                .thenReturn(Optional.of(0L));
        when(socksRepository.findByColorIgnoreCaseAndCottonPercentageBetween(color, 100,
                100, sort)).thenReturn(new ArrayList<>());

        final List<SocksResponseDto> result = socksService
                .findByFilters(color, operator, cottonPercentage, null, null, sort);

        assertTrue(result.isEmpty());
    }
}
