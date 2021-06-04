package ua.knu.resolver;

import ua.knu.token.Token;

public class OperatorResolver implements TokenResolver {

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
                    if (currentChar == '&') {
                        state = 1;
                    } else if (currentChar == '|') {
                        state = 2;
                    } else if ("<>*/%".indexOf(currentChar) != -1) {
                        state = 3;
                        result = new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else if (currentChar == '!' || currentChar == '=') {
                        state = 4;
                        result = new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else if (currentChar == '+') {
                        state = 5;
                        result = new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else if (currentChar == '-') {
                        state = 6;
                        result = new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else {
                        return null;
                    }
                    continue;

                case 1:
                    if (currentChar == '&') {
                        return new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else {
                        return null;
                    }

                case 2:
                    if (currentChar == '|') {
                        return new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else {
                        return null;
                    }

                case 3:
                    if (currentChar == '=') {
                        return new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else {
                        return result;
                    }

                case 4:
                    if (currentChar == '=') {
                        state = 3;
                        result = new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                        continue;
                    } else {
                        return result;
                    }

                case 5:
                    if (currentChar == '+' || currentChar == '=') {
                        return new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else {
                        return result;
                    }

                case 6:
                    if (currentChar == '-' || currentChar == '=') {
                        return new Token(Token.Type.OPERATOR, text.substring(position, current), position, current);
                    } else {
                        return result;
                    }

                default:
                    return result;
            }
        }

        return result;
    }
}
