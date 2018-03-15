package net.windia.insdata.service.diffcalc;

import net.windia.insdata.model.internal.IgStat;

import java.util.Date;

public interface IgDiffCalculator<StatType extends IgStat> {
    void calculate(StatType diff, StatType lastSnapshot, StatType newSnapshot, Date sinceTime);
}
