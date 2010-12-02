/**
 * ruby-maven-plugin : hook ruby scripts in the build lifecycle
 * 
 * Copyright (C) 2008  Paul Merlin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.n0pe.ruby;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * @author Paul Merlin <eskatos@n0pe.org>
 */
public abstract class AbstractRubyMojo
        extends AbstractMojo {

    /**
     * @parameter
     */
    protected File workingDirectory;

    /**
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    private Map pluginArtifactMap;

    /** 
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /** 
     * @component
     * @required
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /** 
     * @parameter expression="${project.remoteArtifactRepositories}" 
     * @required
     * @readonly
     */
    private List remoteRepositories;

    protected void forkJavaVM(String[] args)
            throws MojoExecutionException {
        try {
            final Commandline commandLine = new Commandline();
            commandLine.setExecutable(getJavaExecutable().getAbsolutePath());
            commandLine.setWorkingDirectory(workingDirectory.getAbsolutePath());
            commandLine.addArguments(args);
            final StreamConsumer stdout = new StreamConsumer() {

                public void consumeLine(String line) {
                    getLog().info(line);
                }

            };
            final StreamConsumer stderr = new StreamConsumer() {

                public void consumeLine(String line) {
                    getLog().error(line);
                }

            };
            int ret = CommandLineUtils.executeCommandLine(commandLine, stdout, stderr);
            if (ret != 0) {
                throw new MojoExecutionException("RubyRunMain exited with status code: " + ret);
            }
        } catch (CommandLineException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    protected String getClasspath()
            throws MojoExecutionException {
        try {
            final List classpath = new ArrayList();
            // Loading self dependencies
            final List scopesToUse = new ArrayList();
            scopesToUse.add(Artifact.SCOPE_COMPILE);
            scopesToUse.add(Artifact.SCOPE_RUNTIME);
            Iterator it = pluginArtifactMap.keySet().iterator();
            while (it.hasNext()) {
                Artifact artifact = (Artifact) pluginArtifactMap.get((String) it.next());
                if (scopesToUse.contains(artifact.getScope())) {
                    artifactResolver.resolve(artifact, remoteRepositories, localRepository);
                    classpath.add(artifact.getFile().getAbsolutePath());
                }
            }
            // Loading self
            final Artifact self = artifactFactory.createArtifact("org.n0pe.mojo",
                                                                 "ruby-maven-plugin",
                                                                 "0.1-SNAPSHOT",
                                                                 Artifact.SCOPE_RUNTIME,
                                                                 "maven-plugin");
            artifactResolver.resolve(self, remoteRepositories, localRepository);
            classpath.add(self.getFile().getAbsolutePath());
            // Building the classpath string
            it = classpath.iterator();
            final StringWriter sw = new StringWriter();
            while (it.hasNext()) {
                sw.append((String) it.next());
                if (it.hasNext()) {
                    sw.append(File.pathSeparator);
                }
            }
            return sw.toString();
        } catch (ArtifactResolutionException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (ArtifactNotFoundException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    protected File getJavaExecutable()
            throws MojoExecutionException {
        final String javaHome = System.getenv("JAVA_HOME");
        if (StringUtils.isEmpty(javaHome)) {
            throw new MojoExecutionException("JAVA_HOME is not set, cannot continue");
        }
        final File java = new File(javaHome + File.separator + "bin" + File.separator + "java");
        if (!java.exists()) {
            throw new MojoExecutionException("Cannot find the java executable at: " + java.getAbsolutePath());
        }
        if (!java.canExecute()) {
            throw new MojoExecutionException("Found java executable is not executable, cannot continue");
        }
        return java;
    }

}
