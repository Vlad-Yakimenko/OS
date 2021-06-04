package ua.knu.lexer;

import ua.knu.resolver.TokenResolver;
import ua.knu.util.Constants;
import ua.knu.token.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private Lexer() {
        throw new IllegalStateException("Utility class!");
    }

    public static List<Token> tokenize(String text) {
        var tokens = new ArrayList<Token>();
        var position = 0;
        Token candidate;
        Token error = null;

        while (position < text.length()) {
            candidate = null;

            for (TokenResolver matcher : Constants.RESOLVERS) {
                var temp = matcher.resolve(text, position);

                if (candidate == null || (temp != null && temp.getEnd() > candidate.getEnd())) {
                    candidate = temp;
                }
            }

            if (candidate != null) {
                if (error != null) {
                    tokens.add(error);
                    error = null;
                }

                tokens.add(candidate);
                position = candidate.getEnd();
                continue;
            }

            if (error == null) {
                error = new Token(Token.Type.ERROR, Character.toString(text.charAt(position)), position, position + 1);
            } else {
                error.append(text.charAt(position));
            }

            position++;
        }

        return tokens;
    }
}
