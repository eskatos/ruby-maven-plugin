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


import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.jruby.javasupport.bsf.JRubyEngine;


/**
 * @goal run
 * @author Paul Merlin <eskatos@n0pe.org>
 */
public class RubyRunMojo
        extends AbstractRubyMojo {


    /**
     * @parameter
     * @required
     */
    private String ruby;


    public void execute()
            throws MojoExecutionException, MojoFailureException {
        if (workingDirectory == null) {
            try {
                BSFManager.registerScriptingEngine("ruby", JRubyEngine.class.getName(), new String[]{"rb"});
                final BSFManager rubyManager = new BSFManager();
                rubyManager.eval("ruby", "RubyMojo", 0, 0, ruby);
            } catch (BSFException ex) {
                throw new MojoExecutionException(ex.getMessage(), ex);
            }
        } else {
            getLog().warn("");
            getLog().warn("");
            getLog().warn("WORKING DIRECTORY IS SET - USING AN HORRIBLE HACK");
            getLog().warn("We are now forking another JVM process to change the ruby code execution working directory");
            getLog().warn("");
            getLog().warn("");
            executeHorribleHack(new String[]{
                                    "-cp",
                                    getClasspath(),
                                    RubyRunMain.class.getName(),
                                    ruby
                                });
        }
    }


}
