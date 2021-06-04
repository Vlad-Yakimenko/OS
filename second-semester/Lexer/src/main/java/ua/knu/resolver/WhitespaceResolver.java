package ua.knu.resolver;

import ua.knu.token.Token;

public class WhitespaceResolver implements TokenResolver {

    @Override
    public Token resolve(String text, int position) {
        var current = position;
        var state = 0;
        char currentChar;
        Token result = null;

        while (current < text.length()) {
            currentChar = text.charAt(current);
            current++;

            switch (state) {
                case 0:
                    if (" \t\n\r".indexOf(currentChar) != -1) {
                        state = 1;
                    } else {
                        return null;
                    }
                    continue;

                case 1:
                    if (" \t\n\r".indexOf(currentChar) == -1) {
                        return new Token(Token.Type.WHITESPACE, text.substring(position, current - 1), position, current - 1);
                    }
            }
        }

        if (state == 1) {
            result = new Token(Token.Type.WHITESPACE, text.substring(position, current), position, current);
        }

        return result;
    }
}
