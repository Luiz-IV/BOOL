package BOOL.Interpretador;

public class Variavel {
    private String nome;  // Nome da variável
    private Integer valor;  // Valor da variável

    // Construtor para inicializar a variável com nome e valor
    public Variavel(String nome, Integer valor) {
        this.nome = nome;
        this.valor = valor;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "VariavelValor{" + "nome='" + nome + '\'' + ", valor=" + valor + '}';
    }
}
