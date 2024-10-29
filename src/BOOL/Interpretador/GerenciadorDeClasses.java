package BOOL.Interpretador;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorDeClasses {
    public static Map<String, VariavelObjeto> classes;  // Mantido como public static para acesso direto
    private Map<String, VariavelObjeto> instancias;  // Armazena instâncias com um nome específico
    private Map<String, Variavel> variaveis;  // Map para armazenar variáveis de ambiente com informações para GC

    public GerenciadorDeClasses() {
        classes = new HashMap<>();
        instancias = new HashMap<>();
        variaveis = new HashMap<>();
    }

    // --- Métodos Existentes para Manipular Classes e Instâncias ---

    // Define uma nova classe com o nome dado
    public void definirClasse(String nomeClasse) {
        if (classes.containsKey(nomeClasse)) {
            throw new RuntimeException("Erro: Classe '" + nomeClasse + "' já existe!");
        }
        classes.put(nomeClasse, new VariavelObjeto(nomeClasse));
    }

    // Verifica se uma classe existe
    public boolean classeExiste(String nomeClasse) {
        return classes.containsKey(nomeClasse);
    }

    // Cria uma nova instância com um nome específico e a armazena no mapa de instâncias
    public void criarInstancia(String nomeClasse, String nomeInstancia) {
        if (instancias.containsKey(nomeInstancia)) {
            throw new RuntimeException("Erro: Instância com nome '" + nomeInstancia + "' já existe!");
        }
        VariavelObjeto classe = obterClasse(nomeClasse);
        VariavelObjeto novaInstancia = VariavelObjeto.copiarDe(classe);  // Cria uma cópia da classe
        instancias.put(nomeInstancia, novaInstancia);  // Armazena a nova instância com o nome específico
    }

    // Retorna uma instância pelo nome
    public VariavelObjeto obterInstancia(String nomeInstancia) {
        if (!instancias.containsKey(nomeInstancia)) {
            throw new RuntimeException("Erro: Instância '" + nomeInstancia + "' não encontrada!");
        }
        return instancias.get(nomeInstancia);
    }

    public boolean istanciaExiste(String nomeInstancia) {
        return instancias.containsKey(nomeInstancia);
    }

    // Retorna a definição de uma classe pelo nome
    public VariavelObjeto obterClasse(String nomeClasse) {
        if (!classes.containsKey(nomeClasse)) {
            throw new RuntimeException("Erro: Classe '" + nomeClasse + "' não encontrada!");
        }
        return classes.get(nomeClasse);
    }

    // --- Métodos para Manipular o Map de Variáveis ---

    // Adiciona uma nova variável ao ambiente
    public void adicionarVariavel(String nome, Integer valor) {
        if (variaveis.containsKey(nome)) {
            throw new RuntimeException("Erro: Variável '" + nome + "' já existe!");
        }
        variaveis.put(nome, new Variavel(nome, valor));
    }

    // Atualiza o valor de uma variável existente
    public void atualizarVariavel(String nome, Integer novoValor) {
        Variavel variavel = variaveis.get(nome);
        if (variavel == null) {
            throw new RuntimeException("Erro: Variável '" + nome + "' não encontrada!");
        }
        variavel.setValor(novoValor);
    }

    // Obtém o valor de uma variável pelo nome
    public Integer obterValorVariavel(String nome) {
        Variavel variavel = variaveis.get(nome);
        if (variavel == null) {
            throw new RuntimeException("Erro: Variável '" + nome + "' não encontrada!");
        }
        return variavel.getValor();
    }

    // Verifica se uma variável existe no ambiente
    public boolean variavelExiste(String nome) {
        return variaveis.containsKey(nome);
    }

    // Remove uma variável do ambiente
    public void removerVariavel(String nome) {
        if (!variaveis.containsKey(nome)) {
            throw new RuntimeException("Erro: Variável '" + nome + "' não encontrada para remoção!");
        }
        variaveis.remove(nome);
    }

}
