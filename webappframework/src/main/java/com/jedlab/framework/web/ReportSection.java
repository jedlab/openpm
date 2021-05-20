package com.jedlab.framework.web;

import java.util.Map;

import com.jedlab.framework.report.ReportHeader;

import ar.com.fdvs.dj.domain.builders.FastReportBuilder;

public interface ReportSection
{

    public void beforeFormGenerateReport(FastReportBuilder frb);

    public ReportHeader getFormReportHeader();

    public Map<String, Object> getParameters();
}