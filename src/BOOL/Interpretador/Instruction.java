package BOOL.Interpretador;

public class Instruction {

    // Método que executa uma instrução
    public static void execute(String instruction, Pilha pilha, Arquivo arquivo, GerenciadorDeClasses gerenciador, String ambiente) {
        String[] parts = instruction.trim().split(" ");
        String command = parts[0];
        String name = parts.length > 1 ? parts[1] : null;

        switch (command) {
            case "class":
                // Exemplo: class NomeDaClasse
                definirClasse(arquivo, gerenciador, name);
                break;

            case "new":
                // Exemplo: new NomeDaClasse
                criarNovaInstancia(gerenciador, name, arquivo);
                break;

            case "method":
                // O method de cada classe já está definido dentro do case "class"
                break;
                
            case "load":
                processarLoad(name, pilha, gerenciador, arquivo, ambiente);
                break;

            case "store":
                processarStore(name, pilha, gerenciador);

            case "if":
                // Exemplo: if a eq b then
                gerenciarCondicional(parts, pilha, arquivo);
                break;

            case "ret":
                // Nada para fazer
                break;

            case "eq": case "ne": case "lt": case "le": case "gt": case "ge":
                // Comparadores: eq, ne, lt, le, gt, ge
                gerenciarComparador(command, pilha);
                break;

            case "main":
                // Função principal
                System.out.println("Iniciando função main.");
                executarMain(pilha, gerenciador, arquivo);
                break;

            case "begin":
                // Início de um método ou bloco
                //System.out.println("Início de bloco/método");
                break;

            case "self":
                // Acessa o objeto atual (self)
                System.out.println("Acessando o self");
                break;

            case "vars":
                // Exemplo: vars a, b, c
                //definirVariaveis(ambiente, parts);
                break;

            case "end-class":
                // Fim da classe
                //System.out.println("Fim da definição da classe.");
                break;

            default:
                throw new RuntimeException("Erro: Instrução não reconhecida: " + command);
        }
    }

    // ----------------------------------------------
    // Métodos auxiliares para manipulação de classes
    // ----------------------------------------------

    // Método para definir uma nova classe e percorrer até encontrar end-class
    private static void definirClasse(Arquivo arquivo, GerenciadorDeClasses gerenciador, String nomeClasse) {
        // Define uma nova classe no gerenciador
        gerenciador.definirClasse(nomeClasse);
        VariavelObjeto classeAtual = gerenciador.obterClasse(nomeClasse);

        String linha;
        try{
            while (!(linha = arquivo.getNextInstruction()).trim().equals("end-class")) {
                String[] partesLinha = linha.trim().split(" ");
                String comandoInterno = partesLinha[0];
                String nameInterno = partesLinha.length > 1 ? partesLinha[1] : null;

                switch (comandoInterno) {
                    case "vars":
                        // Exemplo: vars a, b, c
                        classeAtual.criarVariavel(nameInterno);
                        break;
                    case "method":
                        // Exemplo: method calc
                        int linhaInicioMetodo = arquivo.getCurrentLine();
                        classeAtual.setMetodo(nameInterno, linhaInicioMetodo);
                        break;
                    case "prototype":
                        // Exemplo de herança
                        System.out.println("Definindo prototype da classe " + nomeClasse);
                        break;
                    default:
                        System.out.println("Comando interno não reconhecido: " + comandoInterno);
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Erro ao processar classe: " + e.getMessage());
        }
    }

    // Método para criar uma nova instância de uma classe
    private static void criarNovaInstancia(GerenciadorDeClasses gerenciador, String nomeClasse, Arquivo arquivo) {
        // Lê a próxima instrução `store <name>` para obter o nome da instância
        String instrucao = arquivo.getNextInstruction();
        String[] partes = instrucao.trim().split(" ");
        if (!partes[0].equals("store") || partes.length < 2) {
            throw new RuntimeException("Erro: Esperado 'store <nomeInstancia>' após 'new " + nomeClasse + "'");
        }
        String nomeInstancia = partes[1];
    
        // Cria a nova instância da classe e a adiciona ao gerenciador
        gerenciador.criarInstancia(nomeClasse, nomeInstancia);
    }


    // --------------------------------------------
    // Métodos auxiliares para manipulação de lógica
    // --------------------------------------------

    // Método para gerenciar uma condicional (if)
    
    private static void gerenciarCondicional(String[] partes, Pilha pilha, Arquivo arquivo) {
        int valor = pilha.pop();  // Retira o valor do topo da pilha para verificar a condição
        int linhasParaPular = Integer.parseInt(partes[1]);  // Número de linhas a pular caso falso
        boolean condicaoVerdadeira = (valor != 0);  // Se o valor é diferente de 0, a condição é verdadeira

        try {
            if (condicaoVerdadeira) {
                // Se verdadeiro, executar até encontrar "else" ou "end-if"
                String linha;
                while (!(linha = arquivo.getNextInstruction()).trim().equals("end-if")) {
                    // Se encontrar um "else <num>", precisa pular <num> linhas
                    if (linha.trim().startsWith("else")) {
                        String[] partesElse = linha.trim().split(" ");
                        int linhasParaPularNoElse = Integer.parseInt(partesElse[1]);

                        // Pula as linhas especificadas no "else <num>"
                        for (int i = 0; i < linhasParaPularNoElse; i++) {
                            arquivo.getNextInstruction();  // Ignora a linha
                        }
                        break;  // Sai do loop após pular o bloco "else"
                    }

                    // Executa a linha normalmente
                    Instruction.execute(linha, pilha, arquivo, null, "process");  // Passamos o ambiente e gerenciador conforme necessário
                }
            } else {
                // Se falso, pular o bloco correspondente a `linhasParaPular`
                for (int i = 0; i < linhasParaPular; i++) {
                    String linha = arquivo.getNextInstruction();  // Pula a linha
                    if (linha.trim().equals("else")) {
                        // Se encontra um "else" enquanto pula, executa o bloco "else" até "end-if"
                        while (!(linha = arquivo.getNextInstruction()).trim().equals("end-if")) {
                            Instruction.execute(linha, pilha, arquivo, null, "process");
                        }
                        break;  // Sai do loop após o "else" ser executado
                    }
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Erro ao processar condicional: " + e.getMessage());
        }
    }



    // Método para gerenciar comparadores
    private static void gerenciarComparador(String comparador, Pilha pilha) {
        int valor1 = pilha.pop();
        int valor2 = pilha.pop();

        boolean resultado = false;
        switch (comparador) {
            case "eq":
                resultado = valor1 == valor2;
                break;
            case "ne":
                resultado = valor1 != valor2;
                break;
            case "lt":
                resultado = valor1 < valor2;
                break;
            case "le":
                resultado = valor1 <= valor2;
                break;
            case "gt":
                resultado = valor1 > valor2;
                break;
            case "ge":
                resultado = valor1 >= valor2;
                break;
        }

        pilha.push(resultado ? 1 : 0);  // Empilha o resultado como 1 (true) ou 0 (false)
    }

    // Método para processar a instrução `load`
    private static void processarLoad(String name, Pilha pilha, GerenciadorDeClasses gerenciador, Arquivo arquivo, String ambiente) {
        try {
            // Primeiro, verifica se a variável está no Map local `variaveis`
            if (gerenciador.variavelExiste(name)) {
                int valor = gerenciador.obterValorVariavel(name);
                pilha.push(valor);
                return;
            }


            // Caso a variável não esteja no Map local, lê a próxima instrução
            String linha = arquivo.getNextInstruction();
            String[] partesLinha = linha.trim().split(" ");
            String comandoInterno = partesLinha[0];
            String nameInterno = partesLinha.length > 1 ? partesLinha[1] : null;


            if (name.equals("load")) {
                // Coloca o valor da variável de uma instância na pilha
                switch (comandoInterno) {
                    case "set":
                        // Implementa a lógica para `set`, atribuindo um novo valor
                        if (gerenciador.istanciaExiste(name)) {
                            VariavelObjeto variavelSet = gerenciador.obterInstancia(name);
                            int novoValor = pilha.pop();  // Pega o valor do topo da pilha
                            variavelSet.setVariavel(name, novoValor);  // Atribui o novo valor
                        } else {
                            throw new RuntimeException("Erro: Variável '" + nameInterno + "' não encontrada para `set`.");
                        }
                        break;

                    case "get":
                        // Implementa a lógica para `get`, empilhando o valor da variável
                        if (gerenciador.istanciaExiste(name)) {
                            VariavelObjeto variavelGet = gerenciador.obterInstancia(name);
                            pilha.push(variavelGet.getVariavel(nameInterno));  // Empilha o valor da variável
                        } else {
                            throw new RuntimeException("Erro: Variável '" + nameInterno + "' não encontrada para `get`.");
                        }
                        break;
                        
                    case "call":
                        executarCall(nameInterno, nameInterno, pilha, gerenciador, arquivo, name);
                        break;

                    //prototype
                    case "load":
                        String linha2 = arquivo.getNextInstruction();
                        String[] partesLinha2 = linha2.trim().split(" ");
                        String comandoInterno2 = partesLinha2[0];
                        String nameInterno2 = partesLinha2.length > 1 ? partesLinha2[1] : null;
                        
                        try {
                            if (comandoInterno2.equals("set") && "_prototype".equals(nameInterno2)) {
                                // Obtém as variáveis
                                VariavelObjeto variavelPrototype = gerenciador.obterInstancia(nameInterno);
                                VariavelObjeto variavelAlvo = gerenciador.obterInstancia(name);
                            
                                // Define o prototype
                                variavelPrototype.setPrototype(variavelAlvo);
                            }
                        } catch (RuntimeException e) {
                            // Lida com exceções de forma a informar o que deu errado
                            System.err.println("Erro ao definir prototype: " + e.getMessage());
                        } catch (Exception e) {
                            // Captura qualquer outra exceção que possa ocorrer
                            System.err.println("Erro inesperado: " + e.getMessage());
                        }

                    default:
                        throw new RuntimeException("Erro: Comando não reconhecido '" + comandoInterno + "'");

                    }

                return;
            }

            if (name.equals("self")) {

                switch (comandoInterno) {
                    case "call":
                        executarCall(nameInterno, nameInterno, pilha, gerenciador, arquivo, ambiente);
                        break;

                    case "set":
                        // Implementa a lógica para `set`, atribuindo um novo valor
                        if (gerenciador.istanciaExiste(ambiente)) {
                            VariavelObjeto variavelSet = gerenciador.obterInstancia(ambiente);
                            int novoValor = pilha.pop();  // Pega o valor do topo da pilha
                            variavelSet.setVariavel(name, novoValor);  // Atribui o novo valor
                        } else {
                            throw new RuntimeException("Erro: Variável '" + nameInterno + "' não encontrada para `set`.");
                        }
                        break;

                    case "get":
                        // Implementa a lógica para `get`, empilhando o valor da variável
                        if (gerenciador.istanciaExiste(ambiente)) {
                            VariavelObjeto variavelGet = gerenciador.obterInstancia(ambiente);
                            pilha.push(variavelGet.getVariavel(nameInterno));  // Empilha o valor da variável
                        } else {
                            throw new RuntimeException("Erro: Variável '" + nameInterno + "' não encontrada para `get`.");
                        }
                        break;

                    default:
                        throw new RuntimeException("Erro: Comando não reconhecido '" + comandoInterno + "'");
                }
            }

        } catch (RuntimeException e) {
            System.err.println("Erro ao processar 'load': " + e.getMessage());
        }
    }

    // Método para processar a instrução `load`
    private static void processarStore(String name, Pilha pilha, GerenciadorDeClasses gerenciador) {
        try {
            Integer valor = pilha.pop();
            gerenciador.atualizarVariavel(name, valor);
        } catch (RuntimeException e) {
            System.err.println("Erro ao processar 'store': " + e.getMessage());
        }
    }

    // Método para processar `call`
    private static void executarCall(String nameInstancia, String nameMetodo, Pilha pilha, GerenciadorDeClasses gerenciador, Arquivo arquivo, String ambiente) {
        try {
            // Obtém a instância e a linha de início do método
            VariavelObjeto instancia = gerenciador.obterInstancia(nameInstancia);
            int linhaInicialMetodo = instancia.getMetodoStartLine(nameMetodo, instancia);

            // Obtém o número de parâmetros do método
            String[] partesMetodo = arquivo.getInstructionAtLine(linhaInicialMetodo).trim().split(" ");
            String[] parametros = partesMetodo[1].split("\\(");
            String listaParametros = parametros[1].replace(")", "");

            // Divide os parâmetros em um array
            String[] nomesParametros = listaParametros.split(",\\s*");

            // Processa os valores para cada parâmetro
            for (String nomeParam : nomesParametros) {
                // Remove espaços em branco e cria a variável
                nomeParam = nomeParam.trim();

                // Desempilha o valor correspondente para o parâmetro
                Integer valor = pilha.pop();  // Presumindo que a pilha retorna um valor Integer

                // Adiciona a variável ao gerenciador
                gerenciador.adicionarVariavel(nomeParam, valor);
            }

            // Processa as instruções do método até encontrar `end-method`
            String linha;
            int currentLine = linhaInicialMetodo + 1;  // Pula a linha do cabeçalho do método
            while (!(linha = arquivo.getInstructionAtLine(currentLine)).trim().equals("end-method")) {
                Instruction.execute(linha, pilha, arquivo, gerenciador, nameInstancia);
                
                currentLine++;  // Avança para a próxima linha do método
            }
        } catch (RuntimeException e) {
            System.err.println("Erro ao processar 'call': " + e.getMessage());
        }
    }

    // Método para iniciar a execução da função main
    private static void executarMain(Pilha pilha, GerenciadorDeClasses gerenciador, Arquivo arquivo) {
        String linha;
        while (!(linha = arquivo.getNextInstruction()).trim().equals("end")) {
            Instruction.execute(linha, pilha, arquivo, gerenciador, "main");
        }
    }
}
