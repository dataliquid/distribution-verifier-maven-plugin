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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dataliquid.maven.distribution.verifier.service.GenerateService;

/**
 * Goal to generate whitelist template from the distribution archive file.
 */
@Mojo(name = "generate", requiresProject = false)
public class GenerateMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
    private File outputDirectory;

    @Parameter(property = "distributionArchiveFile", defaultValue = "${project.build.directory}/${project.build.finalName}.${project.packaging}")
    private File distributionArchiveFile;

    @Parameter(property = "whitelist", defaultValue = "${project.build.directory}/whitelist.tmpl.xml")
    private File whitelist;

    public void execute() throws MojoExecutionException
    {
        getLog().info("Generating whitelist file for distribution archive file " + distributionArchiveFile);
        GenerateService verifierPluginService = new GenerateService();
        verifierPluginService.generate(distributionArchiveFile, outputDirectory, whitelist);
        getLog().info("Whitelist generated.");
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

}
