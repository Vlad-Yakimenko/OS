package ua.knu.util;

import ua.knu.resolver.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class!");
    }

    public static final List<TokenResolver> RESOLVERS = List.of(
            new NumberResolver(),
            new BinAndHexNumberResolver(),
            new OperatorResolver(),
            new SpecialSymbolResolver(),
            new PunctuationSymbolResolver(),
            new IdentifierResolver(),
            new WhitespaceResolver(),
            new StringResolver(),
            new SingleLineCommentResolver(),
            new MultiLineCommentResolver()
    );

    public static final Set<String> MODIFIER_KEYWORDS = new HashSet<>(Arrays.asList(
            "external", "final", "lateinit", "noinline", "open", "annotation", "companion", "const", "reified", "operator",
            "actual", "abstract", "crossinline", "data", "enum", "private", "protected", "public", "expect",
            "out", "override", "infix", "inline", "inner", "internal", "sealed", "suspend", "tailrec", "vararg"
    ));

    public static final Set<String> HARD_KEYWORDS = new HashSet<>(Arrays.asList(
            "null", "object", "val", "var", "when", "while", "this", "throw", "true", "try", "typealias", "typeof",
            "as", "break", "class", "continue", "do", "else", "false", "package", "return", "super", "for", "fun", "if", "in", "interface", "is"
    ));

    public static final Set<String> SOFT_KEYWORDS = new HashSet<>(Arrays.asList(
            "by", "file", "finally", "get", "import", "init", "param", "property", "receiver",
            "it", "catch", "constructor", "delegate", "dynamic", "field", "set", "setparam", "where"
    ));
}
