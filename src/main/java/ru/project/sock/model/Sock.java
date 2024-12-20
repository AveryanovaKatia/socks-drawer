package ru.project.sock.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "socks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sock {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "color")
    String color;

    @Column(name = "cotton_percentage")
    Integer cottonPercentage;

    @Column(name = "amount")
    Long amount;

}
