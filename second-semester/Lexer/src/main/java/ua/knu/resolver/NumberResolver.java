package ua.knu.resolver;

import ua.knu.token.Token;

public class NumberResolver implements TokenResolver {

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
                    if (currentChar == '+' || currentChar == '-') {
                        state = 1;
                    } else if (currentChar == '0') {
                        state = 2;
                        result = new Token(Token.Type.INT, text.substring(position, current), position, current);
                    } else if (Character.isDigit(currentChar)) {
                        state = 4;
                        result = new Token(Token.Type.INT, text.substring(position, current), position, current);
                    } else if (currentChar == '.') {
                        state = 6;
                    } else {
                        return null;
                    }
                    continue;

                case 1:
                    if (currentChar == '0') {
                        state = 2;
                        result = new Token(Token.Type.INT, text.substring(position, current), position, current);
                    } else if (Character.isDigit(currentChar)) {
                        state = 4;
                        result = new Token(Token.Type.INT, text.substring(position, current), position, current);
                    } else if (currentChar == '.') {
                        state = 6;
                    } else {
                        return null;
                    }
                    continue;

                case 2:
                    if (Character.isDigit(currentChar) || currentChar == '_') {
                        state = 3;
                    } else if (currentChar == 'L') {
                        return new Token(Token.Type.LONG, text.substring(position, current), position, current);
                    } else if (currentChar == '.') {
                        state = 6;
                    } else if (currentChar == 'F' || currentChar == 'f') {
                        return new Token(Token.Type.FLOAT, text.substring(position, current), position, current);
                    } else {
                        return result;
                    }
                    continue;

                case 3:
                    if (currentChar == 'F' || currentChar == 'f') {
                        return new Token(Token.Type.FLOAT, text.substring(position, current), position, current);
                    } else if (Character.isDigit(currentChar) || currentChar == '_') {
                        continue;
                    } else {
                        return result;
                    }

                case 4:
                    if (Character.isDigit(currentChar) || currentChar == '_') {
                        result = new Token(Token.Type.INT, text.substring(position, current), position, current);
                        continue;
                    } else if (currentChar == 'L') {
                        return new Token(Token.Type.LONG, text.substring(position, current), position, current);
                    } else if (currentChar == 'F' || currentChar == 'f') {
                        return new Token(Token.Type.FLOAT, text.substring(position, current), position, current);
                    } else if (currentChar == '.') {
                        state = 6;
                        continue;
                    } else {
                        return result;
                    }

                case 6:
                    if (Character.isDigit(currentChar)) {
                        result = new Token(Token.Type.DOUBLE, text.substring(position, current), position, current);
                        state = 7;
                        continue;
                    } else {
                        return result;
                    }

                case 7:
                    if (Character.isDigit(currentChar) || currentChar == '_') {
                        result = new Token(Token.Type.DOUBLE, text.substring(position, current), position, current);
                    } else if (currentChar == 'F' || currentChar == 'f') {
                        return new Token(Token.Type.FLOAT, text.substring(position, current), position, current);
                    } else {
                        return result;
                    }
            }
        }

        return result;
    }
}
