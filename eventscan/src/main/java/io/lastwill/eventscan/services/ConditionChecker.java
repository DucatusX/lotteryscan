package io.lastwill.eventscan.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;

@Slf4j
@Component
public class ConditionChecker {
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Value("${io.lastwill.eventscan.model.finish.date}")
    private Date finishDate;

    public boolean checkDate() {
        Date now = new Date();
        return now.after(finishDate);
    }

    public boolean checkAmount(BigInteger amountToCheck, BigInteger minimalAmount) {
        return (amountToCheck != null && amountToCheck.compareTo(minimalAmount) >= 0);
    }

    public boolean checkAllConditions(BigInteger amountToCheck, BigInteger minimalAmount) {
        return checkDate() || checkAmount(amountToCheck, minimalAmount);
    }
}
