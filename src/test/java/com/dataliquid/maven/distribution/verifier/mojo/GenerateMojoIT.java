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

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import java.io.File;
import org.apache.commons.io.FileUtils;

import static org.junit.Assert.*;

/**
 * Integration test for GenerateMojo
 */
public class GenerateMojoIT {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGenerateMojoInstantiation() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        assertNotNull("Mojo should be instantiated", mojo);
    }

    @Test
    public void testGenerateWhitelist() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File("src/test/resources/generate-whitelist/generate_whitelist.zip");
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
    }

    @Test
    public void testGenerateWhitelistWithTemplateComparison() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File("src/test/resources/generate-whitelist/generate_whitelist.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "generated-whitelist.xml");
        File templateFile = new File("src/test/resources/generate-whitelist/whitelist.tmpl.xml");
        
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
        assertEquals("Should have same number of entries", templateEntries, generatedEntries);
    }

    @Test(expected = org.apache.maven.plugin.MojoExecutionException.class)
    public void testGenerateWhitelistMissingDistribution() throws Exception {
        GenerateMojo mojo = new GenerateMojo();
        
        File distributionFile = new File("src/test/resources/non-existent.zip");
        File outputFile = new File(temporaryFolder.getRoot(), "generated-whitelist.xml");
        
        mojo.setDistributionArchiveFile(distributionFile);
        mojo.setWhitelist(outputFile);
        
        // This should throw MojoExecutionException
        mojo.execute();
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
}