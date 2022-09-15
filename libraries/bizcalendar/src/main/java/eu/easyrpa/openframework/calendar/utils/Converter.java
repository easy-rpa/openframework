package eu.easyrpa.openframework.calendar.utils;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;

public class Converter {

    public static LocalDate fromHijriToLocal(HolidayEntity holiday) {
        return LocalDate.from(HijrahDate.of(holiday.getValidFrom(), holiday.getMonth(), holiday.getDay()));
    }

    public static LocalDate getFloatingChurchHolidayDate(HolidayEntity holiday){
        LocalDate easterDate = easterDateResolving(holiday.getValidFrom(), holiday.getChurchHolidayType());
        return easterDate.plusDays(holiday.getDaysFromEaster());
    }

    public static LocalDate easterDateResolving(int year, HolidayEntity.ChurchHolidayType type) {
        float A, B, C, P, Q, M, N, D, E;

        A = year % 19;
        B = year % 4;
        C = year % 7;
        P = (float) Math.floor(year / 100d);
        Q = (float) Math.floor((13 + 8 * P) / 25);
        M = (int) (15 - Q + P - Math.floor(P / 4)) % 30;
        N = (int) (4 + P - Math.floor(P / 4)) % 7;
        D = (19 * A + M) % 30;
        E = (2 * B + 4 * C + 6 * D + N) % 7;
        int days = (int) (22 + D + E);

        if ((D == 29) && (E == 6)) {
            LocalDate resultDate = LocalDate.of(year, 4, 19);
            return type.equals(HolidayEntity.ChurchHolidayType.CATHOLIC) ? resultDate : resultDate.plusDays(7);
        }

        else if ((D == 28) && (E == 6)) {
            LocalDate resultDate = LocalDate.of(year, 4, 18);
            return type.equals(HolidayEntity.ChurchHolidayType.CATHOLIC) ? resultDate : resultDate.plusDays(7);
        } else {

            LocalDate resultDate;
            if (days > 31) {
                resultDate = LocalDate.of(year, 4, days - 31);
            }
            else {
                resultDate = LocalDate.of(year, 4, days);
            }
            return type.equals(HolidayEntity.ChurchHolidayType.CATHOLIC) ? resultDate : resultDate.plusDays(7);
        }
    }

}
