package ua.knu.resolver;

import ua.knu.token.Token;
import ua.knu.util.Constants;

public class IdentifierResolver implements TokenResolver {

    @Override
    public Token resolve(String text, int position) {
        var current = position;
        var state = 0;
        Token result = null;

        while (current < text.length()) {
            var currentChar = text.charAt(current);
            current++;

            if (state == 0) {
                if (currentChar == '_' || Character.isLetter(currentChar)) {
                    state = 1;
                } else {
                    return null;
                }
            } else if (state == 1 && !(Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_' || currentChar == '$')) {
                return new Token(getType(text.substring(position, current - 1)), text.substring(position, current - 1), position, current - 1);
            }
        }

        if (state == 1) {
            result = new Token(getType(text.substring(position, current)), text.substring(position, current), position, current);
        }

        return result;
    }

    private Token.Type getType(String text) {
        if (Constants.HARD_KEYWORDS.contains(text)) return Token.Type.HARD_KEYWORD;
        if (Constants.SOFT_KEYWORDS.contains(text)) return Token.Type.SOFT_KEYWORD;
        if (Constants.MODIFIER_KEYWORDS.contains(text)) return Token.Type.MODIFIER_KEYWORD;
        return Token.Type.IDENTIFIER;
    }
}
