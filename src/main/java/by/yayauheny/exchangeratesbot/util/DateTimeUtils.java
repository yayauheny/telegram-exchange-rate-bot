package by.yayauheny.exchangeratesbot.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public String parseDate(LocalDateTime dateTime) {
        return dateTime.format(dateFormatter);
    }

    public String parseTime(LocalDateTime dateTime) {
        return dateTime.format(timeFormatter);
    }
}
