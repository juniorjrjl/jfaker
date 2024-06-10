package net.jfaker.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookModel {

    private long id;
    private String name;
    private String author;
    private int year;
    private BigDecimal value;

}
