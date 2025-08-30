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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.zeroturnaround.zip.ZipUtil;

import com.dataliquid.maven.distribution.verifier.domain.Entry;
import com.dataliquid.maven.distribution.verifier.domain.ResultEntry;
import com.dataliquid.maven.distribution.verifier.domain.VerificationStatus;
import com.dataliquid.maven.distribution.verifier.domain.VerifierResult;

public class VerifierService
{
    private static final String EMPTY = "";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public VerifierResult verify(File distributionArchiveFile, File workDirectory, File whitelist, Map<String, String> properties)
    {
        List<ResultEntry> verificationResults = new LinkedList<>();
        boolean verificationStatus = false;
        try
        {
            File destinationDirectory = determineDestinationDirectory(distributionArchiveFile, workDirectory);

            if (logger.isInfoEnabled())
            {
                logger.info("Unzip distribution archive file " + distributionArchiveFile.getPath() + " to " + destinationDirectory);
            }

            ZipUtil.unpack(distributionArchiveFile, destinationDirectory);

            if (logger.isInfoEnabled())
            {
                logger.info("File unzipped successfully");
            }

            if (logger.isInfoEnabled())
            {
                logger.info("Loading whitelist " + whitelist);
            }
            List<Entry> entries = loadWhitelist(whitelist, properties);
            if (logger.isInfoEnabled())
            {
                logger.info("Whitelist file loaded successfully - Entries: " + entries.size());
            }

            if (logger.isInfoEnabled())
            {
                logger.info("Verifying whitelist files against distribution archive");
            }

            verificationStatus = verifyDistributionArchive(destinationDirectory, entries, destinationDirectory, verificationResults);

            if (logger.isInfoEnabled())
            {
                logger.info("Verification completed.");
            }

        }
        catch (Exception e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Error occurred : {}", e.getMessage(), e);
            }
        }

        return new VerifierResult(verificationStatus, verificationResults);

    }

    private File determineDestinationDirectory(File distributionArchiveFile, File workDirectory)
    {
        File destinationDirectory;
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
        return destinationDirectory;
    }

    private boolean verifyDistributionArchive(File directory, List<Entry> entries, File originalDirectory,
            List<ResultEntry> verificationResults) throws Exception
    {
        boolean allEntriesValid = verifyAllEntries(directory, entries, verificationResults);
        boolean allFilesInWhitelist = verifyAllFilesInWhitelist(directory, entries, originalDirectory, verificationResults);

        return allEntriesValid && allFilesInWhitelist;
    }

    private boolean verifyAllEntries(File directory, List<Entry> entries, List<ResultEntry> verificationResults) throws Exception
    {
        boolean allValid = true;
        for (Entry entry : entries)
        {
            boolean isValid = verifySingleEntry(directory, entry, verificationResults);
            allValid = allValid && isValid;
        }
        return allValid;
    }

    private boolean verifySingleEntry(File directory, Entry entry, List<ResultEntry> verificationResults) throws Exception
    {
        ResultEntry resultEntry = createResultEntry(entry);
        verificationResults.add(resultEntry);

        File currentFile = new File(directory, entry.getPath());

        if (!currentFile.exists())
        {
            return handleMissingFile(resultEntry, entry);
        }

        logDebug("Defined entry found " + entry.getPath());

        if (shouldVerifyChecksum(entry))
        {
            return verifyChecksum(currentFile, entry, resultEntry);
        }

        markAsSuccessful(resultEntry);
        return true;
    }

    private ResultEntry createResultEntry(Entry entry)
    {
        ResultEntry resultEntry = new ResultEntry();
        resultEntry.setPath(entry.getPath());
        resultEntry.setMd5(entry.getMd5());
        return resultEntry;
    }

    private boolean handleMissingFile(ResultEntry resultEntry, Entry entry)
    {
        logDebug("Defined file is not found " + entry.getPath());
        markAsFailed(resultEntry, "Defined file not found");
        return false;
    }

    private boolean shouldVerifyChecksum(Entry entry)
    {
        return entry.getMd5() != null && !entry.getMd5().isEmpty();
    }

    private boolean verifyChecksum(File file, Entry entry, ResultEntry resultEntry) throws Exception
    {
        String actualChecksum = getFileChecksum(file);
        String expectedChecksum = entry.getMd5();

        if (actualChecksum.equals(expectedChecksum))
        {
            logDebug("MD5 Checksum of file " + file.getPath() + " is identical to " + entry.getPath());
            markAsSuccessful(resultEntry);
            return true;
        }
        else
        {
            logDebug("MD5 checksum of file " + file.getPath() + " is different to " + entry.getPath());
            markAsFailed(resultEntry, "File found but with a different MD5 Checksum " + actualChecksum);
            return false;
        }
    }

    private void markAsSuccessful(ResultEntry resultEntry)
    {
        resultEntry.setStatus(VerificationStatus.SUCCESS.name());
        resultEntry.setMessage("Validation passed successfully");
    }

    private void markAsFailed(ResultEntry resultEntry, String message)
    {
        resultEntry.setStatus(VerificationStatus.FAILED.name());
        resultEntry.setMessage(message);
    }

    private void logDebug(String message)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(message);
        }
    }

    private boolean verifyAllFilesInWhitelist(File directory, List<Entry> whitelistEntries, File originalDirectory,
            List<ResultEntry> verificationResults) throws Exception
    {
        boolean allFilesFound = true;
        File[] directoryEntries = directory.listFiles();
        for (File directoryEntry : directoryEntries)
        {
            if (directoryEntry.isDirectory())
            {
                if (!verifyAllFilesInWhitelist(directoryEntry, whitelistEntries, originalDirectory, verificationResults))
                {
                    allFilesFound = false;
                }
            }
            else
            {
                if (!verifyFileInWhitelist(directoryEntry, whitelistEntries, originalDirectory, verificationResults))
                {
                    allFilesFound = false;
                }
            }
        }
        return allFilesFound;
    }

    private boolean verifyFileInWhitelist(File subDirectory, List<Entry> entries, File originalDirectory,
            List<ResultEntry> verificationResults) throws DOMException, NoSuchAlgorithmException, IOException
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
            ResultEntry resultEntry = new ResultEntry();
            verificationResults.add(resultEntry);
            resultEntry.setPath(strippedDirectory);
            resultEntry.setMd5(getFileChecksum(subDirectory));
            resultEntry.setStatus(VerificationStatus.FAILED.name());
            resultEntry.setMessage("File is not defined in whitelist");
        }
        return exists;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<Entry> loadWhitelist(File whitelist, Map<String, String> properties) throws Exception
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(whitelist);

        List<Entry> entries = new ArrayList<>();
        List<Node> nodes = document.selectNodes("//whitelist/entry");
        for (Node node : nodes)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;

                Entry entry = new Entry();
                entry.setPath(element.attributeValue("path"));
                entry.setMd5(element.attributeValue("md5"));
                evaluate(entry, properties);
                entries.add(entry);
                if (logger.isDebugEnabled())
                {
                    logger.debug("<entry path=\"" + entry.getPath() + "\" md5=\"" + entry.getMd5() + "\"");
                }
            }
        }

        return entries;
    }

    private void evaluate(Entry entry, Map<String, String> properties)
    {
        String path = evaluateString(entry.getPath(), properties);
        entry.setPath(path);
    }

    /**
     * Evaluate variable within given string value
     *
     * @param value
     *            lorem ${var} elit
     * @param variables
     *            Map<String, String> vars; vars.put("var", "ipsum");
     * @return
     */
    private String evaluateString(String value, Map<String, String> variables)
    {
        final Matcher matcher = Pattern.compile("\\$\\{(.*?)\\}").matcher(value);
        final StringBuffer buffer = new StringBuffer(value.length());
        while (matcher.find())
        {
            if (variables.containsKey(matcher.group(1)))
            {
                matcher.appendReplacement(buffer, variables.get(matcher.group(1)));
            }
            else
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Variable '" + matcher.group(1) + "' is defined but could not resolved by the given variables.");
                }
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException
    {
        try (InputStream is = Files.newInputStream(file.toPath()))
        {
            return DigestUtils.md5Hex(is);
        }
    }

}
