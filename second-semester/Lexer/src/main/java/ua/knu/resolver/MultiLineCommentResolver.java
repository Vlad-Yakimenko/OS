package ua.knu.resolver;

import ua.knu.token.Token;

public class MultiLineCommentResolver implements TokenResolver {

    @Override
    public Token resolve(String text, int position) {
        var current = position;
        var state = 0;
        char currentChar;

        while (current < text.length()) {
            currentChar = text.charAt(current);
            current++;

            switch (state) {
                case 0:
                    if (currentChar == '/') {
                        state = 1;
                    } else return null;
                    continue;
                case 1:
                    if (currentChar == '*') {
                        state = 2;
                    } else return null;
                    continue;
                case 2:
                    if (currentChar == '*') {
                        state = 3;
                    }
                    continue;
                case 3:
                    if (currentChar == '/') {
                        return new Token(Token.Type.MULTILINE_COMMENT, text.substring(position, current), position, current);
                    } else {
                        state = 2;
                    }
            }
        }

        return null;
    }
}
