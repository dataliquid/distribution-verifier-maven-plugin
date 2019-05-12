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
package com.dataliquid.maven.distribution.verifier.service;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.dataliquid.maven.distribution.verifier.service.VerifierService;

public class VerifierPluginServiceTest
{
    private VerifierService verifierService;

    private File outputDirectory;

    @Before
    public void setUp() throws IOException
    {
        verifierService = new VerifierService();
        outputDirectory = new File("target/");
    }

    @Test
    public void shouldVerifyValid() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/valid-fullmatch/whitelist.xml");
        String expectedReport = "src/test/resources/valid-fullmatch/report.xml";
        String report = "target/valid-fullmatch-report.xml";
        File distributionArchive = new File("src/test/resources/valid-fullmatch/valid_fullmatch.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyInvalidMissingFile() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-missingfile/whitelist.xml");
        String expectedReport = "src/test/resources/invalid-missingfile/report.xml";
        String report = "target/invalid-missingfile-report.xml";
        File distributionArchive = new File("src/test/resources/invalid-missingfile/invalid_missingfile.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyInvalidFoundUndefinedFile() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-found-undefined-file/whitelist.xml");
        String expectedReport = "src/test/resources/invalid-found-undefined-file/report.xml";
        String report = "target/invalid-found-undefined-file-report.xml";
        File distributionArchive = new File("src/test/resources/invalid-found-undefined-file/invalid_found_undefined_file.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

    @Test
    public void shouldVerifyInvalidDifferentMd5Checksum() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-different-md5-checksum/whitelist.xml");
        String expectedReport = "src/test/resources/invalid-different-md5-checksum/report.xml";
        String report = "target/invalid-different-md5-checksum-report.xml";
        File distributionArchive = new File("src/test/resources/invalid-different-md5-checksum/invalid_different_md5_checksum.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, report);

        // then
        assertThat(new File(expectedReport), isSimilarTo(new File(report)).ignoreWhitespace().ignoreComments());

    }

}
