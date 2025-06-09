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
package com.dataliquid.maven.distribution.verifier.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.dataliquid.maven.distribution.verifier.domain.ResultEntry;
import com.dataliquid.maven.distribution.verifier.domain.VerifierResult;

public class VerifierServiceTest
{

    private VerifierService verifierService;

    private File outputDirectory;

    private Map<String, String> variables;

    @Before
    public void setUp() throws IOException
    {
        verifierService = new VerifierService();
        variables = new HashMap<>();
        outputDirectory = new File("target/");
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldVerifyValid() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/valid-fullmatch/whitelist.xml");
        File distributionArchive = new File("src/test/resources/valid-fullmatch/valid_fullmatch.zip");
    
        // when
        VerifierResult verifierResult = verifierService.verify(distributionArchive, outputDirectory, whitelist, variables);
    
        // then
        final List<ResultEntry> verificationResults = verifierResult.getResultEntries();;
        verificationResults.stream().forEach(System.out::println);
        assertThat(verificationResults,contains( 
                allOf(
                    hasProperty("status", is("SUCCESS")),
                    hasProperty("message", is("Validation passed successfully")), 
                    hasProperty("path", is("/Sample.md")),
                    hasProperty("md5", is("4114b3e750902c5404ffe4864b3e11b8"))),
                allOf(
                        hasProperty("status", is("SUCCESS")),
                        hasProperty("message", is("Validation passed successfully")), 
                        hasProperty("path", is("/Sample.txt")),
                        hasProperty("md5", is("193fa5e788a1800a760d1108051c2363")))
                ));
        
        assertThat(verifierResult.isValid(), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldVerifyWithVariables() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/valid-fullmatch-variables/whitelist.xml");
        File distributionArchive = new File("src/test/resources/valid-fullmatch-variables/valid_fullmatch_variables.zip");

        variables.put("project.artifactId", "myartifact");
        variables.put("project.version", "1.0.0");

        // when
        VerifierResult verifierResult = verifierService.verify(distributionArchive, outputDirectory, whitelist, variables);

        // then
        final List<ResultEntry> verificationResults = verifierResult.getResultEntries();;
        verificationResults.stream().forEach(System.out::println);
        assertThat(verificationResults,contains( 
                allOf(
                    hasProperty("status", is("SUCCESS")),
                    hasProperty("message", is("Validation passed successfully")), 
                    hasProperty("path", is("/Sample-1.0.0.md")),
                    hasProperty("md5", is("4114b3e750902c5404ffe4864b3e11b8"))),
                allOf(
                        hasProperty("status", is("SUCCESS")),
                        hasProperty("message", is("Validation passed successfully")), 
                        hasProperty("path", is("/Sample-myartifact.txt")),
                        hasProperty("md5", is("193fa5e788a1800a760d1108051c2363")))
                ));
        
        assertThat(verifierResult.isValid(), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldVerifyInvalidMissingFile() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-missingfile/whitelist.xml");
        File distributionArchive = new File("src/test/resources/invalid-missingfile/invalid_missingfile.zip");

        // when
        VerifierResult verifierResult = verifierService.verify(distributionArchive, outputDirectory, whitelist, variables);

        // then
        final List<ResultEntry> verificationResults = verifierResult.getResultEntries();
        verificationResults.stream().forEach(System.out::println);
        assertThat(verificationResults,contains( 
                allOf(
                    hasProperty("status", is("SUCCESS")),
                    hasProperty("message", is("Validation passed successfully")), 
                    hasProperty("path", is("/Sample.md")),
                    hasProperty("md5", is("4114b3e750902c5404ffe4864b3e11b8"))),
                allOf(
                        hasProperty("status", is("SUCCESS")),
                        hasProperty("message", is("Validation passed successfully")), 
                        hasProperty("path", is("/Sample.txt")),
                        hasProperty("md5", is("193fa5e788a1800a760d1108051c2363"))),
                allOf(
                        hasProperty("status", is("FAILED")),
                        hasProperty("message", is("Defined file not found")), 
                        hasProperty("path", is("/Sample.adoc")),
                        hasProperty("md5", is("193fa5e788a1800a760d1108051c7778")))
                ));
        
        assertThat(verifierResult.isValid(), is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldVerifyInvalidFoundUndefinedFile() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-found-undefined-file/whitelist.xml");
        File distributionArchive = new File("src/test/resources/invalid-found-undefined-file/invalid_found_undefined_file.zip");

        // when
        VerifierResult verifierResult = verifierService.verify(distributionArchive, outputDirectory, whitelist, variables);

        // then
        final List<ResultEntry> verificationResults = verifierResult.getResultEntries();
        verificationResults.stream().forEach(System.out::println);
        assertThat(verificationResults,contains( 
                allOf(
                    hasProperty("status", is("SUCCESS")),
                    hasProperty("message", is("Validation passed successfully")), 
                    hasProperty("path", is("/Sample.md")),
                    hasProperty("md5", is("4114b3e750902c5404ffe4864b3e11b8"))),
                allOf(
                        hasProperty("status", is("SUCCESS")),
                        hasProperty("message", is("Validation passed successfully")), 
                        hasProperty("path", is("/Sample.txt")),
                        hasProperty("md5", is("193fa5e788a1800a760d1108051c2363"))),
                allOf(
                        hasProperty("status", is("FAILED")),
                        hasProperty("message", is("File is not defined in whitelist")), 
                        hasProperty("path", is("/Sample.adoc")),
                        hasProperty("md5", is("0430eba9643b5e60e49c055eb16cbf7a")))
                ));
        
        assertThat(verifierResult.isValid(), is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldVerifyInvalidDifferentMd5Checksum() throws Exception
    {
        // given
        File whitelist = new File("src/test/resources/invalid-different-md5-checksum/whitelist.xml");
        File distributionArchive = new File("src/test/resources/invalid-different-md5-checksum/invalid_different_md5_checksum.zip");

        // when
        VerifierResult verifierResult = verifierService.verify(distributionArchive, outputDirectory, whitelist, variables);

        // then
        final List<ResultEntry> verificationResults = verifierResult.getResultEntries();
        verificationResults.stream().forEach(System.out::println);
        assertThat(verificationResults,contains( 
                allOf(
                    hasProperty("status", is("SUCCESS")),
                    hasProperty("message", is("Validation passed successfully")), 
                    hasProperty("path", is("/Sample.md")),
                    hasProperty("md5", is("4114b3e750902c5404ffe4864b3e11b8"))),
                allOf(
                        hasProperty("status", is("FAILED")),
                        hasProperty("message", is("File found but with a different MD5 Checksum 193fa5e788a1800a760d1108051c2363")), 
                        hasProperty("path", is("/Sample.txt")),
                        hasProperty("md5", is("193fa5e788a1800a760d1108051c4711")))
                ));
        
        assertThat(verifierResult.isValid(), is(false));
    }
}
