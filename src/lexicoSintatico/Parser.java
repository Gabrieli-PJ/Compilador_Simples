package lexicoSintatico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import lexicoSintatico.LexicalAnalyzer.TokenType;


public class Parser {

    enum SymbolType { TERMINAL, NON_TERMINAL }

    static class Symbol {
        SymbolType type;
        String value;
        String acaoSemantica;

        Symbol(SymbolType type, String value) {
            this.type = type;
            this.value = value;
            this.acaoSemantica = null;
        }

        Symbol(SymbolType type, String value, String acaoSemantica) {
            this.type = type;
            this.value = value;
            this.acaoSemantica = acaoSemantica;
        }

        @Override
        public String toString() {
            return value;
        }
    }


    private final List<LexicalAnalyzer.Token> tokens;
    private int currentTokenIndex;
    private LexicalAnalyzer.Token currentToken;
    private final Stack<Symbol> stack = new Stack<>();
    private final List<Simbolo> tabelaSimbolos = new ArrayList<>();
    private List<String> nomesTemp = new ArrayList<>();
    private String tipoTemp = null;


    private final Map<String, Map<LexicalAnalyzer.TokenType, List<Symbol>>> parsingTable = new HashMap<>();

    public Parser(List<LexicalAnalyzer.Token> tokens) {
        this.tokens = tokens;
        this.tokens.add(new LexicalAnalyzer.Token(LexicalAnalyzer.TokenType.DOT, "$", -1, -1));
        this.currentTokenIndex = 0;
        this.currentToken = tokens.get(0);
        initParsingTable();
    }

    private Simbolo buscarSimbolo(String nome) {
        for (Simbolo s : tabelaSimbolos) {
            if (s.getNome().equals(nome)) {
                return s;
            }
        }
        return null;
    }

    private void reportarErro(String mensagem, int linha) {
        throw new RuntimeException("Erro semântico na linha " + linha + ": " + mensagem);
    }

    private void mostrarTabela() {
        System.out.println("\n===== Tabela de Símbolos Atual =====");
        for (Simbolo s : tabelaSimbolos) {
            System.out.println(s);
        }
        System.out.println("====================================\n");
    }

    private void inserirVariavel(String nome, String tipo, int linha) {
        if (buscarSimbolo(nome) != null) {
            reportarErro("Variável '" + nome + "' já declarada.", linha);
        } else {
            Simbolo s = new Simbolo(nome, tipo, "variavel", null, linha, null);
            tabelaSimbolos.add(s);
            mostrarTabela();
        }
    }

    private void inserirConstante(String nome, String tipo, Object valor, int linha) {
        if (buscarSimbolo(nome) != null) {
            reportarErro("Constante '" + nome + "' já declarada.", linha);
        } else {
            Simbolo s = new Simbolo(nome, tipo, "constante", valor, linha, null);
            tabelaSimbolos.add(s);
            mostrarTabela();
        }
    }

    private void inserirProcedure(String nome, List<String> tiposParams, int linha) {
        if (buscarSimbolo(nome) != null) {
            reportarErro("Procedure '" + nome + "' já declarada.", linha); // Se já existe
        } else {
            Simbolo s = new Simbolo(nome, "procedure", "procedure", null, linha, tiposParams);
            tabelaSimbolos.add(s);
            mostrarTabela();
        }
    }


    private void verificarUsoIdent(String nome, int linha) {
        if (buscarSimbolo(nome) == null) {
            reportarErro("Identificador '" + nome + "' não declarado.", linha);
        }
    }

    private void verificarTipoAtribuicao(String nomeVar, String tipoExpr, int linha) {
        Simbolo s = buscarSimbolo(nomeVar);
        if (s != null) {
            System.out.println("Verificando tipo: variável '" + nomeVar + "' é do tipo " + s.getTipo() + " e expressão é " + tipoExpr);
        }
        if (s != null && !s.getTipo().equals(tipoExpr)) {
            reportarErro("Incompatibilidade de tipos: variável '" + nomeVar + "' é " + s.getTipo() + ", mas expressão é " + tipoExpr + ".", linha);
        }
    }



    private void executarAcaoSemantica(String acao, LexicalAnalyzer.Token token, String tipoExtra, Object valorExtra, List<String> params) {
        switch (acao) {
            case "inserirVariavel":
                inserirVariavel(token.lexeme, tipoExtra, token.line);
                break;
            case "inserirConstante":
                inserirConstante(token.lexeme, tipoExtra, valorExtra, token.line);
                break;
            case "inserirProcedure":
                inserirProcedure(token.lexeme, params, token.line);
                break;
            case "verificarUso":
                verificarUsoIdent(token.lexeme, token.line);
                break;
            case "verificarTipoAtribuicao":
                verificarTipoAtribuicao(token.lexeme, tipoExtra, token.line);
                break;
            default:
                System.out.println("⚠️ Ação semântica não reconhecida: " + acao);
                break;
        }
    }

    public void parse() {
        stack.push(new Symbol(SymbolType.TERMINAL, "$"));
        stack.push(new Symbol(SymbolType.NON_TERMINAL, "PROGRAMA"));

        nomesTemp = new ArrayList<>();
        tipoTemp = null;
        String nomeProcTemp = null;
        List<String> tiposParams = new ArrayList<>();
        String lastTerminal = null;

        while (!stack.isEmpty()) {
            printState();

            Symbol top = stack.peek();

            if (top.type == SymbolType.TERMINAL) {
                if (match(top.value)) {
                    if (top.value.equals("IDENT")) {
                        if ("PROCEDURE".equals(lastTerminal)) {
                            nomeProcTemp = currentToken.lexeme;
                        } else {
                            nomesTemp.add(currentToken.lexeme);
                        }
                    }

                    if (top.value.equals("INTEGER") || top.value.equals("REAL") || top.value.equals("STRING")) {
                        tipoTemp = currentToken.lexeme;
                    }

                    if (top.acaoSemantica != null) {
                        System.out.println("Executando ação semântica: " + top.acaoSemantica);
                        executarAcaoSemantica(top.acaoSemantica, currentToken, tipoTemp, nomesTemp, tiposParams);
                    }

                    lastTerminal = top.value;
                    stack.pop();
                    advance();
                } else {
                    error("Token inesperado: esperado '" + top.value + "'");
                }
            } else {
                List<Symbol> production = getProduction(top.value, currentToken.type);
                if (production != null) {
                    stack.pop();
                    pushProduction(production);
                    if (top.value.equals("LDVAR")) {
                        for (String nome : nomesTemp) {
                            inserirVariavel(nome, tipoTemp, currentToken.line);
                        }
                        nomesTemp.clear();
                        tipoTemp = null;
                    }
                    if (top.value.equals("VARIAVEIS")) {
                        nomesTemp.clear();
                        tipoTemp = null;
                    }
                    if (top.value.equals("CONSTANTES")) {
                        nomesTemp.clear();
                    }
                    if (top.value.equals("PROCEDIMENTOS") && nomeProcTemp != null) {
                        inserirProcedure(nomeProcTemp, tiposParams, currentToken.line);
                        nomeProcTemp = null;
                        tiposParams.clear();
                    }
                    if (top.value.equals("ASSIGN")) {
                        System.out.println("Executando ação semântica: verificarTipoAtribuicao");
                        executarAcaoSemantica("verificarTipoAtribuicao", currentToken, tipoTemp, nomesTemp, tiposParams);
                    }
                } else {
                    error("Produção não encontrada para '" + top.value + "' com token '" + currentToken.lexeme + "'");
                }
            }
        }

        System.out.println("\nAnálise sintática concluída com sucesso.");
    }






    private void pushProduction(List<Symbol> production) {
        ListIterator<Symbol> it = production.listIterator(production.size());
        while (it.hasPrevious()) {
            Symbol sym = it.previous();
            if (!sym.value.equals("ε")) {
                stack.push(sym);
            }
        }
    }

    private boolean match(String terminalName) {
        if (terminalName.equals("$")) {
            return currentToken.type == TokenType.EOF;
        }
        return currentToken.type.name().equalsIgnoreCase(terminalName);
    }

    private void advance() {
        if (currentTokenIndex < tokens.size() - 1) {
            currentTokenIndex++;
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    private void printState() {
        System.out.println("Token: { cod=" + LexicalAnalyzer.getTokencodemap().get(currentToken.type) +
                ", lexeme='" + currentToken.lexeme + "', linha=" + currentToken.line + " }");
        System.out.println("Pilha: " + stack);
    }

    private void error(String message) {
        throw new RuntimeException("Erro sintático na linha " + currentToken.line + ": " + message);
    }

    private List<Symbol> getProduction(String nonTerminal, LexicalAnalyzer.TokenType lookahead) {
        Map<LexicalAnalyzer.TokenType, List<Symbol>> row = parsingTable.get(nonTerminal);
        if (row != null) {
            System.out.println("DEBUG → Buscando produção para " + nonTerminal + " com lookahead " + lookahead);
            return row.get(lookahead);
        }
        return null;
    }
    private void initParsingTable() {
        parsingTable.put("PROGRAMA", new HashMap<>());
        parsingTable.get("PROGRAMA").put(LexicalAnalyzer.TokenType.PROGRAM, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "PROGRAM"),
            new Symbol(SymbolType.TERMINAL, "IDENT"),
            new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
            new Symbol(SymbolType.NON_TERMINAL, "DECLARACOES"),
            new Symbol(SymbolType.NON_TERMINAL, "BLOCO"),
            new Symbol(SymbolType.TERMINAL, "DOT")
        ));
        parsingTable.put("BLOCO", new HashMap<>());
        parsingTable.get("BLOCO").put(LexicalAnalyzer.TokenType.BEGIN, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "BEGIN"),
            new Symbol(SymbolType.NON_TERMINAL, "COMANDOS"),
            new Symbol(SymbolType.TERMINAL, "END")
        ));


        parsingTable.put("DECLARACOES", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.CONST, LexicalAnalyzer.TokenType.VAR,
            LexicalAnalyzer.TokenType.PROCEDURE, LexicalAnalyzer.TokenType.BEGIN)) {
            parsingTable.get("DECLARACOES").put(t, Arrays.asList(
                new Symbol(SymbolType.NON_TERMINAL, "CONSTANTES"),
                new Symbol(SymbolType.NON_TERMINAL, "VARIAVEIS"),
                new Symbol(SymbolType.NON_TERMINAL, "PROCEDIMENTOS")
            ));
        }

        parsingTable.put("CONSTANTES", new HashMap<>());
        parsingTable.get("CONSTANTES").put(LexicalAnalyzer.TokenType.CONST, Arrays.asList(
        	    new Symbol(SymbolType.TERMINAL, "CONST"),
        	    new Symbol(SymbolType.TERMINAL, "IDENT", "inserirConstante"),
        	    new Symbol(SymbolType.TERMINAL, "EQUAL"),
        	    new Symbol(SymbolType.NON_TERMINAL, "VALORCONST"),
        	    new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
        	    new Symbol(SymbolType.NON_TERMINAL, "CONSTANTES")
        	));
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.VAR, LexicalAnalyzer.TokenType.PROCEDURE, LexicalAnalyzer.TokenType.BEGIN)) {
            parsingTable.get("CONSTANTES").put(t, Collections.singletonList(
                new Symbol(SymbolType.TERMINAL, "ε")
            ));
        }
        
        parsingTable.put("VALORCONST", new HashMap<>());
        parsingTable.get("VALORCONST").put(LexicalAnalyzer.TokenType.NINT,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "NINT")));
        parsingTable.get("VALORCONST").put(LexicalAnalyzer.TokenType.NREAL,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "NREAL")));
        parsingTable.get("VALORCONST").put(LexicalAnalyzer.TokenType.VSTRING,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "VSTRING")));


        parsingTable.put("VARIAVEIS", new HashMap<>());
        parsingTable.get("VARIAVEIS").put(LexicalAnalyzer.TokenType.VAR, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "VAR"),
            new Symbol(SymbolType.NON_TERMINAL, "LISTAVARIAVEIS", "marcarVariavel"),
            new Symbol(SymbolType.TERMINAL, "COLON"),
            new Symbol(SymbolType.NON_TERMINAL, "TIPO"),
            new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
            new Symbol(SymbolType.NON_TERMINAL, "LDVAR")
        ));
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.PROCEDURE, LexicalAnalyzer.TokenType.BEGIN)) {
            parsingTable.get("VARIAVEIS").put(t, Collections.singletonList(
                new Symbol(SymbolType.TERMINAL, "ε")
            ));
        }
        
        parsingTable.put("LDVAR", new HashMap<>());
        parsingTable.get("LDVAR").put(LexicalAnalyzer.TokenType.IDENT, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "LISTAVARIAVEIS"),
            new Symbol(SymbolType.TERMINAL, "COLON"),
            new Symbol(SymbolType.NON_TERMINAL, "TIPO"),
            new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
            new Symbol(SymbolType.NON_TERMINAL, "LDVAR")
        ));
        for (LexicalAnalyzer.TokenType t : Arrays.asList(LexicalAnalyzer.TokenType.PROCEDURE, LexicalAnalyzer.TokenType.BEGIN)) {
            parsingTable.get("LDVAR").put(t, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        }
        parsingTable.put("PROCEDIMENTOS", new HashMap<>());
        parsingTable.get("PROCEDIMENTOS").put(LexicalAnalyzer.TokenType.PROCEDURE, Arrays.asList(
        	    new Symbol(SymbolType.TERMINAL, "PROCEDURE"),
        	    new Symbol(SymbolType.TERMINAL, "IDENT"),
        	    new Symbol(SymbolType.NON_TERMINAL, "PARAMETROS"),
        	    new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
        	    new Symbol(SymbolType.NON_TERMINAL, "BLOCO"),
        	    new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
        	    new Symbol(SymbolType.NON_TERMINAL, "PROCEDIMENTOS")
        	    
        	));
        

        parsingTable.get("PROCEDIMENTOS").put(LexicalAnalyzer.TokenType.BEGIN, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));


        parsingTable.put("TIPO", new HashMap<>());
        parsingTable.get("TIPO").put(LexicalAnalyzer.TokenType.INTEGER,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "INTEGER")));
        parsingTable.get("TIPO").put(LexicalAnalyzer.TokenType.REAL,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "REAL")));
        parsingTable.get("TIPO").put(LexicalAnalyzer.TokenType.STRING,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "STRING")));

        parsingTable.put("LISTAVARIAVEIS", new HashMap<>());
        parsingTable.get("LISTAVARIAVEIS").put(LexicalAnalyzer.TokenType.IDENT, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "IDENT"),
            new Symbol(SymbolType.NON_TERMINAL, "REPIDENT")
        ));

        parsingTable.put("REPIDENT", new HashMap<>());
        parsingTable.get("REPIDENT").put(LexicalAnalyzer.TokenType.COMMA, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "COMMA"),
            new Symbol(SymbolType.TERMINAL, "IDENT"),
            new Symbol(SymbolType.NON_TERMINAL, "REPIDENT")
        ));
        for (LexicalAnalyzer.TokenType t : Arrays.asList(LexicalAnalyzer.TokenType.COLON)) {
            parsingTable.get("REPIDENT").put(t, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        }
        parsingTable.put("PARAMETROS", new HashMap<>());
        parsingTable.get("PARAMETROS").put(LexicalAnalyzer.TokenType.LPAR, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "LPAR"),
            new Symbol(SymbolType.NON_TERMINAL, "LISTAVARIAVEIS"),
            new Symbol(SymbolType.TERMINAL, "COLON"),
            new Symbol(SymbolType.NON_TERMINAL, "TIPO"),
            new Symbol(SymbolType.NON_TERMINAL, "REPPARAMETROS"),
            new Symbol(SymbolType.TERMINAL, "RPAR")
        ));
        parsingTable.get("PARAMETROS").put(LexicalAnalyzer.TokenType.SEMICOLON, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));

        parsingTable.put("REPPARAMETROS", new HashMap<>());
        parsingTable.get("REPPARAMETROS").put(LexicalAnalyzer.TokenType.COMMA, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "COMMA"),
            new Symbol(SymbolType.NON_TERMINAL, "LISTAVARIAVEIS"),
            new Symbol(SymbolType.TERMINAL, "COLON"),
            new Symbol(SymbolType.NON_TERMINAL, "TIPO"),
            new Symbol(SymbolType.NON_TERMINAL, "REPPARAMETROS")
        ));
        parsingTable.get("REPPARAMETROS").put(LexicalAnalyzer.TokenType.RPAR, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));


        parsingTable.put("EXPRESSION", new HashMap<>());
        parsingTable.get("EXPRESSION").put(LexicalAnalyzer.TokenType.IDENT, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        parsingTable.get("EXPRESSION").put(LexicalAnalyzer.TokenType.NINT, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        parsingTable.get("EXPRESSION").put(LexicalAnalyzer.TokenType.NREAL, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        parsingTable.get("EXPRESSION").put(LexicalAnalyzer.TokenType.LITERAL, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        parsingTable.get("EXPRESSION").put(LexicalAnalyzer.TokenType.VSTRING, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        parsingTable.get("EXPRESSION").put(LexicalAnalyzer.TokenType.LPAR, Arrays.asList(
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));

        parsingTable.put("TERM", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.IDENT, LexicalAnalyzer.TokenType.NINT, LexicalAnalyzer.TokenType.NREAL,
            LexicalAnalyzer.TokenType.LITERAL, LexicalAnalyzer.TokenType.VSTRING, LexicalAnalyzer.TokenType.LPAR)) {
            parsingTable.get("TERM").put(t, Arrays.asList(
                new Symbol(SymbolType.NON_TERMINAL, "FACTOR"),
                new Symbol(SymbolType.NON_TERMINAL, "TER")
            ));
        }

        parsingTable.put("FACTOR", new HashMap<>());
        parsingTable.get("FACTOR").put(LexicalAnalyzer.TokenType.IDENT, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "IDENT")));
        parsingTable.get("FACTOR").put(LexicalAnalyzer.TokenType.NINT, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "NINT")));
        parsingTable.get("FACTOR").put(LexicalAnalyzer.TokenType.NREAL, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "NREAL")));
        parsingTable.get("FACTOR").put(LexicalAnalyzer.TokenType.LITERAL, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "LITERAL")));
        parsingTable.get("FACTOR").put(LexicalAnalyzer.TokenType.VSTRING, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "VSTRING")));
        parsingTable.get("FACTOR").put(LexicalAnalyzer.TokenType.LPAR, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "LPAR"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION"),
            new Symbol(SymbolType.TERMINAL, "RPAR")
        ));

        parsingTable.put("EXPR", new HashMap<>());
        parsingTable.get("EXPR").put(LexicalAnalyzer.TokenType.RBRACE,
        	    Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));

        parsingTable.get("EXPR").put(LexicalAnalyzer.TokenType.PLUS, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "PLUS"),
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        parsingTable.get("EXPR").put(LexicalAnalyzer.TokenType.MINUS, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "MINUS"),
            new Symbol(SymbolType.NON_TERMINAL, "TERM"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPR")
        ));
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.RPAR, LexicalAnalyzer.TokenType.SEMICOLON, LexicalAnalyzer.TokenType.COMMA,
            LexicalAnalyzer.TokenType.EQUAL, LexicalAnalyzer.TokenType.OP_GE, LexicalAnalyzer.TokenType.OP_GT,
            LexicalAnalyzer.TokenType.OP_LE, LexicalAnalyzer.TokenType.OP_LT, LexicalAnalyzer.TokenType.OP_NE,
            LexicalAnalyzer.TokenType.THEN, LexicalAnalyzer.TokenType.DO)) {
            parsingTable.get("EXPR").put(t, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        }
        parsingTable.put("COMANDOS", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.IDENT, LexicalAnalyzer.TokenType.IF, LexicalAnalyzer.TokenType.WHILE,
            LexicalAnalyzer.TokenType.FOR, LexicalAnalyzer.TokenType.PRINT, LexicalAnalyzer.TokenType.READ,
            LexicalAnalyzer.TokenType.BEGIN)) {
            parsingTable.get("COMANDOS").put(t, Arrays.asList(
                new Symbol(SymbolType.NON_TERMINAL, "COMANDO"),
                new Symbol(SymbolType.TERMINAL, "SEMICOLON"),
                new Symbol(SymbolType.NON_TERMINAL, "COMANDOS")
            ));
        }
        parsingTable.get("COMANDOS").put(LexicalAnalyzer.TokenType.END,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        parsingTable.put("COMANDO", new HashMap<>());

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.PRINT, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "PRINT"),
            new Symbol(SymbolType.TERMINAL, "LBRACE"),
            new Symbol(SymbolType.NON_TERMINAL, "ITEMSAIDA"),
            new Symbol(SymbolType.NON_TERMINAL, "REPITEM"),
            new Symbol(SymbolType.TERMINAL, "RBRACE")
        ));

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.IF, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "IF"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPRELACIONAL"),
            new Symbol(SymbolType.TERMINAL, "THEN"),
            new Symbol(SymbolType.NON_TERMINAL, "BLOCO"),
            new Symbol(SymbolType.NON_TERMINAL, "ELSEOPC")
        ));

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.WHILE, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "WHILE"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPRELACIONAL"),
            new Symbol(SymbolType.TERMINAL, "DO"),
            new Symbol(SymbolType.NON_TERMINAL, "BLOCO")
        ));

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.FOR, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "FOR"),
            new Symbol(SymbolType.TERMINAL, "IDENT", "verificarUso"),
            new Symbol(SymbolType.TERMINAL, "ASSIGN"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION"),
            new Symbol(SymbolType.TERMINAL, "TO"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION"),
            new Symbol(SymbolType.TERMINAL, "DO"),
            new Symbol(SymbolType.NON_TERMINAL, "BLOCO")
        ));

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.READ, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "READ"),
            new Symbol(SymbolType.TERMINAL, "LPAR"),
            new Symbol(SymbolType.TERMINAL, "IDENT"),
            new Symbol(SymbolType.TERMINAL, "RPAR")
        ));

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.IDENT, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "IDENT", "verificarUso"),
            new Symbol(SymbolType.NON_TERMINAL, "CHAMADAPROC")
        ));

        parsingTable.get("COMANDO").put(LexicalAnalyzer.TokenType.BEGIN,
            Collections.singletonList(new Symbol(SymbolType.NON_TERMINAL, "BLOCO")));
        parsingTable.put("ITEMSAIDA", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.IDENT, LexicalAnalyzer.TokenType.NINT, LexicalAnalyzer.TokenType.NREAL,
            LexicalAnalyzer.TokenType.LITERAL, LexicalAnalyzer.TokenType.VSTRING, LexicalAnalyzer.TokenType.LPAR)) {
            parsingTable.get("ITEMSAIDA").put(t, Collections.singletonList(
                new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION")
            ));
        }

        parsingTable.put("REPITEM", new HashMap<>());
        parsingTable.get("REPITEM").put(LexicalAnalyzer.TokenType.COMMA, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "COMMA"),
            new Symbol(SymbolType.NON_TERMINAL, "ITEMSAIDA"),
            new Symbol(SymbolType.NON_TERMINAL, "REPITEM")
        ));
        parsingTable.get("REPITEM").put(LexicalAnalyzer.TokenType.RBRACE,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        parsingTable.put("EXPRELACIONAL", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.IDENT, LexicalAnalyzer.TokenType.NINT, LexicalAnalyzer.TokenType.NREAL,
            LexicalAnalyzer.TokenType.LITERAL, LexicalAnalyzer.TokenType.VSTRING, LexicalAnalyzer.TokenType.LPAR)) {
            parsingTable.get("EXPRELACIONAL").put(t, Arrays.asList(
                new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION"),
                new Symbol(SymbolType.NON_TERMINAL, "OPREL"),
                new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION")
            ));
        }

        parsingTable.put("OPREL", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.EQUAL, LexicalAnalyzer.TokenType.OP_NE, LexicalAnalyzer.TokenType.OP_LT,
            LexicalAnalyzer.TokenType.OP_GT, LexicalAnalyzer.TokenType.OP_LE, LexicalAnalyzer.TokenType.OP_GE)) {
            parsingTable.get("OPREL").put(t, Collections.singletonList(new Symbol(SymbolType.TERMINAL, t.name())));
        }
        parsingTable.put("ELSEOPC", new HashMap<>());
        parsingTable.get("ELSEOPC").put(LexicalAnalyzer.TokenType.ELSE, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "ELSE"),
            new Symbol(SymbolType.NON_TERMINAL, "BLOCO")
        ));
        parsingTable.get("ELSEOPC").put(LexicalAnalyzer.TokenType.SEMICOLON,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        
        parsingTable.put("CHAMADAPROC", new HashMap<>());
        parsingTable.get("CHAMADAPROC").put(LexicalAnalyzer.TokenType.LPAR, Collections.singletonList(
            new Symbol(SymbolType.NON_TERMINAL, "LISTAPARAMETROS")
        ));
        parsingTable.get("CHAMADAPROC").put(LexicalAnalyzer.TokenType.ASSIGN, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "ASSIGN"),
            new Symbol(SymbolType.NON_TERMINAL, "EXPRESSION", "verificarTipoAtribuicao")
        ));

        parsingTable.put("LISTAPARAMETROS", new HashMap<>());
        parsingTable.get("LISTAPARAMETROS").put(LexicalAnalyzer.TokenType.LPAR, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "LPAR"),
            new Symbol(SymbolType.NON_TERMINAL, "PAR"),
            new Symbol(SymbolType.NON_TERMINAL, "REPPAR"),
            new Symbol(SymbolType.TERMINAL, "RPAR")
        ));

        parsingTable.put("PAR", new HashMap<>());
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.IDENT, LexicalAnalyzer.TokenType.NINT, LexicalAnalyzer.TokenType.NREAL,
            LexicalAnalyzer.TokenType.LITERAL, LexicalAnalyzer.TokenType.VSTRING)) {
            parsingTable.get("PAR").put(t, Collections.singletonList(new Symbol(SymbolType.TERMINAL, t.name())));
        }

        parsingTable.put("REPPAR", new HashMap<>());
        parsingTable.get("REPPAR").put(LexicalAnalyzer.TokenType.COMMA, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "COMMA"),
            new Symbol(SymbolType.NON_TERMINAL, "PAR"),
            new Symbol(SymbolType.NON_TERMINAL, "REPPAR")
        ));
        parsingTable.get("REPPAR").put(LexicalAnalyzer.TokenType.RPAR,
            Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));


        parsingTable.put("TER", new HashMap<>());
        parsingTable.get("TER").put(LexicalAnalyzer.TokenType.RBRACE,
        	    Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        parsingTable.get("TER").put(LexicalAnalyzer.TokenType.MULT, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "MULT"),
            new Symbol(SymbolType.NON_TERMINAL, "FACTOR"),
            new Symbol(SymbolType.NON_TERMINAL, "TER")
        ));
        parsingTable.get("TER").put(LexicalAnalyzer.TokenType.DIV, Arrays.asList(
            new Symbol(SymbolType.TERMINAL, "DIV"),
            new Symbol(SymbolType.NON_TERMINAL, "FACTOR"),
            new Symbol(SymbolType.NON_TERMINAL, "TER")
        ));
        for (LexicalAnalyzer.TokenType t : Arrays.asList(
            LexicalAnalyzer.TokenType.PLUS, LexicalAnalyzer.TokenType.MINUS, LexicalAnalyzer.TokenType.RPAR,
            LexicalAnalyzer.TokenType.SEMICOLON, LexicalAnalyzer.TokenType.COMMA,
            LexicalAnalyzer.TokenType.EQUAL, LexicalAnalyzer.TokenType.OP_GE, LexicalAnalyzer.TokenType.OP_GT,
            LexicalAnalyzer.TokenType.OP_LE, LexicalAnalyzer.TokenType.OP_LT, LexicalAnalyzer.TokenType.OP_NE,
            LexicalAnalyzer.TokenType.THEN, LexicalAnalyzer.TokenType.DO)) {
            parsingTable.get("TER").put(t, Collections.singletonList(new Symbol(SymbolType.TERMINAL, "ε")));
        }
    }
}
