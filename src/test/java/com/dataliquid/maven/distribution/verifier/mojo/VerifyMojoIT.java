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
package com.dataliquid.maven.distribution.verifier.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.File;

/**
 * Integration test for VerifyMojo
 */
public class VerifyMojoIT extends AbstractMojoTestCase {

    @Rule
    public MojoRule rule = new MojoRule();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testValidFullMatch() throws Exception {
        File pom = new File("src/test/resources/test-poms/valid-fullmatch-test-pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/valid-fullmatch/valid_fullmatch.zip"));
        mojo.setWhitelist(new File("src/test/resources/valid-fullmatch/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/valid-fullmatch-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should execute without exceptions
        mojo.execute();
        
        // Verify report was created
        assertTrue("Report file should exist", mojo.getOutputFile().exists());
    }

    @Test
    public void testValidWithVariables() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/valid-fullmatch-variables/valid_fullmatch_variables.zip"));
        mojo.setWhitelist(new File("src/test/resources/valid-fullmatch-variables/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/valid-variables-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should execute without exceptions
        mojo.execute();
        
        // Verify report was created
        assertTrue("Report file should exist", mojo.getOutputFile().exists());
    }

    @Test(expected = MojoFailureException.class)
    public void testInvalidMissingFile() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/invalid-missingfile/invalid_missingfile.zip"));
        mojo.setWhitelist(new File("src/test/resources/invalid-missingfile/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/invalid-missing-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should throw MojoFailureException
        mojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void testInvalidDifferentChecksum() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/invalid-different-md5-checksum/invalid_different_md5_checksum.zip"));
        mojo.setWhitelist(new File("src/test/resources/invalid-different-md5-checksum/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/invalid-checksum-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should throw MojoFailureException
        mojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void testInvalidUndefinedFile() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/invalid-found-undefined-file/invalid_found_undefined_file.zip"));
        mojo.setWhitelist(new File("src/test/resources/invalid-found-undefined-file/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/invalid-undefined-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should throw MojoFailureException
        mojo.execute();
    }

    @Test
    public void testJUnitReportFormat() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/valid-fullmatch/valid_fullmatch.zip"));
        mojo.setWhitelist(new File("src/test/resources/valid-fullmatch/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/valid-junit-report.xml"));
        mojo.setReportFormat("junit");
        mojo.setFailOnError(true);
        
        mojo.execute();
        
        // Verify JUnit report was created
        File reportFile = mojo.getOutputFile();
        assertTrue("JUnit report file should exist", reportFile.exists());
        
        // Read and verify it contains JUnit XML structure
        String content = org.apache.commons.io.FileUtils.readFileToString(reportFile, "UTF-8");
        assertTrue("Should contain testsuite element", content.contains("<testsuite"));
        assertTrue("Should contain testcase elements", content.contains("<testcase"));
    }

    @Test
    public void testFailOnErrorFalse() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/invalid-missingfile/invalid_missingfile.zip"));
        mojo.setWhitelist(new File("src/test/resources/invalid-missingfile/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/fail-on-error-false-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(false);
        
        // Should NOT throw exception when failOnError is false
        mojo.execute();
        
        // But report should still be created
        assertTrue("Report file should exist", mojo.getOutputFile().exists());
    }

    @Test(expected = MojoExecutionException.class)
    public void testMissingDistributionFile() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/non-existent.zip"));
        mojo.setWhitelist(new File("src/test/resources/valid-fullmatch/whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/missing-dist-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should throw MojoExecutionException
        mojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void testMissingWhitelistFile() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        mojo.setDistributionArchiveFile(new File("src/test/resources/valid-fullmatch/valid_fullmatch.zip"));
        mojo.setWhitelist(new File("src/test/resources/non-existent-whitelist.xml"));
        mojo.setOutputFile(new File("target/test-reports/missing-whitelist-report.xml"));
        mojo.setReportFormat("xml");
        mojo.setFailOnError(true);
        
        // Should throw MojoExecutionException
        mojo.execute();
    }
}