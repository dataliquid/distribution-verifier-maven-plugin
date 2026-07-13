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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Integration test for verify goal functionality
 */
public class VerifyMojoIT extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testValidFullMatch() throws Exception {
        // Create a test POM with absolute paths
        File basedir = new File(getBasedir());
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
            "         http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>com.dataliquid.test</groupId>\n" +
            "    <artifactId>test-valid-fullmatch</artifactId>\n" +
            "    <version>1.0.0</version>\n" +
            "    <build>\n" +
            "        <directory>" + basedir + "/target</directory>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>com.dataliquid.maven</groupId>\n" +
            "                <artifactId>distribution-verifier-maven-plugin</artifactId>\n" +
            "                <version>1.0.4-SNAPSHOT</version>\n" +
            "                <configuration>\n" +
            "                    <distributionArchiveFile>" + basedir + "/src/test/resources/valid-fullmatch/valid_fullmatch.zip</distributionArchiveFile>\n" +
            "                    <whitelist>" + basedir + "/src/test/resources/valid-fullmatch/whitelist.xml</whitelist>\n" +
            "                    <reportFile>" + basedir + "/target/test-report.xml</reportFile>\n" +
            "                    <reportType>xml</reportType>\n" +
            "                </configuration>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <goals>\n" +
            "                            <goal>verify</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>";
            
        File pom = new File(basedir, "target/test-valid-fullmatch-pom.xml");
        pom.getParentFile().mkdirs();
        FileUtils.writeStringToFile(pom, pomContent, "UTF-8");
        
        VerifyMojo mojo = (VerifyMojo) lookupMojo("verify", pom);
        assertNotNull(mojo);
        
        // Execute mojo
        mojo.execute();
        
        // Check that the report was generated
        File reportFile = new File(basedir, "target/test-report.xml");
        assertTrue("Report file should exist", reportFile.exists());
        
        // Check report content
        String content = FileUtils.readFileToString(reportFile, "UTF-8");
        assertTrue("Report should contain VALID status", content.contains("<VerificationResult status=\"VALID\""));
        assertTrue("Report should contain 2 entries", content.contains("numberOfEntries=\"2\""));
    }

    public void testInvalidMissingFile() throws Exception {
        // Create a test POM with absolute paths
        File basedir = new File(getBasedir());
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
            "         http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>com.dataliquid.test</groupId>\n" +
            "    <artifactId>test-invalid-missingfile</artifactId>\n" +
            "    <version>1.0.0</version>\n" +
            "    <build>\n" +
            "        <directory>" + basedir + "/target</directory>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>com.dataliquid.maven</groupId>\n" +
            "                <artifactId>distribution-verifier-maven-plugin</artifactId>\n" +
            "                <version>1.0.4-SNAPSHOT</version>\n" +
            "                <configuration>\n" +
            "                    <distributionArchiveFile>" + basedir + "/src/test/resources/invalid-missingfile/invalid_missingfile.zip</distributionArchiveFile>\n" +
            "                    <whitelist>" + basedir + "/src/test/resources/invalid-missingfile/whitelist.xml</whitelist>\n" +
            "                    <reportFile>" + basedir + "/target/test-invalid-report.xml</reportFile>\n" +
            "                    <reportType>xml</reportType>\n" +
            "                </configuration>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <goals>\n" +
            "                            <goal>verify</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>";
            
        File pom = new File(basedir, "target/test-invalid-missingfile-pom.xml");
        pom.getParentFile().mkdirs();
        FileUtils.writeStringToFile(pom, pomContent, "UTF-8");
        
        VerifyMojo mojo = (VerifyMojo) lookupMojo("verify", pom);
        assertNotNull(mojo);
        
        try {
            mojo.execute();
            fail("Expected build to fail due to missing file");
        } catch (Exception e) {
            // Expected - build should fail
            assertTrue("Should contain verification failed message", 
                e.getMessage().contains("Verification failed"));
        }
        
        // Check that the report was still generated
        File reportFile = new File(basedir, "target/test-invalid-report.xml");
        assertTrue("Report file should exist even on failure", reportFile.exists());
        
        String content = FileUtils.readFileToString(reportFile, "UTF-8");
        assertTrue("Report should contain INVALID status", content.contains("<VerificationResult status=\"INVALID\""));
    }

    public void testJUnitReportFormat() throws Exception {
        // Create a test POM with JUnit report format
        File basedir = new File(getBasedir());
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
            "         http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>com.dataliquid.test</groupId>\n" +
            "    <artifactId>test-junit-report</artifactId>\n" +
            "    <version>1.0.0</version>\n" +
            "    <build>\n" +
            "        <directory>" + basedir + "/target</directory>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>com.dataliquid.maven</groupId>\n" +
            "                <artifactId>distribution-verifier-maven-plugin</artifactId>\n" +
            "                <version>1.0.4-SNAPSHOT</version>\n" +
            "                <configuration>\n" +
            "                    <distributionArchiveFile>" + basedir + "/src/test/resources/valid-fullmatch/valid_fullmatch.zip</distributionArchiveFile>\n" +
            "                    <whitelist>" + basedir + "/src/test/resources/valid-fullmatch/whitelist.xml</whitelist>\n" +
            "                    <reportFile>" + basedir + "/target/test-junit-report.xml</reportFile>\n" +
            "                    <reportType>junit</reportType>\n" +
            "                </configuration>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <goals>\n" +
            "                            <goal>verify</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>";
            
        File pom = new File(basedir, "target/test-junit-report-pom.xml");
        pom.getParentFile().mkdirs();
        FileUtils.writeStringToFile(pom, pomContent, "UTF-8");
        
        VerifyMojo mojo = (VerifyMojo) lookupMojo("verify", pom);
        assertNotNull(mojo);
        
        // Execute mojo
        mojo.execute();
        
        // Check that the JUnit report was generated
        File reportFile = new File(basedir, "target/test-junit-report.xml");
        assertTrue("JUnit report file should exist", reportFile.exists());
        
        String content = FileUtils.readFileToString(reportFile, "UTF-8");
        assertTrue("Should contain testsuite element", content.contains("<testsuite"));
        assertTrue("Should contain testcase elements", content.contains("<testcase"));
    }

}