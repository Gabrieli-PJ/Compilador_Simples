package lexicoSintatico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    public enum TokenType {
        WHILE, VAR, TO, THEN, STRING, REAL, READ, PROGRAM, PROCEDURE, PRINT,
        WRITELN,
        NREAL, NINT, LITERAL, INTEGER, IF, IDENT, FOR, END, ELSE, DO,
        CONST, BEGIN, VSTRING,
        OP_GE, OP_GT, EQUAL, OP_NE, OP_LE, OP_LT,
        PLUS, SEMICOLON, ASSIGN, COLON, DIV,
        DOT, COMMA, MULT, RPAR, LPAR, LBRACE, RBRACE, MINUS, EOF
    }

    public static class Token {
        public final TokenType type;
        public final String lexeme;
        public final int line;
        public final int column;

        public Token(TokenType type, String lexeme, int line, int column) {
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("Token { cod=%d, lexeme='%s', linha=%d, coluna=%d }",
                    getTokencodemap().get(type), lexeme, line, column);
        }
    }

    public static class TokenPattern {
        public final TokenType type;
        public final Pattern pattern;

        public TokenPattern(TokenType type, String regex) {
            this.type = type;
            this.pattern = Pattern.compile("^(" + regex + ")");
        }
    }

    private final List<TokenPattern> tokenPatterns;

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("program", TokenType.PROGRAM);
        keywords.put("const", TokenType.CONST);
        keywords.put("var", TokenType.VAR);
        keywords.put("procedure", TokenType.PROCEDURE);
        keywords.put("begin", TokenType.BEGIN);
        keywords.put("end", TokenType.END);
        keywords.put("print", TokenType.PRINT);
        keywords.put("if", TokenType.IF);
        keywords.put("then", TokenType.THEN);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("to", TokenType.TO);
        keywords.put("do", TokenType.DO);
        keywords.put("while", TokenType.WHILE);
        keywords.put("read", TokenType.READ);
        keywords.put("real", TokenType.REAL);
    }

    static final Map<TokenType, Integer> tokenCodeMap = new HashMap<>();
    static {
        getTokencodemap().put(TokenType.WHILE, 1);
        getTokencodemap().put(TokenType.VAR, 2);
        getTokencodemap().put(TokenType.TO, 3);
        getTokencodemap().put(TokenType.THEN, 4);
        getTokencodemap().put(TokenType.STRING, 5);
        getTokencodemap().put(TokenType.REAL, 6);
        getTokencodemap().put(TokenType.READ, 7);
        getTokencodemap().put(TokenType.PROGRAM, 8);
        getTokencodemap().put(TokenType.PROCEDURE, 9);
        getTokencodemap().put(TokenType.PRINT, 10);
        getTokencodemap().put(TokenType.NREAL, 11);
        getTokencodemap().put(TokenType.NINT, 12);
        getTokencodemap().put(TokenType.LITERAL, 13);
        getTokencodemap().put(TokenType.INTEGER, 14);
        getTokencodemap().put(TokenType.IF, 15);
        getTokencodemap().put(TokenType.IDENT, 16);
        getTokencodemap().put(TokenType.FOR, 17);
        getTokencodemap().put(TokenType.END, 18);
        getTokencodemap().put(TokenType.ELSE, 19);
        getTokencodemap().put(TokenType.DO, 20);
        getTokencodemap().put(TokenType.CONST, 21);
        getTokencodemap().put(TokenType.BEGIN, 22);
        getTokencodemap().put(TokenType.VSTRING, 23);
        getTokencodemap().put(TokenType.OP_GE, 24);
        getTokencodemap().put(TokenType.OP_GT, 25);
        getTokencodemap().put(TokenType.EQUAL, 26);
        getTokencodemap().put(TokenType.OP_NE, 27);
        getTokencodemap().put(TokenType.OP_LE, 28);
        getTokencodemap().put(TokenType.OP_LT, 29);
        getTokencodemap().put(TokenType.PLUS, 30);
        getTokencodemap().put(TokenType.SEMICOLON, 31);
        getTokencodemap().put(TokenType.ASSIGN, 32);
        getTokencodemap().put(TokenType.COLON, 33);
        getTokencodemap().put(TokenType.DIV, 34);
        getTokencodemap().put(TokenType.DOT, 35);
        getTokencodemap().put(TokenType.COMMA, 36);
        getTokencodemap().put(TokenType.MULT, 37);
        getTokencodemap().put(TokenType.RPAR, 38);
        getTokencodemap().put(TokenType.LPAR, 39);
        getTokencodemap().put(TokenType.LBRACE, 40);
        getTokencodemap().put(TokenType.RBRACE, 41);
        getTokencodemap().put(TokenType.MINUS, 42);
    }

    public LexicalAnalyzer() {
        tokenPatterns = new ArrayList<>();
        tokenPatterns.add(new TokenPattern(TokenType.NREAL, "\\b\\d+\\.\\d{2}\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.NINT, "\\b\\d+\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.LITERAL, "'[^']*'"));
        tokenPatterns.add(new TokenPattern(TokenType.VSTRING, "\"[^\"]*\""));
        tokenPatterns.add(new TokenPattern(TokenType.BEGIN, "\\bbegin\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.END, "\\bend\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.PRINT, "\\bprint\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.CONST, "\\bconst\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.VAR, "\\bvar\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.PROCEDURE, "\\bprocedure\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.IF, "\\bif\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.STRING, "\\bstring\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.THEN, "\\bthen\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.ELSE, "\\belse\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.FOR, "\\bfor\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.TO, "\\bto\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.DO, "\\bdo\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.WHILE, "\\bwhile\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.READ, "\\bread\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.INTEGER, "\\binteger\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.INTEGER, "\\breal\\b"));
        tokenPatterns.add(new TokenPattern(TokenType.IDENT, "[a-zA-Z_][a-zA-Z0-9_]*"));
        tokenPatterns.add(new TokenPattern(TokenType.OP_GE, ">="));
        tokenPatterns.add(new TokenPattern(TokenType.OP_LE, "<="));
        tokenPatterns.add(new TokenPattern(TokenType.OP_NE, "<>"));
        tokenPatterns.add(new TokenPattern(TokenType.OP_GT, ">"));
        tokenPatterns.add(new TokenPattern(TokenType.OP_LT, "<"));
        tokenPatterns.add(new TokenPattern(TokenType.ASSIGN, ":="));
        tokenPatterns.add(new TokenPattern(TokenType.COLON, ":"));
        tokenPatterns.add(new TokenPattern(TokenType.EQUAL, "="));
        tokenPatterns.add(new TokenPattern(TokenType.PLUS, "\\+"));
        tokenPatterns.add(new TokenPattern(TokenType.MINUS, "-"));
        tokenPatterns.add(new TokenPattern(TokenType.MULT, "\\*"));
        tokenPatterns.add(new TokenPattern(TokenType.DIV, "/"));
        tokenPatterns.add(new TokenPattern(TokenType.SEMICOLON, ";"));
        tokenPatterns.add(new TokenPattern(TokenType.COMMA, ","));
        tokenPatterns.add(new TokenPattern(TokenType.DOT, "\\."));
        tokenPatterns.add(new TokenPattern(TokenType.LPAR, "\\("));
        tokenPatterns.add(new TokenPattern(TokenType.RPAR, "\\)"));
        tokenPatterns.add(new TokenPattern(TokenType.LBRACE, "\\{"));
        tokenPatterns.add(new TokenPattern(TokenType.RBRACE, "\\}"));
    }

    private String removeComments(String input) {
        String noBlock = input.replaceAll("(?s)/\\*.*?\\*/", "");
        return noBlock.replaceAll("//.*", "");
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = removeComments(input).split("\\n");
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            int pos = 0;
            while (pos < line.length()) {
                if (Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                    continue;
                }
                boolean matched = false;
                for (TokenPattern tp : tokenPatterns) {
                    Matcher m = tp.pattern.matcher(line.substring(pos));
                    if (m.find()) {
                        String lexeme = m.group();
                        if (tp.type == TokenType.IDENT) {
                            if (lexeme.length() > 20)
                                throw new RuntimeException("Identificador muito longo na linha " + (lineIndex + 1) + ", coluna " + (pos + 1));
                            if (keywords.containsKey(lexeme)) {
                                tokens.add(new Token(keywords.get(lexeme), lexeme, lineIndex + 1, pos + 1));
                            } else {
                                tokens.add(new Token(TokenType.IDENT, lexeme, lineIndex + 1, pos + 1));
                            }
                        } else if (tp.type == TokenType.NINT) {
                            long value = Long.parseLong(lexeme);
                            if (value > 1_000_000_000L)
                                throw new RuntimeException("Inteiro fora do limite na linha " + (lineIndex + 1));
                            tokens.add(new Token(TokenType.NINT, lexeme, lineIndex + 1, pos + 1));
                        } else if (tp.type == TokenType.NREAL) {
                            double value = Double.parseDouble(lexeme);
                            if (value > 1_000_000_000)
                                throw new RuntimeException("Real fora do limite na linha " + (lineIndex + 1));
                            tokens.add(new Token(TokenType.NREAL, lexeme, lineIndex + 1, pos + 1));
                        } else {
                            tokens.add(new Token(tp.type, lexeme, lineIndex + 1, pos + 1));
                        }
                        pos += m.end();
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    throw new RuntimeException("Token inválido na linha " + (lineIndex + 1) + ", coluna " + (pos + 1));
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, "$", lines.length + 1, 1));
        return tokens;
    }

    public static Map<TokenType, Integer> getTokencodemap() {
        return tokenCodeMap;
    }
}