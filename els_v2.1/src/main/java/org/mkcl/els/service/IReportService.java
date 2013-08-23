package org.mkcl.els.service;

import java.io.File;
import org.mkcl.els.common.xmlvo.XmlVO;

public interface IReportService {
	File generateReport(XmlVO data) throws Exception;
}
