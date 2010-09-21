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
public class Toky implements TokenReplacer {

    private final TokenReplacer impl;

    public Toky(TokenReplacer impl) {
        this.impl = impl;
    }

    public Toky() {
        this.impl = new CharSequenceTokenReplacer();
    }

    @Override
    public TokenReplacer register(String token, String value) {
        return impl.register(token, value);
    }

    @Override
    public TokenReplacer register(Token token) {
        return impl.register(token);
    }

    @Override
    public String substitute(String toSubstitute) {
        return impl.substitute(toSubstitute);
    }

    @Override
    public TokenReplacer withArgumentDelimiter(String argsSep) {
        return impl.withArgumentDelimiter(argsSep);
    }

    @Override
    public TokenReplacer withArgumentEnd(String argsEnd) {
        return impl.withArgumentEnd(argsEnd);
    }

    @Override
    public TokenReplacer withArgumentStart(String argsStart) {
        return impl.withArgumentStart(argsStart);
    }

    @Override
    public TokenReplacer withTokenEnd(String tokenEnd) {
        return impl.withTokenEnd(tokenEnd);
    }

    @Override
    public TokenReplacer withTokenStart(String tokenStart) {
        return impl.withTokenStart(tokenStart);
    }

}
