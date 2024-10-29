package BOOL.Interpretador;
import java.util.HashMap;
import java.util.Map;

public class VariavelObjeto {
    private String nomeClasse;  // Novo atributo para armazenar o nome da classe
    private Map<String, Integer> variaveis;  // Variáveis associadas ao objeto
    private Map<String, Integer> metodos;    // Nome do método e linha inicial
    private VariavelObjeto prototype;        // Referência para o _prototype (herança)

    public VariavelObjeto(String nomeClasse) {
        this.nomeClasse = nomeClasse;
        variaveis = new HashMap<>();
        metodos = new HashMap<>();
        prototype = null;  // Inicialmente, sem herança
    }

    public String getNomeClasse() {
        return nomeClasse;
    }

    // Método para criar uma variável sem atribuir valor
    public void criarVariavel(String name) {
        variaveis.put(name, null);  // Adiciona a variável ao mapa sem um valor inicial
    }

    // Define o valor de uma variável
    public void setVariavel(String name, int value) {
        variaveis.put(name, value);
    }

    // Retorna o valor de uma variável (procurando no prototype se necessário)
    public int getVariavel(String name) {
        return variaveis.get(name);
    }

    // Define o _prototype (herança)
    public void setPrototype(VariavelObjeto prototype) {
        this.prototype = prototype;
    }

    // Retorna o _prototype atual
    public VariavelObjeto getPrototype() {
        return prototype;
    }

    // Associa o nome do método à linha inicial
    public void setMetodo(String name, int startLine) {
        metodos.put(name, startLine);
    }

    // Retorna a linha inicial do método (procurando no prototype se necessário)
    public int getMetodoStartLine(String name, VariavelObjeto instancia) {
        if (metodos.containsKey(name)) {
            return metodos.get(name);
        } else {
            VariavelObjeto prototipoInstancia = instancia.getPrototype();
            return prototype.getMetodoStartLine(name, prototipoInstancia);  // Herança
        }
    }

    // Método para copiar a estrutura de outra instância (usado para criação de nova instância)
    public static VariavelObjeto copiarDe(VariavelObjeto classe) {
        VariavelObjeto copia = new VariavelObjeto(classe.getNomeClasse());
        copia.variaveis.putAll(classe.variaveis);
        copia.metodos.putAll(classe.metodos);
        copia.prototype = classe.prototype;
        return copia;
    }
}
