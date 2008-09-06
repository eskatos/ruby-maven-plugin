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
import org.apache.commons.lang.StringUtils;

import org.jruby.javasupport.bsf.JRubyEngine;


/**
 * @author Paul Merlin <eskatos@n0pe.org>
 */
public class RubyExecMain {


    public static void main(String[] args) {
        try {
            if (args.length != 1 || StringUtils.isEmpty(args[0])) {
                System.err.println("RubyExecMain take only one parameter : a ruby script to run");
                System.exit(1);
            }
            final File rubyScript = new File(args[0]);
            if (!rubyScript.exists() || !rubyScript.canRead()) {
                System.err.println("RubyExecMain could not read the ruby script: " + rubyScript.getAbsolutePath());
            }
            BSFManager.registerScriptingEngine("ruby", JRubyEngine.class.getName(), new String[]{"rb"});
            final BSFManager rubyManager = new BSFManager();

            rubyManager.exec("ruby", rubyScript.getAbsolutePath(), -1, -1,
                             IOUtils.toString(new FileReader(rubyScript)));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (BSFException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }


}
