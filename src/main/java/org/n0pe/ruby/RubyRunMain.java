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

import org.apache.commons.lang.StringUtils;

import org.jruby.javasupport.bsf.JRubyEngine;


/**
 * @author Paul Merlin <eskatos@n0pe.org>
 */
public class RubyRunMain {


    public static void main(String[] args) {
        try {
            if (args.length != 1 || StringUtils.isEmpty(args[0])) {
                System.err.println("RubyRunMain take only one parameter : a ruby string to eval");
                System.exit(1);
            }
            final String ruby = args[0];
            BSFManager.registerScriptingEngine("ruby", JRubyEngine.class.getName(), new String[]{"rb"});
            final BSFManager rubyManager = new BSFManager();
            rubyManager.eval("ruby", "RubyMojo", 0, 0, ruby);
        } catch (BSFException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }


}
