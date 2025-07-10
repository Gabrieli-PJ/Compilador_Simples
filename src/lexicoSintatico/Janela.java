package lexicoSintatico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Janela extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    
	FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivos Word e TXT", "docx", "txt");
    File f;
    JFileChooser j = new JFileChooser();
    String data1[][] = {};
    String cabecera1[] = {"No.", " Token ", " Tipo"};
    String path;
    int count = 0;
    int erros;
    String msg = "";
    String tipo = "";

    Escopo escopoAtual = null;

    private final Set<String> symbolTable = new HashSet<>();

    public Janela() {
    	setTitle("Compilador - Análise Léxica, Sintática e Semântica");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        initComponents();
    }
    private void initComponents() {

        tela = new javax.swing.JPanel();
        tabelaView = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        codigoLabel = new javax.swing.JLabel();
        gerarTabelaBtn = new javax.swing.JButton();
        tabelaLabel = new javax.swing.JLabel();
        limparBtn = new javax.swing.JButton();
        erroView = new javax.swing.JScrollPane();
        Error = new javax.swing.JEditorPane();
        analiseBtn = new javax.swing.JButton();
        codigoScroll = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        txtATexto1 = new javax.swing.JEditorPane();
        linhas = new javax.swing.JEditorPane();
        linhasErro = new javax.swing.JEditorPane();
        listaErrLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabela.setFont(new java.awt.Font("Century Gothic", 1, 14));
        tabela.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {},
                        {}
                },
                new String[]{

                }
        ));
        tabelaView.setViewportView(tabela);

        codigoLabel.setFont(new java.awt.Font("Times New Roman", 1, 17));
        codigoLabel.setForeground(new java.awt.Color(50, 14, 79));
        codigoLabel.setText("Código:");
        codigoLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabelaLabel.setFont(new java.awt.Font("Times New Roman", 1, 17));
        tabelaLabel.setForeground(new java.awt.Color(50, 14, 79));
        tabelaLabel.setText("Tabela de Símbolos");
        tabelaLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        listaErrLabel.setFont(new java.awt.Font("Times New Roman", 1, 17));
        listaErrLabel.setForeground(new java.awt.Color(50, 14, 79));
        listaErrLabel.setText("Lista de erros Sintáticos e Semânticos");
        listaErrLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        gerarTabelaBtn.setBackground(new java.awt.Color(204, 204, 204));
        gerarTabelaBtn.setFont(new java.awt.Font("Times New Roman", 1, 14));
        gerarTabelaBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/Table.png")));
        gerarTabelaBtn.setText("Gerar tabela de análise léxica");
        gerarTabelaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        limparBtn.setFont(new java.awt.Font("Times New Roman", 1, 14));
        limparBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/Brush.png")));
        limparBtn.setText("Limpar");
        limparBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        
        analiseBtn.setFont(new java.awt.Font("Arial", 1, 14));
        analiseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/Component.png")));
        analiseBtn.setText("Análise Sintático-Semântico");
        analiseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        Error.setEditable(false);
        Error.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Error.setFont(new java.awt.Font("Times New Roman", 1, 17));
        Error.setForeground(java.awt.Color.RED);
        erroView.setViewportView(Error);

        txtATexto1.setFont(new java.awt.Font("Arial", 1, 12));
        txtATexto1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtATexto1KeyPressed(evt);
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtATexto1KeyReleased(evt);
            }
        });

        linhas.setEditable(false);
        linhas.setText("1");
        linhas.setOpaque(false);

        linhasErro.setEditable(false);
        linhasErro.setFont(new java.awt.Font("Tahoma", 1, 11));
        linhasErro.setForeground(java.awt.Color.red);
        linhasErro.setToolTipText("");
        linhasErro.setOpaque(false);

        GroupLayout codigoTela = new GroupLayout(jPanel1);
        jPanel1.setLayout(codigoTela);
        codigoTela.setHorizontalGroup(
            codigoTela.createSequentialGroup()
                .addComponent(linhasErro, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                .addComponent(linhas, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtATexto1, GroupLayout.PREFERRED_SIZE, 460, GroupLayout.PREFERRED_SIZE)
        );
        codigoTela.setVerticalGroup(
            codigoTela.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(linhasErro, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                .addComponent(linhas, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
                .addComponent(txtATexto1, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE)
        );

        codigoScroll.setViewportView(jPanel1);
        GroupLayout layout = new GroupLayout(tela);
        tela.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(codigoLabel)
                    .addComponent(codigoScroll, 510, 510, 510))
                .addGroup(layout.createParallelGroup()
                    .addComponent(tabelaLabel)
                    .addComponent(tabelaView, 300, 300, 300)
                    .addComponent(gerarTabelaBtn)
                    .addComponent(analiseBtn)
                    .addComponent(limparBtn))
                .addGroup(layout.createParallelGroup()
                    .addComponent(listaErrLabel)
                    .addComponent(erroView, 300, 300, 300))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(codigoLabel)
                    .addComponent(codigoScroll, 450, 450, 450))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(tabelaLabel)
                    .addComponent(tabelaView, 300, 300, 300)
                    .addComponent(gerarTabelaBtn, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(analiseBtn, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(limparBtn, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(listaErrLabel)
                    .addComponent(erroView, 450, 450, 450))
        );

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/About.png")));
        jMenu1.setText("Opções");
        jMenu1.setFont(new java.awt.Font("Century Gothic", 1, 14));

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/Save.png")));
        jMenu2.setText("Abrir");
        jMenu2.setFont(new java.awt.Font("Century Gothic", 1, 12));

        jMenuItem1.setFont(new java.awt.Font("Century Gothic", 1, 12));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/New document.png")));
        jMenuItem1.setText("Abrir arquivo.txt");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenu1.add(jMenu2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout2 = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout2);
        layout2.setHorizontalGroup(
                layout2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(tela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 42, Short.MAX_VALUE))
        );
        layout2.setVerticalGroup(
                layout2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(tela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 37, Short.MAX_VALUE))
        );

        pack();
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        j.setCurrentDirectory(new File("src\\lexicosintactico"));
        j.getSelectedFile();
        j.setFileFilter(filtro);
        j.showOpenDialog(j);

        int contPalavra = 0;
        try {
            path = j.getSelectedFile().getAbsolutePath();
            String name = j.getSelectedFile().getName();
            String lectura = "";
            f = new File(path);

            try {

                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String aux;
                StreamTokenizer st = new StreamTokenizer(new FileReader(f));
                while (st.nextToken() != StreamTokenizer.TT_EOF) {
                    if (st.ttype == StreamTokenizer.TT_WORD) {
                        contPalavra++;

                    }
                }
                while ((aux = br.readLine()) != null)
                    lectura = lectura + aux + "\n";

            } catch (IOException e) {
            }

            txtATexto1.setText(lectura);
            int contador = 0;
            StringTokenizer st = new StringTokenizer(txtATexto1.getText(), "\n", true);
            String Text = "", token;
            contador = 1;

            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if ("\n".equals(token)) contador++;
            }

            for (int i = 1; i <= contador; i++) {
                Text += i + "\n";
            }
            linhas.setText(Text);


        } catch (NullPointerException e) {

            javax.swing.JOptionPane.showMessageDialog(j, "Saindo do programa...");

            System.exit(0);

        }
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        String input = txtATexto1.getText();

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Lexema", "Tipo", "Código"});

        try {
            List<LexicalAnalyzer.Token> tokens = lexer.tokenize(input);

            for (LexicalAnalyzer.Token token : tokens) {
                if (token.type != LexicalAnalyzer.TokenType.EOF) {
                    model.addRow(new Object[]{
                            token.lexeme,
                            token.type.toString(),
                            LexicalAnalyzer.getTokencodemap().get(token.type)
                    });
                }
            }

            tabela.setModel(model);

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, "Erro léxico: " + e.getMessage());
        }
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        erros = 0;

        LinkedList<String> ENT = new LinkedList<>();
        LinkedList<String> DEC = new LinkedList<>();
        LinkedList<String> TEXT = new LinkedList<>();
        LinkedList<String> TAKE = new LinkedList<>();

        Error.setText("");
        linhasErro.setText("");
        String txt = "";

        try {
            symbolTable.clear();
            ENT.clear();
            DEC.clear();
            TEXT.clear();
            TAKE.clear();

            String entrada = txtATexto1.getText();
            LexicalAnalyzer lexer = new LexicalAnalyzer();
            List<LexicalAnalyzer.Token> tokens = lexer.tokenize(entrada);

            for (int i = 0; i < tokens.size(); i++) {
                LexicalAnalyzer.Token token = tokens.get(i);

                if (token.type == LexicalAnalyzer.TokenType.PROGRAM) {
                    escopoAtual = new Escopo();
                }

                if (token.type == LexicalAnalyzer.TokenType.VAR) {
                    int k = i + 1;
                    List<String> nomes = new ArrayList<>();

                    while (k < tokens.size() && tokens.get(k).type == LexicalAnalyzer.TokenType.IDENT) {
                        nomes.add(tokens.get(k).lexeme);
                        k++;
                        if (k < tokens.size() && tokens.get(k).type == LexicalAnalyzer.TokenType.COMMA) {
                            k++;
                        } else {
                            break;
                        }
                    }

                    for (String nome : nomes) {
                        if (escopoAtual.ENT.contains(nome) || escopoAtual.DEC.contains(nome) || escopoAtual.TEXT.contains(nome) || escopoAtual.TAKE.contains(nome)) {
                            Error.setText("Variável repetida: '" + nome + "' na linha " + token.line);
                            erros = 1;
                            break;
                        }
                    }

                    if (erros == 0) {
                        int j = k;
                        while (j < tokens.size() && tokens.get(j).type != LexicalAnalyzer.TokenType.COLON) j++;

                        if (j + 1 < tokens.size()) {
                            LexicalAnalyzer.TokenType tipo = tokens.get(j + 1).type;
                            switch (tipo) {
                                case INTEGER -> escopoAtual.ENT.addAll(nomes);
                                case REAL -> escopoAtual.DEC.addAll(nomes);
                                case STRING -> escopoAtual.TEXT.addAll(nomes);
                                default -> { /* tratamento */ }
                            }
                        }
                    }
                    i = k;

                    int j = k;
                    while (j < tokens.size() && tokens.get(j).type != LexicalAnalyzer.TokenType.COLON) {
                        j++;
                    }

                    if (j + 1 < tokens.size()) {
                        LexicalAnalyzer.TokenType tipo = tokens.get(j + 1).type;
                        switch (tipo) {
                            case INTEGER -> ENT.addAll(nomes);
                            case REAL -> DEC.addAll(nomes);
                            case STRING -> TEXT.addAll(nomes);
                            default -> {
                            }
                        }
                    }

                    i = k;
                }

                if (token.type == LexicalAnalyzer.TokenType.READ && i + 2 < tokens.size()) {
                    LexicalAnalyzer.Token idTok = tokens.get(i + 2);
                    if (idTok.type == LexicalAnalyzer.TokenType.IDENT && !TAKE.contains(idTok.lexeme)) {
                        TAKE.add(idTok.lexeme);
                    }
                }
            }

            Parser parser = new Parser(tokens);
            parser.parse();

        } catch (Exception e) {
            Error.setText("Erro: " + e.getMessage());
            erros = 1;
        }

        if (erros == 0) {
            Error.setText("Código analisado com sucesso!");
        }

    }


    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        txtATexto1.setText("");
        linhasErro.setText("");
        Error.setText("");

    }

    private void txtATexto1KeyPressed(java.awt.event.KeyEvent evt) {
        StringTokenizer st = new StringTokenizer(txtATexto1.getText(), "\n", true);
        String txt = "", token;
        linhasErro.setText("");
        Error.setText("");
        count = 1;

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if ("\n".equals(token)) count++;
        }

        for (int i = 1; i <= count; i++) {
            txt += i + "\n";
        }
        linhas.setText(txt);
    }

    private void txtATexto1KeyReleased(java.awt.event.KeyEvent evt) {
        StringTokenizer st = new StringTokenizer(txtATexto1.getText(), "\n", true);
        String txt = "", token;
        count = 1;

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if ("\n".equals(token)) count++;
        }

        for (int i = 1; i <= count; i++) {
            txt += i + "\n";
        }
        linhas.setText(txt);
    }


    private int processarPrograma(List<LexicalAnalyzer.Token> tokens, int i, StringBuilder traducao) {
        symbolTable.clear();
        if (i + 2 < tokens.size()
                && tokens.get(i + 1).type == LexicalAnalyzer.TokenType.IDENT
                && tokens.get(i + 2).type == LexicalAnalyzer.TokenType.SEMICOLON) {

            String nomePrograma = tokens.get(i + 1).lexeme;
            traducao.append("REM Programa ").append(nomePrograma).append("\n");
            i += 3;

            if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.VAR) {
                i = processarVariaveis(tokens, i, traducao);
            }

            if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.BEGIN) {
                i = processarBloco(tokens, i, traducao);
            }
        } else {
            i++;
        }
        return i;
    }

    private int processarVariaveis(List<LexicalAnalyzer.Token> tokens, int i, StringBuilder traducao) {
        i++;

        while (i < tokens.size() && tokens.get(i).type != LexicalAnalyzer.TokenType.BEGIN) {
            i = processarDeclaracaoVariavel(tokens, i, traducao);
        }
        return i;
    }

    private int processarDeclaracaoVariavel(List<LexicalAnalyzer.Token> tokens, int i, StringBuilder traducao) {
        List<String> nomes = new ArrayList<>();

        while (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.IDENT) {
            nomes.add(tokens.get(i).lexeme);
            i++;
            if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.COMMA) {
                i++;
            } else {
                break;
            }
        }

        for (String nome : nomes) {
            if (symbolTable.contains(nome)) {
                throw new RuntimeException("Variável repetida: '" + nome + "'");
            }
            symbolTable.add(nome);
        }


        if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.COLON) {
            i++;
        } else {
            return i;
        }

        String tipoBasic = "";
        if (i < tokens.size()) {
            switch (tokens.get(i).type) {
                case INTEGER:
                    tipoBasic = "AS INTEGER";
                    break;
                case STRING:
                    tipoBasic = "AS STRING";
                    break;
                default:
                    tipoBasic = "";
            }
            i++;
        }

        if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.SEMICOLON) {
            i++;
        }

        traducao.append("DIM ");
        for (int j = 0; j < nomes.size(); j++) {
            if (j > 0) traducao.append(", ");
            traducao.append(nomes.get(j));
        }
        traducao.append(" ").append(tipoBasic).append("\n");

        return i;
    }

    private int processarBloco(List<LexicalAnalyzer.Token> tokens, int i, StringBuilder traducao) {
        if (tokens.get(i).type == LexicalAnalyzer.TokenType.BEGIN) {
            i++;
        } else {
            return i;
        }

        while (i < tokens.size() && tokens.get(i).type != LexicalAnalyzer.TokenType.END) {
            i = processarComando(tokens, i, traducao);
        }

        if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.END) {
            i++;
            if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.DOT) {
                i++;
            }
        }
        return i;
    }

    private int processarComando(List<LexicalAnalyzer.Token> tokens, int i, StringBuilder traducao) {
        LexicalAnalyzer.Token token = tokens.get(i);

        if (token.type == LexicalAnalyzer.TokenType.IDENT) {
            String varName = token.lexeme;
            i++;
            if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.ASSIGN) {
                i++;

                StringBuilder expr = new StringBuilder();
                while (i < tokens.size() && tokens.get(i).type != LexicalAnalyzer.TokenType.SEMICOLON) {
                    expr.append(tokens.get(i).lexeme).append(" ");
                    i++;
                }
                if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.SEMICOLON) {
                    i++;
                }
                traducao.append(varName).append(" = ").append(expr.toString().trim()).append("\n");
            } else {
                i++;
            }
        } else if (token.type == LexicalAnalyzer.TokenType.WRITELN) {
            i++;
            if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.LPAR) {
                i++;
                StringBuilder content = new StringBuilder();
                while (i < tokens.size() && tokens.get(i).type != LexicalAnalyzer.TokenType.RPAR) {
                    content.append(tokens.get(i).lexeme).append(" ");
                    i++;
                }
                if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.RPAR) {
                    i++;
                }
                if (i < tokens.size() && tokens.get(i).type == LexicalAnalyzer.TokenType.SEMICOLON) {
                    i++;
                }
                traducao.append("PRINT ").append(content.toString().trim()).append("\n");
            }
        } else {
            i++;
        }

        return i;
    }
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Janela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Janela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Janela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Janela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Janela().setVisible(true);
            }
        });
    }

    private javax.swing.JEditorPane Error;
    private javax.swing.JEditorPane linhasErro;
    private javax.swing.JEditorPane linhas;
    private javax.swing.JButton gerarTabelaBtn;
    private javax.swing.JButton analiseBtn;
    private javax.swing.JButton limparBtn;
    private javax.swing.JLabel codigoLabel;
    private javax.swing.JLabel tabelaLabel;
    private javax.swing.JLabel listaErrLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane codigoScroll;
    private javax.swing.JScrollPane tabelaView;
    private javax.swing.JScrollPane erroView;
    private javax.swing.JPanel tela;
    private javax.swing.JTable tabela;
    private javax.swing.JEditorPane txtATexto1;
}
