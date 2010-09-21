package de.marcelsauer.tokenreplacer;

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
public interface Generator {

    /**
     * @return the generated String that will be used to replace a token
     */
    String generate();

    /**
     * @param args
     *            to inject into the Generator. will be called before the call to {@link #generate()}. will never be
     *            null but can be of size 0!
     */
    void inject(String[] args);

}
