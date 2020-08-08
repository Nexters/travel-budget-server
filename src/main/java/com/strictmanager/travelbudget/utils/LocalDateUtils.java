package com.strictmanager.travelbudget.utils;

import com.strictmanager.travelbudget.domain.plan.PlanException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateUtils {

    public static List<LocalDate> getLocalDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();

        int datePeriod = startDate.until(endDate).getDays() + 1;

        LocalDate tempDate = startDate;
        for (int i = 0; i < datePeriod; i++) {
            dates.add(tempDate);
            tempDate = tempDate.plusDays(1);
        }
        return dates;
    }


    public static void checkDateValidation(LocalDate startDate, LocalDate endDate) {
        if (startDate.compareTo(endDate) > 0) {
            throw new PlanException();
        }
    }

}

