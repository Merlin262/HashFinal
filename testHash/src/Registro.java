import java.util.Random;

public class Registro {
    private int codigo;

    public Registro(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static Registro[] gerarDados(int tamanho) {
        Registro[] dados = new Registro[tamanho];
        Random random = new Random();

        for (int i = 0; i < tamanho; i++) {
            int codigo = random.nextInt(900000000) + 100000000; // Gera códigos de 9 dígitos
            Registro registro = new Registro(codigo);
            dados[i] = registro;
        }

        return dados;
    }
}
