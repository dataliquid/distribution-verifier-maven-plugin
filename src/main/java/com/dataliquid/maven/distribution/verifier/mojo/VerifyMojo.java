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
package com.dataliquid.maven.distribution.verifier.mojo;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dataliquid.maven.distribution.verifier.service.VerifierService;

/**
 * Goal to verify the distribution archive file.
 */
@Mojo(name = "verify", defaultPhase = LifecyclePhase.VERIFY)
public class VerifyMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
    private File outputDirectory;

    @Parameter(property = "distributionArchiveFile", defaultValue = "${project.build.directory}/${project.build.finalName}.${project.packaging}")
    private File distributionArchiveFile;

    @Parameter(property = "whitelist", defaultValue = "src/main/resources/whitelist.xml")
    private File whitelist;

    @Parameter(property = "report", defaultValue = "${project.build.directory}/report.xml")
    private String reportFile;

    /**
     * Variables which can be used in whitelist path attribute.
     * 
     * <pre>
     * <configuration>
     *   <environments>
     *      <my.prop1>value</my.prop1>
     *      <my.prop2>${project.version}-myvalue.jar</my.prop2>
     *    </environments>
     * </configuration>
     * </pre>
     */
    @Parameter(property = "properties")
    private Map<String, String> properties;

    public void execute() throws MojoExecutionException
    {
        initialize();
        getLog().info("Verifying the distribution archive file " + distributionArchiveFile);
        VerifierService verifierPluginService = new VerifierService();
        boolean match = verifierPluginService.verify(distributionArchiveFile, outputDirectory, whitelist, reportFile, properties);
        if (match)
        {
            getLog().info("Verification finished successfully.");
        }
        else
        {
            throw new MojoExecutionException("Verification failed! Report file generated: " + reportFile);
        }
    }

    private void initialize()
    {
        if (properties == null || properties.isEmpty())
        {
            properties = new LinkedHashMap<>();
            properties.put("project.groupId", project.getGroupId());
            properties.put("project.artifactId", project.getArtifactId());
            properties.put("project.version", project.getVersion());
        }
    }

    public MavenProject getProject()
    {
        return project;
    }

    public void setProject(MavenProject project)
    {
        this.project = project;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public File getDistributionArchiveFile()
    {
        return distributionArchiveFile;
    }

    public void setDistributionArchiveFile(File distributionArchiveFile)
    {
        this.distributionArchiveFile = distributionArchiveFile;
    }

    public File getWhitelist()
    {
        return whitelist;
    }

    public void setWhitelist(File whitelist)
    {
        this.whitelist = whitelist;
    }

    public String getReportFile()
    {
        return reportFile;
    }

    public void setReportFile(String reportFile)
    {
        this.reportFile = reportFile;
    }

}
