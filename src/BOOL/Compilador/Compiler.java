package BOOL.Compilador;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    private String inputFilename = "input.bool";
    private String outputFilename = "output.boolc";
    private InstructionClassifier instructionClassifier;

    public Compiler() {
        instructionClassifier = new InstructionClassifier(this);
    }

    public void compile() throws IOException {
        // Cria ou limpa o arquivo de saída
        Files.write(Paths.get(outputFilename), new byte[0]);

        // Lê o arquivo de entrada linha por linha
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                instructionClassifier.classifyAndGenerateTokens(line);
            }
        }

        System.out.println("Compilação concluída com sucesso! Arquivo de saída: " + outputFilename);
    }

    public void writeOutput(String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename, true))) { 
            bw.write(content);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de saída: " + e.getMessage());
        }
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public static void main(String[] args) {
        Compiler compiler = new Compiler();

        // Verifica se o arquivo de entrada existe
        File inputFile = new File(compiler.inputFilename);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("Erro: O arquivo de entrada " + compiler.inputFilename + " não existe ou não é um arquivo válido.");
            return;
        }

        try {
            compiler.compile();
    
            // Chamada para ajustar a indentação no arquivo de saída após a compilação
            //compiler.instructionClassifier.ajustarIndentacao();
            //System.out.println("Indentação ajustada com sucesso! Verifique o arquivo: " + compiler.getOutputFilename());
        } catch (IOException e) {
            System.err.println("Erro durante a compilação ou ajuste de indentação: " + e.getMessage());
        }
    

    }
}
