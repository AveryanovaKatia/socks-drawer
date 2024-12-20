package ru.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.project.exception.ValidationException;
import ru.project.sock.controller.SocksController;
import ru.project.sock.dto.SocksRequestDto;
import ru.project.sock.dto.SocksResponseDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SocksControllerTest {

    @Autowired
    private SocksController socksController;

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("SocksController_saveInEmptyDB")
    void testSaveInEmptyDB() {

        final SocksRequestDto socksRequestDto = new SocksRequestDto("black", 20, 100L);
        final ResponseEntity<SocksResponseDto> socksResponseDto = socksController.save(socksRequestDto);

        assertEquals(1L, Objects.requireNonNull(socksResponseDto.getBody()).getId());
        assertEquals("black", socksResponseDto.getBody().getColor());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("SocksController_save")
    void testSave() {

        final SocksRequestDto socksRequestDto1 = new SocksRequestDto("black", 20, 100L);
        final SocksRequestDto socksRequestDto2 = new SocksRequestDto("green", 50, 500L);
        final SocksRequestDto socksRequestDto3 = new SocksRequestDto("black", 20, 40L);

        socksController.save(socksRequestDto1);
        socksController.save(socksRequestDto2);
        final ResponseEntity<SocksResponseDto> socksResponseDto = socksController.save(socksRequestDto3);

        assertEquals(1L, Objects.requireNonNull(socksResponseDto.getBody()).getId());
        assertEquals(140L, socksResponseDto.getBody().getAmount());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("SocksController_releasePositiveCase")
    void testReleaseGoodPositiveCase() {

        final SocksRequestDto socksRequestDto1 = new SocksRequestDto("black", 20, 100L);
        final SocksRequestDto socksRequestDto2 = new SocksRequestDto("green", 50, 500L);
        final SocksRequestDto socksRequestDto3 = new SocksRequestDto("black", 20, 40L);

        socksController.save(socksRequestDto1);
        socksController.save(socksRequestDto2);
        final ResponseEntity<SocksResponseDto> socksResponseDto = socksController.release(socksRequestDto3);

        assertEquals(1L, Objects.requireNonNull(socksResponseDto.getBody()).getId());
        assertEquals(60L, socksResponseDto.getBody().getAmount());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("SocksController_releaseFindByFilters")
    void testReleaseGoodNegativeCase() {

        final SocksRequestDto socksRequestDto1 = new SocksRequestDto("black", 20, 100L);
        final SocksRequestDto socksRequestDto2 = new SocksRequestDto("green", 50, 500L);
        final SocksRequestDto socksRequestDto3 = new SocksRequestDto("black", 20, 140L);

        socksController.save(socksRequestDto1);
        socksController.save(socksRequestDto2);

        assertThrows(
                ValidationException.class,
                () -> socksController.release(socksRequestDto3)
        );
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("SocksController_update")
    void testUpdate() {

        final SocksRequestDto socksRequestDto1 = new SocksRequestDto("black", 20, 100L);
        final SocksRequestDto socksRequestDto2 = new SocksRequestDto("green", 50, 500L);
        final SocksRequestDto socksRequestDto3 = new SocksRequestDto("white", 20, 140L);

        socksController.save(socksRequestDto1);
        socksController.save(socksRequestDto2);
        final ResponseEntity<SocksResponseDto> socksResponseDto = socksController.update(1L, socksRequestDto3);

        assertEquals("white", Objects.requireNonNull(socksResponseDto.getBody()).getColor());
        assertEquals(140L, socksResponseDto.getBody().getAmount());
    }
}
