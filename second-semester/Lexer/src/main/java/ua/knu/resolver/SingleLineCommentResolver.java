package ua.knu.resolver;

import ua.knu.token.Token;

public class SingleLineCommentResolver implements TokenResolver {

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
                        continue;
                    } else {
                        return null;
                    }

                case 1:
                    if (currentChar == '/') {
                        state = 2;
                        continue;
                    } else {
                        return null;
                    }

                case 2:
                    if (currentChar == '\n') {
                        return new Token(Token.Type.COMMENT, text.substring(position, current), position, current);
                    }
            }
        }

        return state == 2 ? new Token(Token.Type.COMMENT, text.substring(position, current), position, current) : null;
    }
}
