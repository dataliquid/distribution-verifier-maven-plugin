/*
 * Copyright © 2019 dataliquid GmbH | www.dataliquid.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dataliquid.maven.distribution.verifier.report;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dataliquid.maven.distribution.verifier.domain.ResultEntry;

public class XmlReport extends AbstractXmlReport
{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void generateReport(List<ResultEntry> results, String reportFileName) throws Exception
    {
        logger.info("start generating xml report");

        Document document = DocumentHelper.createDocument();
        Element report = document.addElement("report");
        for (ResultEntry resultEntry : results)
        {
            logger.debug("start processing resultEntry:" + resultEntry.toString());
            Element reportEntry = report.addElement("entry");
            reportEntry.addAttribute("path", resultEntry.getPath());
            reportEntry.addAttribute("md5", resultEntry.getMd5());
            Element result = reportEntry.addElement("result");
            result.addAttribute("status", resultEntry.getStatus());
            result.addAttribute("message", resultEntry.getMessage());
        }

        writeFile(reportFileName, document);
        logger.info("report has been written to:" + reportFileName);
    }

}
