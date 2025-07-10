package lexicoSintatico;
import java.util.List;

public class Simbolo {
    private String nome;
    private String tipo;       // integer, real, string
    private String categoria;  // variavel, constante, procedure
    private Object valor;      // Para constantes ou valor inicial
    private int linhaDeclaracao;
    private List<String> parametros; // Para procedures (pode ser null)

    // Construtor
    public Simbolo(String nome, String tipo, String categoria, Object valor, int linhaDeclaracao, List<String> parametros) {
        this.nome = nome;
        this.tipo = tipo;
        this.categoria = categoria;
        this.valor = valor;
        this.linhaDeclaracao = linhaDeclaracao;
        this.parametros = parametros;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public Object getValor() {
        return valor;
    }

    public int getLinhaDeclaracao() {
        return linhaDeclaracao;
    }

    public List<String> getParametros() {
        return parametros;
    }

    // Para imprimir bonitinho na tabela
    @Override
    public String toString() {
        return "Simbolo{" +
                "nome='" + nome + '\'' +
                ", tipo='" + tipo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", valor=" + valor +
                ", linha=" + linhaDeclaracao +
                ", parametros=" + parametros +
                '}';
    }
}
