package ua.knu;

import ua.knu.lexer.Lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        var tokens = Lexer.tokenize(getKotlinCode("src/main/resources/kotlin.txt"));
        var prettifiedToString = new StringBuilder();

        tokens.forEach(token -> prettifiedToString.append('\n').append(token));

        System.out.println(prettifiedToString);
    }

    private static String getKotlinCode(String path) {
        var kotlinCode = new StringBuilder();

        try (var reader = new BufferedReader(new FileReader(path))) {
            String row;
            while ((row = reader.readLine()) != null) {
                kotlinCode.append(row).append('\n');
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return kotlinCode.toString();
    }
}
