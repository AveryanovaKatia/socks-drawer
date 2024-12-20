package ru.project.sock.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocksFileDto {

    @CsvBindByName(required = true)
    String color;

    @CsvBindByName(required = true)
    Integer cottonPercentage;

    @CsvBindByName(required = true)
    Long amount;
}
