package com.dataliquid.maven.verifier.service;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

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
        String expectedReportFilePath = "src/test/resources/valid-fullmatch/report.xml";
        String reportFile = "target/valid-fullmatch-report.xml";
        File distributionArchive = new File("src/test/resources/valid-fullmatch/valid_fullmatch.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, reportFile);

        // then
        assertThat(new File(expectedReportFilePath), isSimilarTo(new File(reportFile)).ignoreWhitespace());

    }

    @Test
    public void shouldVerifyInvalidMissingFile() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-missingfile/whitelist.xml");
        String expectedReportFilePath = "src/test/resources/invalid-missingfile/report.xml";
        String reportFile = "target/invalid-missingfile-report.xml";
        File distributionArchive = new File("src/test/resources/invalid-missingfile/invalid_missingfile.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, reportFile);

        // then
        assertThat(new File(expectedReportFilePath), isSimilarTo(new File(reportFile)).ignoreWhitespace());

    }

    @Test
    public void shouldVerifyInvalidFoundUndefinedFile() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-found-undefined-file/whitelist.xml");
        String expectedReportFilePath = "src/test/resources/invalid-found-undefined-file/report.xml";
        String reportFile = "target/invalid-found-undefined-file-report.xml";
        File distributionArchive = new File("src/test/resources/invalid-found-undefined-file/invalid_found_undefined_file.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, reportFile);

        // then
        assertThat(new File(expectedReportFilePath), isSimilarTo(new File(reportFile)).ignoreWhitespace());

    }

    @Test
    public void shouldVerifyInvalidDifferentMd5Checksum() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-different-md5-checksum/whitelist.xml");
        String expectedReportFilePath = "src/test/resources/invalid-different-md5-checksum/report.xml";
        String reportFile = "target/invalid-different-md5-checksum-report.xml";
        File distributionArchive = new File("src/test/resources/invalid-different-md5-checksum/invalid_different_md5_checksum.zip");

        // when
        verifierService.verify(distributionArchive, outputDirectory, whitelist, reportFile);

        // then
        assertThat(new File(expectedReportFilePath), isSimilarTo(new File(reportFile)).ignoreWhitespace());

    }

}
