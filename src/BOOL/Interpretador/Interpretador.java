package BOOL.Interpretador;
import java.io.IOException;

public class Interpretador {
    private Pilha pilhaPrincipal;  // Pilha principal usada pelo interpretador
    private Arquivo arquivo;       // Classe para lidar com o arquivo de instruções
    private int contadorInstrucoes; // Contador de instruções (GC desativado)
    private GerenciadorDeClasses gerenciadorDeClasses;  // Gerenciador de classes
    public Io io;  // Instância do objeto io, acessível globalmente dentro do interpretador

    public Interpretador(String filePath) throws IOException {
        this.arquivo = new Arquivo(filePath);  // Carregar o arquivo
        this.pilhaPrincipal = new Pilha();     // Inicializar a pilha principal
        this.contadorInstrucoes = 0;  // Contador de instruções
        this.gerenciadorDeClasses = new GerenciadorDeClasses();  // Inicializar o gerenciador de classes
        this.io = new Io();  // Inicializar o objeto io
    }

    // Classe interna Io com o método print
    public class Io {
        public int print(int n) {
            System.out.println(n);  // Exibe o valor seguido de nova linha
            return 0;  // Retorna 0 conforme especificado
        }
    }

    // Método principal que inicia a execução
    public void executar() {
        String instrucao;

        // Ler e executar as instruções uma a uma
        while ((instrucao = arquivo.getNextInstruction()) != null) {
            executarInstrucao(instrucao);

            // Contador de instruções (por enquanto, GC não será acionado)
            contadorInstrucoes++;
            /*
            // A cada 5 instruções, chamar o Garbage Collector (temporariamente desativado)
            if (contadorInstrucoes % 5 == 0) {
                garbageCollect();  // Desativado por enquanto
            }
            */
        }
    }

    // Executa uma instrução específica
    private void executarInstrucao(String instrucao) {
        try {
            // Verifica se a instrução é "io.print(<número>)"
            if (instrucao.startsWith("io.print(") && instrucao.endsWith(")")) {
                // Extrai o valor entre parênteses
                String valorString = instrucao.substring(9, instrucao.length() - 1).trim();
                int valor = Integer.parseInt(valorString);
                io.print(valor);  // Chama o método print da instância io
            } else {
                // Passa a instrução para ser executada por Instruction
                Instruction.execute(instrucao, pilhaPrincipal, arquivo, gerenciadorDeClasses, "process");
            }
        } catch (RuntimeException e) {
            System.err.println("Erro durante a execução: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro: argumento inválido para io.print (esperado inteiro).");
        }
    }

    // Simulação do Garbage Collector (desativado por enquanto)
    private void garbageCollect() {
        System.out.println("Garbage Collector acionado! (Inativo)");
        // Implementação futura do mark-and-sweep
    }

    // Método principal para inicializar o interpretador a partir de um arquivo .boolc
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Interpretador <arquivo.boolc>");
            return;
        }

        try {
            // Criar uma instância do interpretador e iniciar a execução
            Interpretador interpretador = new Interpretador(args[0]);
            interpretador.executar();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
