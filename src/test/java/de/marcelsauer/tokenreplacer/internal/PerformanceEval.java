/**
 * Token Replacer Copyright (C) 2010 Marcel Sauer <marcel DOT sauer AT gmx DOT de>
 * 
 * This file is part of Token Replacer.
 * 
 * Token Replacer is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Token Replacer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Token Replacer. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package de.marcelsauer.tokenreplacer.internal;

import java.util.ArrayList;
import java.util.List;

import de.marcelsauer.tokenreplacer.TokenReplacer;
import de.marcelsauer.tokenreplacer.Toky;

/**
 * @author msauer
 */
public class PerformanceEval {
	public static void main(String[] args) {

        List<Result> results = new ArrayList<Result>();

        List<String> toSubstitute = new ArrayList<String>(); 
        toSubstitute.add("{name}"); 
        toSubstitute.add("{name}{name}"); 
        toSubstitute.add("{name}{name}{name}{name}"); 
        toSubstitute.add("{name}{name}{name}{name}{name}{name}{name}{name}"); 
        toSubstitute.add("{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}{name}");

        TokenReplacer toky = new Toky().register("name", "xxx");

        for (String test : toSubstitute) { 
            long start = System.currentTimeMillis(); 
            for (int i = 0; i <= 1000000; i++) { 
                toky.substitute(test); 
            } 
            long end = System.currentTimeMillis(); 
            results.add(new Result(test, end - start)); 
        }

        for (Result result : results) {
            System.out.println(result.test + " -> " + result.millis); 
        } 
    }

    private static class Result { 
        final String test; 
        final long millis;

        public Result(String test, long millis) { 
            this.test = test; 
            this.millis = millis; 
        } 
    }
}
