package net.windia.insdata.service.diffcalc;

import net.windia.insdata.model.internal.IgProfileStat;

import java.util.Date;

public interface IgDiffCalculator<StatType extends IgProfileStat> {
    void calculate(StatType diff, StatType lastSnapshot, StatType newSnapshot, Date sinceTime);
}
