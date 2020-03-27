package io.lastwill.eventscan.model;

public enum TransferStatus {
    WAIT_FOR_SEND,
    SENDING,
    WAIT_FOR_CONFIRM,
    LATE,
    ERROR,
    OK,
}