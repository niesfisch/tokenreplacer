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
public interface TokenReplacer {

    /**
     * @param toSubstitute
     *            the string that contains the tokens
     * @return the result after replacing all tokens with the proper values
     */
    public abstract String substitute(final String toSubstitute);

    /**
     * registers a static value for a given token. if you need dynamic behaviour then use {@link #register(Generator)}
     * 
     * @param token
     *            the name of the token to be replaced e.g. for ${date} -> "date"
     * @param value
     *            the static value that will be used when replacing the token
     * @return the {@link TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer register(String token, String value);

    /**
     * @param token
     * @return the {@link #TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer register(Token token);

    /**
     * @param tokenStart
     *            e.g. '{'
     * @return the {@link #TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer withTokenStart(String tokenStart);

    /**
     * @param tokenEnd
     *            e.g. '}'
     * @return the {@link #TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer withTokenEnd(String tokenEnd);

    /**
     * @param argsSep
     *            e.g. ','
     * @return the {@link #TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer withArgumentDelimiter(String argsSep);

    /**
     * @param argsStart
     *            e.g. '('
     * @return the {@link #TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer withArgumentStart(String argsStart);

    /**
     * @param argsEnd
     *            e.g. ')'
     * @return the {@link #TokenReplacer} to allow method chaining
     */
    public abstract TokenReplacer withArgumentEnd(String argsEnd);

}