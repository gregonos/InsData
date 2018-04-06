package net.windia.insdata.model.internal;

import java.time.LocalDate;

public interface IgStatDaily {

    LocalDate getWeek();

    void setWeek(LocalDate week);

    Byte getMonth();

    void setMonth(Byte month);

    Byte getWeekday();

    void setWeekday(Byte weekday);
}
