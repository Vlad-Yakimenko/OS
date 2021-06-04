package ua.knu.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {

    private Type type;
    private String value;
    private int start;
    private int end;

    public Token(String value, int start, int end) {
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public void append(char ch) {
        value += ch;
        end++;
    }

    public enum Type {
        INT,
        FLOAT,
        DOUBLE,
        LONG,
        HEX,
        BIN,
        CHAR,
        STRING,
        IDENTIFIER,
        MODIFIER_KEYWORD,
        SOFT_KEYWORD,
        HARD_KEYWORD,
        OPERATOR,
        SEPARATOR,
        DOT,
        BRACKET,
        COMMENT,
        MULTILINE_COMMENT,
        WHITESPACE,
        SPECIAL_SYMBOL,
        ERROR
    }
}
