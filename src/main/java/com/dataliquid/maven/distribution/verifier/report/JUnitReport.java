/**
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
import com.dataliquid.maven.distribution.verifier.domain.VerificationStatus;

public class JUnitReport extends AbstractXmlReport
{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void generateReport(List<ResultEntry> results, String reportFileName) throws Exception
    {
        logger.info("start generating junit report");
        Document document = DocumentHelper.createDocument();
        final Element testsuite = registerTestSuiteInDocument(results, document);

        for (ResultEntry resultEntry : results)
        {
            final Element testCase = testsuite.addElement("testcase");
            testCase.addAttribute("classname", resultEntry.getPath());
            testCase.addAttribute("name", resultEntry.getPath());
            testCase.addAttribute("time", "0");
            if (VerificationStatus.FAILED.name().equals(resultEntry.getStatus()))
            {
                final Element error = testCase.addElement("error");
                error.addAttribute("message", resultEntry.getMessage());
                error.addAttribute("type", "error");
                error.addText(resultEntry.getMessage());
            }
        }

        writeFile(reportFileName, document);
        logger.info("report has been written to:" + reportFileName);
    }

    private Element registerTestSuiteInDocument(List<ResultEntry> results, Document document)
    {
        Element testsuite = document.addElement("testsuite");
        final String zero = String.valueOf(0);
        testsuite.addAttribute("tests", String.valueOf(results.size()));
        testsuite.addAttribute("failures", zero);
        testsuite.addAttribute("name", "Verifier");
        testsuite.addAttribute("time", zero);
        testsuite.addAttribute("errors", zero);
        testsuite.addAttribute("skipped", zero);
        return testsuite;
    }

}
