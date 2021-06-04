package ua.knu.resolver;

import ua.knu.token.Token;

import java.util.regex.Pattern;

public class SpecialSymbolResolver implements TokenResolver {

    private static final int MAX_LENGTH = 2;
    private static final Pattern isSpecialSymbol = Pattern.compile("(\\?[.:]?)|\\.\\.|(:){1,2}|->|!!|\\$|@");

    @Override
    public Token resolve(String text, int position) {
        Token result = null;

        for (var i = 1; i <= MAX_LENGTH && position + i <= text.length(); i++) {
            var substring = text.substring(position, position + i);

            if (isSpecialSymbol.matcher(substring).matches()) {
                result = new Token(Token.Type.SPECIAL_SYMBOL, substring, position, position + i);
            }
        }

        return result;
    }
}
