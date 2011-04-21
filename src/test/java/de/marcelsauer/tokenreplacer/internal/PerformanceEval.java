/**
 * Copyright (C) 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            for (int i = 0; i <= 999999; i++) { 
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
