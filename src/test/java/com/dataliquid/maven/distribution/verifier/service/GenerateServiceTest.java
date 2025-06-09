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

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class GenerateServiceTest
{
    private GenerateService verifierService;

    private File outputDirectory;

    @Before
    public void setUp() throws IOException
    {
        verifierService = new GenerateService();
        outputDirectory = new File("target/");
    }

    @Test
    public void shouldGenerateWhitelist() throws Exception
    {
        // given
        File whitelist = new File(outputDirectory, "generate-whitelist/whitelist.tmpl.xml");
        File expectedWhitelist = new File("src/test/resources/generate-whitelist/whitelist.tmpl.xml");
        File distributionArchive = new File("src/test/resources/generate-whitelist/generate_whitelist.zip");

        // when
        verifierService.generate(distributionArchive, outputDirectory, whitelist);

        // then
        assertThat(expectedWhitelist, isSimilarTo(whitelist).ignoreWhitespace().ignoreComments());

    }

}
