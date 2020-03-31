package io.lastwill.eventscan.model;

import lombok.Getter;

@Getter
public enum  TokenType {
    SMALL("8GRAM"),
    BIG("10GRAM");

    private String name;
    TokenType(String name) {this.name = name;}
}
