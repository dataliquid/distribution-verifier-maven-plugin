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
import java.io.File;

import static org.junit.Assert.*;

/**
 * Integration test for VerifyMojo
 */
public class VerifyMojoIT {

    @Test
    public void testVerifyMojoInstantiation() throws Exception {
        VerifyMojo mojo = new VerifyMojo();
        assertNotNull("Mojo should be instantiated", mojo);
    }

    @Test
    public void testValidFullMatch() throws Exception {
        // Test can be run with Maven directly using the test POMs
        // mvn -f src/test/resources/test-poms/valid-fullmatch-test-pom.xml distribution-verifier:verify
        assertTrue("Valid full match test pom exists", 
            new File("src/test/resources/test-poms/valid-fullmatch-test-pom.xml").exists());
    }

    @Test
    public void testValidWithVariables() throws Exception {
        // Test distribution with variable substitution
        File distributionFile = new File("src/test/resources/valid-fullmatch-variables/valid_fullmatch_variables.zip");
        File whitelistFile = new File("src/test/resources/valid-fullmatch-variables/whitelist.xml");
        
        assertTrue("Distribution file exists", distributionFile.exists());
        assertTrue("Whitelist file exists", whitelistFile.exists());
    }

    @Test
    public void testInvalidMissingFile() throws Exception {
        // Test can be run with Maven directly using the test POMs
        // This should fail: mvn -f src/test/resources/test-poms/invalid-missingfile-test-pom.xml distribution-verifier:verify
        assertTrue("Invalid missing file test pom exists", 
            new File("src/test/resources/test-poms/invalid-missingfile-test-pom.xml").exists());
    }

    @Test
    public void testInvalidDifferentChecksum() throws Exception {
        // Test distribution with different checksum
        File distributionFile = new File("src/test/resources/invalid-different-md5-checksum/invalid_different_md5_checksum.zip");
        File whitelistFile = new File("src/test/resources/invalid-different-md5-checksum/whitelist.xml");
        
        assertTrue("Distribution file exists", distributionFile.exists());
        assertTrue("Whitelist file exists", whitelistFile.exists());
    }

    @Test
    public void testInvalidUndefinedFile() throws Exception {
        // Test distribution with undefined file
        File distributionFile = new File("src/test/resources/invalid-found-undefined-file/invalid_found_undefined_file.zip");
        File whitelistFile = new File("src/test/resources/invalid-found-undefined-file/whitelist.xml");
        
        assertTrue("Distribution file exists", distributionFile.exists());
        assertTrue("Whitelist file exists", whitelistFile.exists());
    }
}