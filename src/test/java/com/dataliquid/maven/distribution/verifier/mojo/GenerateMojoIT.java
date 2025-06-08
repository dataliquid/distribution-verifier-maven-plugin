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
 * Integration test for generate goal functionality
 */
public class GenerateMojoIT extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGenerateWhitelist() throws Exception {
        // Create test POM
        File basedir = new File(getBasedir());
        File testPom = createTestPom("generate-whitelist-test", 
            basedir + "/src/test/resources/generate-whitelist/generate_whitelist.zip",
            basedir + "/target/generated-whitelist.xml");
        
        GenerateMojo mojo = (GenerateMojo) lookupMojo("generate", testPom);
        assertNotNull(mojo);
        
        // Execute mojo
        mojo.execute();
        
        // Verify the file was created
        File outputFile = new File(basedir, "target/generated-whitelist.xml");
        assertTrue("Whitelist file should be created", outputFile.exists());
        
        // Verify the content contains expected structure
        String content = FileUtils.readFileToString(outputFile, "UTF-8");
        assertTrue("Should contain whitelist root element", content.contains("<whitelist>"));
        assertTrue("Should contain entry elements", content.contains("<entry"));
        assertTrue("Should contain path attributes", content.contains("path="));
        assertTrue("Should contain md5 attributes", content.contains("md5="));
    }

    private File createTestPom(String artifactId, String distributionFile, String outputFile) throws Exception {
        String pomXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
            "         http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>com.dataliquid.test</groupId>\n" +
            "    <artifactId>" + artifactId + "</artifactId>\n" +
            "    <version>1.0.0</version>\n" +
            "    <build>\n" +
            "        <directory>${basedir}/target</directory>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>com.dataliquid.maven</groupId>\n" +
            "                <artifactId>distribution-verifier-maven-plugin</artifactId>\n" +
            "                <version>1.0.4-SNAPSHOT</version>\n" +
            "                <configuration>\n" +
            "                    <distributionArchiveFile>" + distributionFile + "</distributionArchiveFile>\n" +
            "                    <whitelist>" + outputFile + "</whitelist>\n" +
            "                </configuration>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <goals>\n" +
            "                            <goal>generate</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>";
            
        File pom = new File(getBasedir(), "target/test-" + artifactId + "-pom.xml");
        pom.getParentFile().mkdirs();
        FileUtils.writeStringToFile(pom, pomXml, "UTF-8");
        return pom;
    }
}