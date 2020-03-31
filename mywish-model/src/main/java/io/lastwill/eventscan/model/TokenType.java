package io.lastwill.eventscan.model;

import lombok.Getter;

@Getter
public enum  TokenType {
    SMALL("8GRAMM"),
    BIG("10GRAMM");

    private String name;
    TokenType(String name) {this.name = name;}
}
