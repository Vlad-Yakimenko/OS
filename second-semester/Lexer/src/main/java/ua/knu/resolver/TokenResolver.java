package ua.knu.resolver;

import ua.knu.token.Token;

public interface TokenResolver {

    Token resolve(String text, int position);
}
