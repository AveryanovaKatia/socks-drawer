package ru.project.sock.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.project.sock.model.Sock;

import java.util.List;
import java.util.Optional;

public interface SocksRepository extends JpaRepository<Sock, Long> {

    Optional<Sock> findByColorIgnoreCaseAndCottonPercentage(final String color,
                                                            final Integer cottonPercentage);

    List<Sock> findByCottonPercentageBetween(final Integer minCottonPercentage,
                                             final Integer maxCottonPercentage,
                                             final Sort sort);

    @Query("SELECT SUM(s.amount) " +
            "FROM Sock s " +
            "WHERE s.cottonPercentage BETWEEN :minCottonPercentage AND :maxCottonPercentage")
    Optional<Long> findByCottonPercentage(@Param("minCottonPercentage") final Integer minCottonPercentage,
                                @Param("maxCottonPercentage") final Integer maxCottonPercentage);

    List<Sock> findByColorIgnoreCaseAndCottonPercentageBetween(final String color,
                                                    final Integer minCottonPercentage,
                                                    final Integer maxCottonPercentage,
                                                    final Sort sort);

    @Query("SELECT SUM(s.amount) " +
            "FROM Sock s " +
            "WHERE s.color = :color AND s.cottonPercentage BETWEEN :minCottonPercentage AND :maxCottonPercentage")
    Optional<Long> findByColorAndCottonPercentage(@Param("color") final String color,
                                        @Param("minCottonPercentage") final Integer minCottonPercentage,
                                        @Param("maxCottonPercentage")  final Integer maxCottonPercentage);




}

