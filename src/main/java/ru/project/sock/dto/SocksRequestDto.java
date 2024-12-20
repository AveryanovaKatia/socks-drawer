package ru.project.sock.dto;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.project.group.CreateGroup;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocksRequestDto {

    @NotBlank(groups = {CreateGroup.class})
    String color;

    @Min(value = 0)
    @Max(value = 100)
    Integer cottonPercentage;

    @PositiveOrZero
    Long amount;

}

