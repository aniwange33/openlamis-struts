package org.fhi360.lamis.service.indicator;

import java.util.Date;

public interface IndicatorService {
    void process(Long facilityId, Date reportDate, int dataElementId, String dbSuffix);

    int[] dataElementIds();
}
