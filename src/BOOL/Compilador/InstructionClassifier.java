package BOOL.Compilador;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstructionClassifier {
    private final Compiler compiler; // Referência ao compilador



    // Construtor que recebe uma instância de Compiler
    public InstructionClassifier(Compiler compiler) {
        this.compiler = compiler;
    }

    public void classifyAndGenerateTokens(String line) {
        // Remover espaços em branco no início e no fim da linha
        line = line.trim();

        // Ignorar linhas vazias ou que contenham apenas uma chave de fechamento
        if (line.isEmpty() || line.matches("^\\s*}\\s*$")) {
            return;
        }

        // regex para cada tipo de instrução
        String classPattern = "^class\\s+.*";   //feito
        String varsPattern = "^vars\\s+.*$";    //feito
        String methodPattern = "^method\\s+.*$";    //feito  analisar caso com parametro
        String mainPattern = "^main\\s*\\(\\s*\\)\\s*$";    //feito
        String beginPattern = "^begin$";    //feito
        String returnPattern = "^return\\s+.*$";    //feito
        String ifPattern = "^if\\s+.*$"; //feito
        String elsePattern = "\\s*else\\s*";    //feito
        String newPattern = "^.*=\\s*new\\s+.*$";   //feito
        String assignmentPattern = "^\\w+(\\.\\w+)?\\s*=.*$";   //feito
        String methodCallPattern = "^\\w+(\\.\\w+)\\s*\\(.*\\)\\s*$"; //feito
        String endClassPattern = "^end-class$"; //feito
        String endMethodPattern = "^end-method$";   //feito
        String endIfPattern = "^end-if$";   //feito
        String endPattern = "^end$";    //feito

        // Verifica o tipo de instrução usando expressões regulares
        if (line.matches(classPattern)) {
    
            handleClassDefinition(line);
        } else if (line.matches(varsPattern)) {
            
            handleVarsDefinition(line);
        } else if (line.matches(methodPattern)) {
            
            handleMethodDefinition(line);
        } else if (line.matches(mainPattern)) {
            
            handleMainDefinition();
        } else if (line.matches(beginPattern)) {
            
            handleBeginPattern();
        } else if (line.matches(returnPattern)) {
            
            handleReturn(line);
        } else if (line.matches(ifPattern)) {
            
            handleIfStatement(line);
        } else if (line.matches(elsePattern)) {
            
            handleElseStatement(line);
        } else if (line.matches(newPattern)) {
            
            handleObjectCreation(line);
        } else if (line.matches(assignmentPattern)) {
            
            handleAssignment(line);
        } else if (line.matches(methodCallPattern)) {
            
            handleMethodCall(line);
        } else if (line.matches(endClassPattern)) {

            compiler.writeOutput("end-class");
        } else if (line.matches(endMethodPattern)) {

            compiler.writeOutput("end-method");
        } else if (line.matches(endIfPattern)) {

            handleEndIfPattern(line);
        } else if (line.matches(endPattern)) {

            compiler.writeOutput("end");
        } else {

            System.out.println("Instrução não reconhecida: " + line);
        }
    }

    private void handleClassDefinition(String line) {
        // Extrai o nome que vem após 'class'
        String className = line.split("\\s+")[1];
        String outputLine = "class " + className;
    
        // Escrever a linha formatada no arquivo de saída
        compiler.writeOutput(outputLine);
    }
    



    private void handleVarsDefinition(String line) {
        // Remover a parte 'vars' e espaços em branco ao redor
        String varsLine = line.substring(4).trim();
        
        // Dividir os nomes das variáveis usando a vírgula como delimitador
        String[] variableNames = varsLine.split("\\s*,\\s*");
        
        // Para cada variável, gerar a saída correspondente
        for (String varName : variableNames) {

            if (!varName.isEmpty()) {
                String outputLine = "vars " + varName;
                compiler.writeOutput(outputLine);
            }
        }
    }
    


    private void handleMethodDefinition(String line) {
        // Remover a parte "method" e espaços em branco ao redor
        String methodLine = line.substring(6).trim();
        
        // Encontra o índice do primeiro '(' e ')'
        int paramStartIndex = methodLine.indexOf("(");
        int paramEndIndex = methodLine.indexOf(")");
        
        // Extrai o nome do método
        String methodName = methodLine.substring(0, paramStartIndex).trim();
        
        // Extrai os parâmetros entre '(' e ')', se houver
        String parameters = methodLine.substring(paramStartIndex + 1, paramEndIndex).trim();
        
        // Remove espaços extras entre os parâmetros
        String formattedParameters;
        if (!parameters.isEmpty()) {
            String[] paramArray = parameters.split("\\s*,\\s*"); // Divide os parâmetros por vírgula e remove espaços
            formattedParameters = String.join(", ", paramArray); // Junta os parâmetros novamente
        } else {
            formattedParameters = ""; // Não há parâmetros
        }
        
        // Monta a linha do método com os parâmetros (se existirem)
        String outputMethodLine;
        if (!formattedParameters.isEmpty()) {
            outputMethodLine = "method " + methodName + "(" + formattedParameters + ")";
        } else {
            outputMethodLine = "method " + methodName + "()";
        }
        
        // Escreve a linha no arquivo de saída
        compiler.writeOutput(outputMethodLine);
    }
    
  
    
    private void handleMainDefinition() {
        String outputLine = "main()";
        compiler.writeOutput(outputLine);
    }

    private void handleBeginPattern() {
        String outputLine = "begin";
        compiler.writeOutput(outputLine);
    }



    private void handleReturn(String line) {
        // Extrair o nome que vem após "return"
        String returnValue = line.substring(6).trim();
    
        // Gerar as saídas desejadas
        String loadOutput = "load " + returnValue;
        String retOutput = "ret";
    
        // Escrever as linhas formatadas no arquivo de saída
        compiler.writeOutput(loadOutput);
        compiler.writeOutput(retOutput);
    }

    

    private void handleObjectCreation(String line) {

        line = line.trim();
    
        // Divide a linha pelo símbolo "="
        String[] parts = line.split("=");
    
        // Assumindo que sempre teremos dois lados, imprime diretamente
        String lhs = parts[0].trim();
        String rhs = parts[1].trim();
    
        // Gera a saída para a criação de objeto
        compiler.writeOutput(rhs);
        compiler.writeOutput("store " + lhs);
    }
    


    private void handleAssignment(String line) {
        
        line = line.trim();
    
        // Verifica se a linha contém um símbolo de atribuição '='
        String[] parts = line.split("=");
        if (parts.length != 2) {
            System.out.println("Atribuição malformada: " + line);
            return;
        }
    
        String lhs = parts[0].trim();
        String rhs = parts[1].trim();
    
        // Identificar o tipo de expressão no lado direito e delegar para subfunções
        if (isNumber(rhs)) {
            handleNumberAssignment(lhs, rhs);       //para o caso de atribuição de numero
        } else if (isMethodCall(rhs)) {
            handleMethodCallAssignment(lhs, rhs);   //para o caso de atribuição via função
        } else if (isSimpleVariable(rhs)) {
            handleVariableAssignment(lhs, rhs);     //para o caso de atribuição de variavel
        } else if (containsOperation(rhs)) {
            handleOperationAssignment(lhs, rhs);    //para o caso de atribuição com operação +|-|*|/
        } else {
            System.out.println("Atribuição não reconhecida: " + line);
        }
    }
    
    // Verifica se o lado direito é um número
    private boolean isNumber(String rhs) {
        return rhs.matches("-?\\d+"); // Verifica se é um número
    }
    
    // Verifica se o lado direito é uma variável simples ou notação de ponto
    private boolean isSimpleVariable(String rhs) {
        return rhs.matches("\\w+") || rhs.contains(".");
    }
    
    // Verifica se o lado direito é uma chamada de método
    private boolean isMethodCall(String rhs) {
        return rhs.matches("^.*?\\.\\w+\\s*\\(.*?\\)\\s*$");
    }
    
    // Verifica se o lado direito contém uma operação aritmética
    private boolean containsOperation(String rhs) {
        return rhs.matches(".*[+\\-*/].*");
    }
    
    // Subfunção para lidar com atribuições de números
    private void handleNumberAssignment(String lhs, String rhs) {
        compiler.writeOutput("const " + rhs);
    
        if (lhs.contains(".")) {
            handleLHSWithDot(lhs);
        } else {
            compiler.writeOutput("store " + lhs);
        }
    }
    
    // Subfunção para lidar com atribuições de variáveis simples ou notação de ponto
    private void handleVariableAssignment(String lhs, String rhs) {
        if (rhs.contains(".")) {
            
            String[] rightParts = rhs.split("\\.");
            compiler.writeOutput("load " + rightParts[0].trim());
            compiler.writeOutput("get " + rightParts[1].trim());
        } else {
            
            compiler.writeOutput("load " + rhs);
        }
    
    
        if (lhs.contains(".")) {
            handleLHSWithDot(lhs);
        } else {
            compiler.writeOutput("store " + lhs);
        }
    }
    
    // Subfunção para lidar com operações aritméticas
    private void handleOperationAssignment(String lhs, String rhs) {
        String[] operationParts = rhs.split("\\s*[+\\-*/]\\s*");
        String leftOperand = operationParts[0].trim();
        String rightOperand = operationParts[1].trim();
        String operator = rhs.replaceAll(".*?([+\\-*/]).*", "$1").trim();
    

        generateLoadForOperand(leftOperand);
    
        generateLoadForOperand(rightOperand);
    
        generateOperator(operator);
    
        // Tratar o LHS (se houver notação de ponto)
        if (lhs.contains(".")) {
            handleLHSWithDot(lhs);
        } else {
            compiler.writeOutput("store " + lhs);
        }
    }
    
    // Subfunção para lidar com atribuições que são chamadas de método
    private void handleMethodCallAssignment(String lhs, String rhs) {
        
        String methodName = rhs.substring(rhs.indexOf('.') + 1, rhs.indexOf('(')).trim();
        String params = rhs.substring(rhs.indexOf('(') + 1, rhs.indexOf(')')).trim();
    
        // Lidar com os parâmetros, se houver
        if (!params.isEmpty()) {
            String[] paramArray = params.split(",\\s*");
            for (String param : paramArray) {
                compiler.writeOutput("load " + param.trim());
            }
        }
    
        // Lidar com o objeto da chamada do método
        String objectName = rhs.substring(0, rhs.indexOf('.')).trim();
        compiler.writeOutput("load " + objectName);
        compiler.writeOutput("call " + methodName);
    
        // Tratar o LHS (se houver notação de ponto)
        if (lhs.contains(".")) {
            handleLHSWithDot(lhs);
        } else {
            compiler.writeOutput("store " + lhs);
        }
    }
    
    // Função para gerar o código de load para um operando (pode ser uma variável ou um número)
    private void generateLoadForOperand(String operand) {
        if (isNumber(operand)) {
            compiler.writeOutput("const " + operand);
        } else {
            compiler.writeOutput("load " + operand);
        }
    }
    
    // Função para gerar o código de operação (add, sub, mul, div)
    private void generateOperator(String operator) {
        switch (operator) {
            case "+":
                compiler.writeOutput("add");
                break;
            case "-":
                compiler.writeOutput("sub");
                break;
            case "*":
                compiler.writeOutput("mul");
                break;
            case "/":
                compiler.writeOutput("div");
                break;
        }
    }
    
    // Função para lidar com o LHS que contém um ponto
    private void handleLHSWithDot(String lhs) {
        String[] leftParts = lhs.split("\\.");
        compiler.writeOutput("load " + leftParts[0].trim());
        compiler.writeOutput("set " + leftParts[1].trim());
    }
    
    
    // Função para lidar com a chamada de methodo, podendo haver parametros ou não
    private void handleMethodCall(String line) {

        line = line.trim();
    
        // Dividir a chamada do método em objeto e método
        String[] methodParts = line.split("\\.");
        String obj = methodParts[0].trim();
    
        // Capturar nome do método e parâmetros
        String methodAndParams = methodParts[1].trim(); 
        String methodName = methodAndParams.substring(0, methodAndParams.indexOf("(")).trim();
        String params = methodAndParams.substring(methodAndParams.indexOf("(") + 1, methodAndParams.indexOf(")")).trim();
    
        // Se houver parâmetros, empilhá-los da esquerda para direita
        if (!params.isEmpty()) {
            String[] paramList = params.split(",");
            for (String param : paramList) {
                compiler.writeOutput("load " + param.trim());
            }
        }
        

        compiler.writeOutput("load " + obj);
        compiler.writeOutput("call " + methodName);
        compiler.writeOutput("pop");
    }
    

    // Função para o caso if, quebra a função em partes ficando o if os nomes e o comparador em lugares diferentes
    private void handleIfStatement(String line) {

        line = line.trim();

        String[] parts = line.split(" ");
    
        String comparison = parts[2];
    
        // Verificar se o comparador é válido
        if (!comparison.equals("lt") && !comparison.equals("le") && !comparison.equals("gt") &&
            !comparison.equals("ge") && !comparison.equals("eq") && !comparison.equals("ne")) {
            throw new IllegalArgumentException("Invalid comparator: " + comparison);
        }
    

        compiler.writeOutput("load " + parts[1]);
        compiler.writeOutput("load " + parts[3]);
    
        // Adicionar o comparador ao arquivo de saída
        compiler.writeOutput(comparison);

        compiler.writeOutput("if");
    }


    //Função para else
    private void handleElseStatement(String line) {
        compiler.writeOutput("else");
    }
    
    
    //Função para a instrução end-if
    //esta função percorre o arquivo de baixo para cima ate encontrar o if
    //adiciona a contagem de linhas apos o if para saber quantos instruçõs deveram ser puladas
    private void handleEndIfPattern(String line) {
        try {

            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(compiler.getOutputFilename()))) {
                String outputLine;
                while ((outputLine = br.readLine()) != null) {
                    lines.add(outputLine);
                }
            }
    
            // Contador de linhas
            int lineCount = 0;
            boolean foundElse = false;
            
            // Contar as linhas do final para o começo
            for (int i = lines.size() - 1; i >= 0; i--) {
                lineCount++;
    
                String currentLine = lines.get(i).trim();
                
                if (currentLine.contains("else") && !foundElse) {
                    // Encontrou "else", coloca o número à frente e reinicia a contagem
                    lines.set(i, currentLine + " " + lineCount);
                    // Reseta a contagem após o "else"
                    lineCount = 0;
                    foundElse = true;
                } else if (currentLine.contains("if")) {
                    // Encontrou "if", coloca o número à frente e para
                    lines.set(i, currentLine + " " + lineCount);
                    break;
                }
            }
    
            // Adicionar "end-if" ao final do arquivo
            lines.add("end-if");
    
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(compiler.getOutputFilename()))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            }
    
        } catch (IOException e) {
            System.err.println("Erro ao manipular o arquivo de saída: " + e.getMessage());
        }
    }
    

    //Função estética que coloca alguns \t nas linhas para facilitar a leitura do compilado, pode ser ignorada
    public void ajustarIndentacao() {
        try {
            // Ler todas as linhas do arquivo de saída
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(compiler.getOutputFilename()))) {
                String outputLine;
                while ((outputLine = br.readLine()) != null) {
                    lines.add(outputLine);
                }
            }
    
            // Contador de linhas e nível de indentação
            int indentLevel = 0;
            
            // Percorrer as linhas do começo ao fim
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i).trim();
    
                // Reduz o nível de indentação ao encontrar "end"
                if (currentLine.startsWith("end")) {
                    indentLevel = Math.max(0, indentLevel - 1);
                }
    
                // Atualiza a linha com o nível de indentação apropriado
                StringBuilder indentedLine = new StringBuilder();
                for (int j = 0; j < indentLevel; j++) {
                    indentedLine.append("\t"); // Adiciona tabs para cada nível de indentação
                }
                indentedLine.append(currentLine);
                lines.set(i, indentedLine.toString());
    
                // Aumenta o nível de indentação ao encontrar "begin"
                if (currentLine.startsWith("begin")) {
                    indentLevel++;
                }
            }
    
            // Reescreve o arquivo de saída com as alterações
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(compiler.getOutputFilename()))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            }
    
        } catch (IOException e) {
            System.err.println("Erro ao manipular o arquivo de saída: " + e.getMessage());
        }
    }
    
}
