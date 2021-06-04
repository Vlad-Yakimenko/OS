package ua.knu.resolver;

import ua.knu.token.Token;

public class PunctuationSymbolResolver implements TokenResolver {

    @Override
    public Token resolve(String text, int position) {
        char currentChar;
        currentChar = text.charAt(position);

        if ("{}[]()".indexOf(currentChar) != -1) {
            return new Token(Token.Type.BRACKET, Character.toString(currentChar), position, position + 1);
        } else if (",;".indexOf(currentChar) != -1) {
            return new Token(Token.Type.SEPARATOR, Character.toString(currentChar), position, position + 1);
        } else if (currentChar == '.') {
            return new Token(Token.Type.DOT, Character.toString(currentChar), position, position + 1);
        }

        return null;
    }
}
