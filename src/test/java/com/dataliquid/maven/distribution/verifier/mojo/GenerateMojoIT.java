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
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.rules.TemporaryFolder;
import org.apache.commons.io.FileUtils;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import java.io.File;
import java.util.Iterator;

/**
 * Integration test for GenerateMojo
 */
public class GenerateMojoIT extends AbstractMojoTestCase {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File testResourcesDir;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testResourcesDir = new File("src/test/resources");
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGenerateWhitelist() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File(testResourcesDir, "generate-whitelist/generate_whitelist.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "generated-whitelist.xml");
        
        mojo.setDistributionArchiveFile(distributionFile);
        mojo.setWhitelist(outputFile);
        
        // Execute the generation
        mojo.execute();
        
        // Verify the file was created
        assertTrue("Whitelist file should be created", outputFile.exists());
        
        // Verify the content contains expected structure
        String content = FileUtils.readFileToString(outputFile, "UTF-8");
        assertTrue("Should contain whitelist root element", content.contains("<whitelist>"));
        assertTrue("Should contain entry elements", content.contains("<entry"));
        assertTrue("Should contain path attributes", content.contains("path="));
        assertTrue("Should contain md5 attributes", content.contains("md5="));
        
        // Verify XML is well-formed
        assertNotNull("Should be able to parse XML", parseXml(outputFile));
    }

    @Test
    public void testGenerateWhitelistWithTemplateComparison() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File(testResourcesDir, "generate-whitelist/generate_whitelist.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "generated-whitelist.xml");
        File templateFile = new File(testResourcesDir, "generate-whitelist/whitelist.tmpl.xml");
        
        mojo.setDistributionArchiveFile(distributionFile);
        mojo.setWhitelist(outputFile);
        
        // Execute the generation
        mojo.execute();
        
        // Read both files
        String generatedContent = FileUtils.readFileToString(outputFile, "UTF-8");
        String templateContent = FileUtils.readFileToString(templateFile, "UTF-8");
        
        // Both should have similar structure (though MD5s might differ)
        assertTrue("Generated content should contain whitelist element", generatedContent.contains("<whitelist>"));
        assertTrue("Template content should contain whitelist element", templateContent.contains("<whitelist>"));
        
        // Count entries - should be similar
        int generatedEntries = countOccurrences(generatedContent, "<entry");
        int templateEntries = countOccurrences(templateContent, "<entry");
        assertTrue("Should have at least one entry", generatedEntries > 0);
        
        // Use XMLUnit to compare structure (ignoring MD5 values)
        Diff diff = DiffBuilder.compare(templateContent)
            .withTest(generatedContent)
            .ignoreWhitespace()
            .ignoreComments()
            .checkForSimilar()
            .withAttributeFilter(attr -> !"md5".equals(attr.getName()))
            .build();
        
        assertFalse("XML structures should be similar (ignoring md5 values)", diff.hasDifferences());
    }

    @Test(expected = MojoExecutionException.class)
    public void testGenerateWhitelistMissingDistribution() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File(testResourcesDir, "non-existent.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "generated-whitelist.xml");
        
        mojo.setDistributionArchiveFile(distributionFile);
        mojo.setWhitelist(outputFile);
        
        // This should throw MojoExecutionException
        mojo.execute();
    }

    @Test
    public void testGenerateWhitelistCreatesParentDirectory() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File(testResourcesDir, "generate-whitelist/generate_whitelist.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "subdir/nested/generated-whitelist.xml");
        
        assertFalse("Parent directory should not exist initially", outputFile.getParentFile().exists());
        
        mojo.setDistributionArchiveFile(distributionFile);
        mojo.setWhitelist(outputFile);
        
        // Execute the generation
        mojo.execute();
        
        // Verify the directory structure was created
        assertTrue("Parent directory should be created", outputFile.getParentFile().exists());
        assertTrue("Whitelist file should be created", outputFile.exists());
    }

    @Test
    public void testGenerateWhitelistOverwritesExisting() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File(testResourcesDir, "generate-whitelist/generate_whitelist.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "generated-whitelist.xml");
        
        // Create an existing file with different content
        FileUtils.writeStringToFile(outputFile, "<whitelist><entry path=\"/test\" /></whitelist>", "UTF-8");
        assertTrue("Pre-existing file should exist", outputFile.exists());
        
        long originalLength = outputFile.length();
        
        mojo.setDistributionArchiveFile(distributionFile);
        mojo.setWhitelist(outputFile);
        
        // Execute the generation
        mojo.execute();
        
        // Verify the file was overwritten
        assertTrue("Whitelist file should still exist", outputFile.exists());
        assertFalse("File content should have changed", originalLength == outputFile.length());
        
        String content = FileUtils.readFileToString(outputFile, "UTF-8");
        assertFalse("Should not contain test entry", content.contains("path=\"/test\""));
    }

    @Test
    public void testGenerateWhitelistWithEmptyZip() throws Exception {
        // Create an empty zip file
        File emptyZip = new File(temporaryFolder.getRoot(), "empty.zip");
        org.zeroturnaround.zip.ZipUtil.packEntries(new File[0], emptyZip);
        
        GenerateMojo mojo = new GenerateMojo();
        File outputFile = new File(temporaryFolder.getRoot(), "empty-whitelist.xml");
        
        mojo.setDistributionArchiveFile(emptyZip);
        mojo.setWhitelist(outputFile);
        
        // Execute the generation
        mojo.execute();
        
        // Verify the file was created with empty whitelist
        assertTrue("Whitelist file should be created", outputFile.exists());
        
        String content = FileUtils.readFileToString(outputFile, "UTF-8");
        assertTrue("Should contain whitelist root element", content.contains("<whitelist>"));
        assertTrue("Should contain closing whitelist tag", content.contains("</whitelist>"));
        assertFalse("Should not contain entry elements", content.contains("<entry"));
    }

    private int countOccurrences(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = str.indexOf(findStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }

    private org.w3c.dom.Document parseXml(File xmlFile) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory dbFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(xmlFile);
    }
}