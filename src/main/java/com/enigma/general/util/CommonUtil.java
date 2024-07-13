package com.enigma.general.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.GregorianCalendar;
import java.util.Random;

@Service
@Slf4j

public class CommonUtil {
    public static String randomNumber(){
        GregorianCalendar gc = new GregorianCalendar();
        String month = String.valueOf(gc.get(GregorianCalendar.MONTH));

        Random random = new Random();
        Integer randomNum = random.nextInt(90000);
        String accNum = String.valueOf(randomNum+10000);

        return month.concat(accNum);
    }
}
