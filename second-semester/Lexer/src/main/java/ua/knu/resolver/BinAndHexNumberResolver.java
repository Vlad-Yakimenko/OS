package ua.knu.resolver;

import ua.knu.token.Token;

import java.util.regex.Pattern;

public class BinAndHexNumberResolver implements TokenResolver {

    private static final int MIN_LENGTH = 3;

    private static final Pattern isBinCandidate = Pattern.compile("[-+]?0[bB].?");
    private static final Pattern isHexCandidate = Pattern.compile("[-+]?0[xX].?");
    private static final Pattern isBinNumber = Pattern.compile("[-+]?0[bB][01]+([eE]([-+]?[0-9]+)?)?");
    private static final Pattern isHexNumber = Pattern.compile("[-+]?0[xX][0-9a-fA-F]+([eE]([-+]?[0-9]+)?)?");

    @Override
    public Token resolve(String text, int position) {
        var current = position;
        var end = position;
        Token.Type type;

        if (text.length() - position < MIN_LENGTH) return null;

        if (isHexCandidate.matcher(text.substring(position, position + MIN_LENGTH)).matches()) {
            type = Token.Type.HEX;
        } else if (isBinCandidate.matcher(text.substring(position, position + MIN_LENGTH)).matches()) {
            type = Token.Type.BIN;
        } else {
            return null;
        }

        while (current < text.length()) {
            current++;

            if (type == Token.Type.BIN && isBinNumber.matcher(text.substring(position, current)).matches()) {
                end = current;
            } else if (type == Token.Type.HEX && isHexNumber.matcher(text.substring(position, current)).matches()) {
                end = current;
            }
        }

        return end != position ? new Token(type, text.substring(position, end), position, end) : null;
    }
}
