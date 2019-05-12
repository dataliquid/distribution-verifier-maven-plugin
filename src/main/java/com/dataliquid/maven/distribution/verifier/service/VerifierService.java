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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.zeroturnaround.zip.ZipUtil;

import com.dataliquid.maven.distribution.verifier.domain.Entry;
import com.dataliquid.maven.distribution.verifier.domain.VerificationStatus;

public class VerifierService
{
    private static final String EMPTY = "";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean verify(File distributionArchiveFile, File workDirectory, File whitelist, String reportFile)
    {

        Boolean verificationStatus = null;
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

            logger.info("Loading whitelist " + whitelist);
            List<Entry> entries = loadWhitelist(whitelist);
            logger.info("Whitelist file loaded successfully - Entries: " + entries.size());

            logger.info("Verifying whitelist files against distribution archive");

            verificationStatus = verifyDistributionArchive(destinationDirectory, entries, reportFile, destinationDirectory);

            logger.info("Verification completed. See the generate report at " + reportFile);

        }
        catch (Exception e)
        {
            logger.error("Error occurred : {}", e.getMessage(), e);
            verificationStatus = false;
        }

        return verificationStatus;

    }

    private boolean verifyDistributionArchive(File directory, List<Entry> entries, String reportFile, File originalDirectory)
            throws Exception
    {
        boolean verificationStatus = true;

        Document document = DocumentHelper.createDocument();
        Element report = document.addElement("report");

        for (Entry entry : entries)
        {
            Element reportEntry = report.addElement("entry");
            reportEntry.addAttribute("path", entry.getPath());
            reportEntry.addAttribute("md5", entry.getMd5());

            File currentFile = new File(directory.getPath().concat(entry.getPath()));
            if (currentFile.exists())
            {
                logger.debug("Defined entry found " + entry.getPath());

                if (entry.getMd5() != null && !entry.getMd5().isEmpty())
                {
                    String fileMd5Checksum = getFileChecksum(currentFile);
                    if (fileMd5Checksum.equals(entry.getMd5()))
                    {
                        logger.debug("MD5 Checksum of file " + currentFile.getPath() + " is identical to " + entry.getPath());

                        Element result = reportEntry.addElement("result");
                        result.addAttribute("status", VerificationStatus.SUCCESS.name());
                        result.addAttribute("message", "Validation passed successfully");
                    }
                    else
                    {
                        verificationStatus = false;

                        logger.debug("MD5 checksum of file " + currentFile.getPath() + " is different to " + entry.getPath());

                        Element result = reportEntry.addElement("result");
                        result.addAttribute("status", VerificationStatus.FAILED.name());
                        result.addAttribute("message", "File found but with a different MD5 Checksum " + fileMd5Checksum);
                    }
                }
                else
                {
                    Element result = reportEntry.addElement("result");
                    result.addAttribute("status", VerificationStatus.SUCCESS.name());
                    result.addAttribute("message", "Validation passed successfully");
                }
            }
            else
            {
                verificationStatus = false;

                logger.debug("Defined file is not found " + entry.getPath() + EMPTY);

                Element result = reportEntry.addElement("result");
                result.addAttribute("status", VerificationStatus.FAILED.name());
                result.addAttribute("message", "Defined file not found");
            }

        }

        verifyAllFilesInWhitelist(directory, entries, originalDirectory, report);

        try (FileWriter writer = new FileWriter(reportFile))
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
        }
        catch (Exception e)
        {
            throw e;
        }

        return verificationStatus;
    }

    public void verifyAllFilesInWhitelist(File directory, List<Entry> whitelistEntries, File originalDirectory, Element report)
            throws Exception
    {
        File[] directoryEntries = directory.listFiles();
        for (File directoryEntry : directoryEntries)
        {
            if (directoryEntry.isDirectory())
            {
                verifyAllFilesInWhitelist(directoryEntry, whitelistEntries, originalDirectory, report);
            }
            else
            {
                verifyFileInWhitelist(directoryEntry, whitelistEntries, originalDirectory, report);
            }
        }
    }

    private void verifyFileInWhitelist(File subDirectory, List<Entry> entries, File originalDirectory, Element report)
            throws DOMException, NoSuchAlgorithmException, IOException
    {
        boolean exists = false;
        String strippedDirectory = FilenameUtils.normalize(subDirectory.getPath().replace(originalDirectory.getPath(), EMPTY), true);
        for (Entry entry : entries)
        {
            if (FilenameUtils.equalsNormalized(strippedDirectory, entry.getPath()))
            {
                exists = true;
                break;
            }
        }
        if (!exists)
        {
            Element reportEntry = report.addElement("entry");
            reportEntry.addAttribute("path", strippedDirectory);
            reportEntry.addAttribute("md5", getFileChecksum(subDirectory));

            Element result = reportEntry.addElement("result");
            result.addAttribute("status", VerificationStatus.FAILED.name());
            result.addAttribute("message", "File is not defined in whitelist");
        }

    }

    public List<Entry> loadWhitelist(File whitelist) throws Exception
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(whitelist);

        List<Entry> entries = new ArrayList<Entry>();
        List<Node> nodes = document.selectNodes("//whitelist/entry");
        for (Node node : nodes)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;

                Entry entry = new Entry();
                entry.setPath(element.attributeValue("path"));
                entry.setMd5(element.attributeValue("md5"));
                entries.add(entry);
                logger.debug("<entry path=\"" + entry.getPath() + "\" md5=\"" + entry.getMd5() + "\"");
            }
        }

        return entries;
    }

    private String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException
    {
        return DigestUtils.md5Hex(new FileInputStream(file));
    }

}
