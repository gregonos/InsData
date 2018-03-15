package net.windia.insdata.model.internal;

import java.util.Date;

public interface IgStatDaily {

    Date getWeek();

    void setWeek(Date week);

    Byte getMonth();

    void setMonth(Byte month);

    Byte getWeekday();

    void setWeekday(Byte weekday);
}
