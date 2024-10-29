package BOOL.Interpretador;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arquivo {
    private List<String> linhas;  // Armazena todas as linhas do arquivo
    private int currentLine;      // A linha atual para leitura contínua

    public Arquivo(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        linhas = new ArrayList<>();
        
        String linha;
        while ((linha = reader.readLine()) != null) {
            linhas.add(linha);  // Adiciona cada linha na lista
        }
        
        reader.close();
        currentLine = 0;  // Inicializa a leitura na primeira linha
    }

    // Retorna a próxima linha de instrução de forma sequencial
    public String getNextInstruction() {
        if (currentLine >= linhas.size()) {
            return null;  // Se chegou ao final do arquivo
        }
        return linhas.get(currentLine++);
    }

    // Retorna a instrução a partir de uma linha específica
    public String getInstructionAtLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= linhas.size()) {
            throw new RuntimeException("Erro: Linha inválida");
        }
        return linhas.get(lineNumber);
    }

    // Método para obter o número total de linhas do arquivo
    public int getTotalLines() {
        return linhas.size();
    }

    // Retorna a linha atual (útil se precisar referenciar)
    public int getCurrentLine() {
        return currentLine;
    }

    // Reinicia a leitura do arquivo do início ou de uma linha específica
    public void reset(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= linhas.size()) {
            throw new RuntimeException("Erro: Linha inválida para reset");
        }
        currentLine = lineNumber;
    }

    // Fecha a leitura do arquivo (não é mais necessário porque o arquivo já foi lido na memória)
    public void close() {
        // Como o arquivo está todo em memória, não há necessidade de um método de fechar
        // Se no futuro quisermos otimizar para grandes arquivos, poderíamos reintroduzir
    }
}
