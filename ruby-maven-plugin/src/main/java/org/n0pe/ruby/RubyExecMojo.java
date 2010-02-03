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
import java.io.FileReader;
import java.io.IOException;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import org.apache.commons.io.IOUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.jruby.javasupport.bsf.JRubyEngine;

/**
 * @goal exec
 * @author Paul Merlin <eskatos@n0pe.org>
 */
public class RubyExecMojo
        extends AbstractRubyMojo {

    /**
     * @parameter
     * @required
     */
    private File script;

    public void execute()
            throws MojoExecutionException, MojoFailureException {
        if (workingDirectory == null) {
            try {
                BSFManager.registerScriptingEngine("ruby", JRubyEngine.class.getName(), new String[]{"rb"});
                BSFManager rubyManager = new BSFManager();
                rubyManager.exec("ruby", script.getAbsolutePath(), -1, -1,
                                 IOUtils.toString(new FileReader(script)));
            } catch (IOException ex) {
                throw new MojoExecutionException(ex.getMessage(), ex);
            } catch (BSFException ex) {
                throw new MojoExecutionException(ex.getMessage(), ex);
            }
        } else {
            getLog().warn(
                    "workingDirectory is set, we need to fork another JVM process to change the ruby code execution working directory");
            forkJavaVM(new String[]{
                        "-cp",
                        getClasspath(),
                        RubyExecMain.class.getName(),
                        script.getAbsolutePath()
                    });
        }
    }

}
