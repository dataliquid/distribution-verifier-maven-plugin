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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.zeroturnaround.zip.ZipUtil;

public class GenerateService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void generate(File distributionArchiveFile, File workDirectory, File whitelist)
    {

        try
        {
            File destinationDirectory = null;
            if (workDirectory != null)
            {
                String distributionWorkDirectory = distributionArchiveFile.getName().concat("-unzipped");
                destinationDirectory = new File(workDirectory, distributionWorkDirectory);
            }
            else
            {
                String name = distributionArchiveFile.getName().concat("-unzipped");
                destinationDirectory = new File(distributionArchiveFile.getParentFile(), name);
            }

            logger.info("Unzip distribution archive file " + distributionArchiveFile.getPath() + " to " + destinationDirectory);

            ZipUtil.unpack(distributionArchiveFile, destinationDirectory);

            logger.info("File unzipped successfully");

            logger.info("Generate whitelist template from distribution archive");

            generateWhitelist(destinationDirectory, destinationDirectory, whitelist);

            logger.info("Whitelist template has been generated. " + whitelist);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error occurred : {}", e.getMessage(), e);
        }

    }

    private boolean generateWhitelist(File directory, File originalDirectory, File whistlist) throws Exception
    {

        Document document = DocumentHelper.createDocument();
        Element whistlistElement = document.addElement("whitelist");

        generateWhistliste(directory, originalDirectory, whistlistElement);

        FileUtils.forceMkdir(new File(whistlist.getParent()));

        try (FileWriter writer = new FileWriter(whistlist))
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
        }
        catch (Exception e)
        {
            throw e;
        }

        return false;
    }

    public void generateWhistliste(File directory, File originalDirectory, Element whistlistElement) throws Exception
    {
        File[] directoryEntries = directory.listFiles();
        for (File directoryEntry : directoryEntries)
        {

            if (directoryEntry.isDirectory())
            {
                generateWhistliste(directoryEntry, originalDirectory, whistlistElement);
            }
            else
            {
                generateWhitelistEntry(directoryEntry, originalDirectory, whistlistElement);
            }
        }

    }

    private void generateWhitelistEntry(File subDirectory, File originalDirectory, Element whistlistElement)
            throws DOMException, NoSuchAlgorithmException, IOException
    {
        String strippedDirectory = FilenameUtils.normalize(subDirectory.getPath().replace(originalDirectory.getPath(), ""), true);

        Element entry = whistlistElement.addElement("entry");
        entry.addAttribute("path", strippedDirectory);
        entry.addAttribute("md5", getFileChecksum(subDirectory));

    }

    private String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException
    {
        return DigestUtils.md5Hex(new FileInputStream(file));
    }

}
