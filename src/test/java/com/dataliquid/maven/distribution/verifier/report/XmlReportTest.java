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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.dataliquid.maven.distribution.verifier.domain.ResultEntry;

public class XmlReportTest
{
    private Report reportService;

    @Before
    public void setUp() throws IOException
    {
        reportService = new XmlReport();
    }

    @Test
    public void shouldVerifyValid() throws Exception
    {
        // given
        String expectedReport = "src/test/resources/valid-fullmatch/report.xml";
        String report = "target/valid-fullmatch-report.xml";

        final List<ResultEntry> verificationResults = new ArrayList<>();

        ResultEntry resultEntryA = new ResultEntry();
        resultEntryA.setStatus("SUCCESS");
        resultEntryA.setMessage("Validation passed successfully");
        resultEntryA.setPath("/Sample.md");
        resultEntryA.setMd5("4114b3e750902c5404ffe4864b3e11b8");
        verificationResults.add(resultEntryA);

        ResultEntry resultEntryB = new ResultEntry();
        resultEntryB.setStatus("SUCCESS");
        resultEntryB.setMessage("Validation passed successfully");
        resultEntryB.setPath("/Sample.txt");
        resultEntryB.setMd5("193fa5e788a1800a760d1108051c2363");
        verificationResults.add(resultEntryB);

        // when
        reportService.generateReport(verificationResults, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyWithVariables() throws Exception
    {
        // given
        String expectedReport = "src/test/resources/valid-fullmatch-variables/report.xml";
        String report = "target/valid-fullmatch-variables-report.xml";

        final List<ResultEntry> verificationResults = new ArrayList<>();

        ResultEntry resultEntryA = new ResultEntry();
        resultEntryA.setStatus("SUCCESS");
        resultEntryA.setMessage("Validation passed successfully");
        resultEntryA.setPath("/Sample-1.0.0.md");
        resultEntryA.setMd5("4114b3e750902c5404ffe4864b3e11b8");
        verificationResults.add(resultEntryA);

        ResultEntry resultEntryB = new ResultEntry();
        resultEntryB.setStatus("SUCCESS");
        resultEntryB.setMessage("Validation passed successfully");
        resultEntryB.setPath("/Sample-myartifact.txt");
        resultEntryB.setMd5("193fa5e788a1800a760d1108051c2363");
        verificationResults.add(resultEntryB);

        // when
        reportService.generateReport(verificationResults, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyInvalidMissingFile() throws Exception
    {
        // given
        String expectedReport = "src/test/resources/invalid-missingfile/report.xml";
        String report = "target/invalid-missingfile-report.xml";

        final List<ResultEntry> verificationResults = new ArrayList<>();

        ResultEntry resultEntryA = new ResultEntry();
        resultEntryA.setStatus("SUCCESS");
        resultEntryA.setMessage("Validation passed successfully");
        resultEntryA.setPath("/Sample.md");
        resultEntryA.setMd5("4114b3e750902c5404ffe4864b3e11b8");
        verificationResults.add(resultEntryA);

        ResultEntry resultEntryB = new ResultEntry();
        resultEntryB.setStatus("SUCCESS");
        resultEntryB.setMessage("Validation passed successfully");
        resultEntryB.setPath("/Sample.txt");
        resultEntryB.setMd5("193fa5e788a1800a760d1108051c2363");
        verificationResults.add(resultEntryB);

        ResultEntry resultEntryC = new ResultEntry();
        resultEntryC.setStatus("FAILED");
        resultEntryC.setMessage("Defined file not found");
        resultEntryC.setPath("/Sample.adoc");
        resultEntryC.setMd5("193fa5e788a1800a760d1108051c7778");
        verificationResults.add(resultEntryC);

        // when
        reportService.generateReport(verificationResults, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyInvalidFoundUndefinedFile() throws Exception
    {
        // given
        String expectedReport = "src/test/resources/invalid-found-undefined-file/report.xml";
        String report = "target/invalid-found-undefined-file-report.xml";

        final List<ResultEntry> verificationResults = new ArrayList<>();

        ResultEntry resultEntryA = new ResultEntry();
        resultEntryA.setStatus("SUCCESS");
        resultEntryA.setMessage("Validation passed successfully");
        resultEntryA.setPath("/Sample.md");
        resultEntryA.setMd5("4114b3e750902c5404ffe4864b3e11b8");
        verificationResults.add(resultEntryA);

        ResultEntry resultEntryB = new ResultEntry();
        resultEntryB.setStatus("SUCCESS");
        resultEntryB.setMessage("Validation passed successfully");
        resultEntryB.setPath("/Sample.txt");
        resultEntryB.setMd5("193fa5e788a1800a760d1108051c2363");
        verificationResults.add(resultEntryB);

        ResultEntry resultEntryC = new ResultEntry();
        resultEntryC.setStatus("FAILED");
        resultEntryC.setMessage("File is not defined in whitelist");
        resultEntryC.setPath("/Sample.adoc");
        resultEntryC.setMd5("0430eba9643b5e60e49c055eb16cbf7a");
        verificationResults.add(resultEntryC);

        // when
        reportService.generateReport(verificationResults, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyInvalidDifferentMd5Checksum() throws Exception
    {
        // given
        String expectedReport = "src/test/resources/invalid-different-md5-checksum/report.xml";
        String report = "target/invalid-different-md5-checksum-report.xml";

        final List<ResultEntry> verificationResults = new ArrayList<>();

        ResultEntry resultEntryA = new ResultEntry();
        resultEntryA.setStatus("SUCCESS");
        resultEntryA.setMessage("Validation passed successfully");
        resultEntryA.setPath("/Sample.md");
        resultEntryA.setMd5("4114b3e750902c5404ffe4864b3e11b8");
        verificationResults.add(resultEntryA);

        ResultEntry resultEntryB = new ResultEntry();
        resultEntryB.setStatus("FAILED");
        resultEntryB.setMessage("File found but with a different MD5 Checksum 193fa5e788a1800a760d1108051c2363");
        resultEntryB.setPath("/Sample.txt");
        resultEntryB.setMd5("193fa5e788a1800a760d1108051c4711");
        verificationResults.add(resultEntryB);

        // when
        reportService.generateReport(verificationResults, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

}
