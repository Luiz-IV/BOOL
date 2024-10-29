package BOOL.Interpretador;
import java.util.Stack;

public class Pilha {
    private Stack<Integer> stack;

    public Pilha() {
        stack = new Stack<>();
    }

    // Adiciona um valor no topo da pilha
    public void push(int value) {
        stack.push(value);
    }

    // Remove e retorna o valor do topo da pilha
    public int pop() {
        if (stack.isEmpty()) {
            throw new RuntimeException("Erro: Pilha vazia!");
        }
        return stack.pop();
    }

    // Retorna o valor no topo da pilha sem remover
    public int peek() {
        if (stack.isEmpty()) {
            throw new RuntimeException("Erro: Pilha vazia!");
        }
        return stack.peek();
    }

    // Verifica se a pilha está vazia
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Retorna o tamanho da pilha
    public int size() {
        return stack.size();
    }
}
