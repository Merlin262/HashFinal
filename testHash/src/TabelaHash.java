public class TabelaHash {
    private int tamanho;
    private ArvoreAVL[] tabela;

    public TabelaHash(int tamanho) {
        this.tamanho = tamanho;
        tabela = new ArvoreAVL[tamanho];
        for (int i = 0; i < tamanho; i++) {
            tabela[i] = new ArvoreAVL();
        }
    }

    public int divisao(Registro[] registros) {
        int codigo = 0;
        for (Registro registro : registros) {
            int chave = registro.getCodigo();
            codigo += chave;
        }

        if (codigo < 0) {
            // Se codigo for negativo, ajusta para um valor positivo
            codigo = -codigo;
        }

        return codigo % tamanho;
    }


    public int hashMultiplicacao(Registro[] registros) {
        double A = 0.61803398875; // Valor típico entre 0 e 1
        int codigoConcatenado = 0;

        // Concatena os códigos dos registros
        for (Registro registro : registros) {
            int codigo = registro.getCodigo();
            codigoConcatenado = concatenaCodigos(codigoConcatenado, codigo);
        }

        double parteFracionaria = codigoConcatenado * A - (int) (codigoConcatenado * A);
        int indice = (int) (tamanho * parteFracionaria);
        return indice;
    }

    private int concatenaCodigos(int codigoAtual, int novoCodigo) {
        // Concatena os códigos multiplicando o código atual por um valor grande
        // para criar um espaço entre os dígitos do novo código.
        return codigoAtual * 1000000 + novoCodigo;
    }

    public int hashDobramento(Registro[] registros) {
        int codigoConcatenado = 0;

        // Concatena os códigos dos registros
        for (Registro registro : registros) {
            int codigo = registro.getCodigo();
            codigoConcatenado = concatenaCodigos(codigoConcatenado, codigo);
        }

        int indice = 0;

        // Realiza o dobramento
        while (codigoConcatenado != 0) {
            indice += codigoConcatenado % 1000; // Soma grupos de 3 dígitos
            codigoConcatenado /= 1000;
        }

        return indice % tamanho;
    }

    public void inserir(Registro[] registros, String valor) {
        int indice = divisao(registros);
        tabela[indice].inserir(registros, valor);
    }

    public void inserirMultiplicacao(Registro[] registros, String valor) {
        int indice = hashMultiplicacao(registros);
        tabela[indice].inserir(registros, valor);
    }

    public void inserirDobramento(Registro[] registros, String valor) {
        int indice = hashDobramento(registros);
        tabela[indice].inserir(registros, valor);
    }

    public String buscar(Registro[] registros) {
        int indice = divisao(registros);
        return tabela[indice].buscar(registros);
    }

    public String buscarMultiplicacao(Registro[] registros) {
        int indice = hashMultiplicacao(registros);
        return tabela[indice].buscar(registros);
    }

    public String buscarDobramento(Registro[] registros) {
        int indice = hashDobramento(registros);
        return tabela[indice].buscar(registros);
    }

    public int obterColisoes() {
        int colisoes = 0;
        for (ArvoreAVL arvore : tabela) {
            colisoes += arvore.obterColisoes();
        }
        return colisoes;
    }

    public static void main(String[] args) {
        int[] tamanhosTabela = {10, 100, 1000, 10000, 100000};
        int[] numConjuntosDados = {20000, 100000, 500000, 1000000, 5000000};

        for (int tamanho : tamanhosTabela) {
            for (int numConjuntos : numConjuntosDados) {
                System.out.println("Testando com tamanho de tabela " + tamanho + " e " + numConjuntos + " conjuntos de dados:");

                // Criar uma tabela divisao com o tamanho atual
                TabelaHash divisao = new TabelaHash(tamanho);
                TabelaHash multiplicacao = new TabelaHash(tamanho);
                TabelaHash dobramento = new TabelaHash(tamanho);

                // Criar diferentes conjuntos de dados usando o método gerarDados
                Registro[] conjuntoDados = Registro.gerarDados(numConjuntos);

                long inicioInsercao = System.nanoTime(); // Marcar o início da inserção

                // Inserir o conjunto de dados na tabela
                for (Registro registro : conjuntoDados) {
                    divisao.inserir(new Registro[]{registro}, "valor_tamanho" + tamanho);
                }

                long fimInsercao = System.nanoTime(); // Marcar o fim da inserção
                long duracaoInsercao = fimInsercao - inicioInsercao; // Calcular a duração da inserção em nanossegundos

                System.out.println("Tempo de inserção para conjunto de dados com tamanho de tabela " + tamanho + ": " + duracaoInsercao + " nanossegundos.");
                System.out.println("Número de colisões durante a inserção (Divisão): " + divisao.obterColisoes());

                long inicioMultiplicacao = System.nanoTime(); // Marcar o início da inserção com divisao por multiplicação

                // Inserir o conjunto de dados na tabela usando a função de divisao por multiplicação
                for (Registro registro : conjuntoDados) {
                    multiplicacao.inserirMultiplicacao(new Registro[]{registro}, "valor_tamanho" + tamanho);
                }

                long fimMultiplicacao = System.nanoTime(); // Marcar o fim da inserção com divisao por multiplicação
                long duracaoInsercaoMultiplicacao = fimMultiplicacao - inicioMultiplicacao; // Calcular a duração da inserção em nanossegundos

                System.out.println("Tempo de inserção para conjunto de dados com tamanho de tabela " + tamanho + " (Multiplicação): " + duracaoInsercaoMultiplicacao + " nanossegundos.");
                System.out.println("Número de colisões durante a inserção (Multiplicação): " + multiplicacao.obterColisoes());

                long inicioDobramento = System.nanoTime(); // Marcar o início da inserção com divisao por dobramento

                // Inserir o conjunto de dados na tabela usando a função de divisao por dobramento
                for (Registro registro : conjuntoDados) {
                    dobramento.inserirDobramento(new Registro[]{registro}, "valor_tamanho" + tamanho);
                }

                long fimDobramento = System.nanoTime(); // Marcar o fim da inserção com divisao por dobramento
                long duracaoInsercaoDobramento = fimDobramento - inicioDobramento; // Calcular a duração da inserção em nanossegundos

                System.out.println("Tempo de inserção para conjunto de dados com tamanho de tabela " + tamanho + " (Dobramento): " + duracaoInsercaoDobramento + " nanossegundos.");
                System.out.println("Número de colisões durante a inserção (Dobramento): " + dobramento.obterColisoes());

                // Buscar elementos no conjunto de dados
                long duracaoBuscaTotalDiv = 0;

                for (Registro registro : conjuntoDados) {
                    long inicioBusca = System.nanoTime(); // Marcar o início da busca
                    // Realizar a busca
                    divisao.buscar(new Registro[]{registro});
                    long fimBusca = System.nanoTime(); // Marcar o fim da busca
                    long duracaoBusca = fimBusca - inicioBusca; // Calcular a duração da busca em nanossegundos
                    duracaoBuscaTotalDiv += duracaoBusca;
                }

                // Calcular e imprimir o tempo médio de busca
                long duracaoBuscaMedia = duracaoBuscaTotalDiv / numConjuntos;
                System.out.println("---------------------------------------");
                System.out.println("Tempo médio de busca para conjunto de dados com tamanho de tabela (Divisão) " + tamanho + ": " + duracaoBuscaMedia + " nanossegundos.");
                System.out.println("---------------------------------------");

                long duracaoBuscaTotalMul = 0;

                for (Registro registro : conjuntoDados) {
                    long inicioBusca = System.nanoTime(); // Marcar o início da busca
                    // Realizar a busca
                    multiplicacao.buscarMultiplicacao(new Registro[]{registro});
                    long fimBusca = System.nanoTime(); // Marcar o fim da busca
                    long duracaoBusca = fimBusca - inicioBusca; // Calcular a duração da busca em nanossegundos
                    duracaoBuscaTotalMul += duracaoBusca;
                }

                // Calcular e imprimir o tempo médio de busca
                long duracaoBuscaMediamul = duracaoBuscaTotalMul / numConjuntos;
                System.out.println("Tempo médio de busca para conjunto de dados com tamanho de tabela (Multiplicação) " + tamanho + ": " + duracaoBuscaMediamul + " nanossegundos.");
                System.out.println("---------------------------------------");

                long duracaoBuscaTotalDob = 0;

                for (Registro registro : conjuntoDados) {
                    long inicioBusca = System.nanoTime(); // Marcar o início da busca
                    // Realizar a busca
                    dobramento.buscarDobramento(new Registro[]{registro});
                    long fimBusca = System.nanoTime(); // Marcar o fim da busca
                    long duracaoBusca = fimBusca - inicioBusca; // Calcular a duração da busca em nanossegundos
                    duracaoBuscaTotalDob += duracaoBusca;
                }

                // Calcular e imprimir o tempo médio de busca
                long duracaoBuscaMediaDob = duracaoBuscaTotalDob / numConjuntos;
                System.out.println("Tempo médio de busca para conjunto de dados com tamanho de tabela (Dobramento) " + tamanho + ": " + duracaoBuscaMediaDob + " nanossegundos.");
                System.out.println("---------------------------------------");
            }
        }
    }
}

class ArvoreAVL {
    private No raiz;
    private int colisoes; // Contador de colisões

    public ArvoreAVL() {
        raiz = null;
        colisoes = 0;
    }

    public void inserir(Registro[] registros, String valor) {
        raiz = inserirRec(raiz, registros, valor);
    }

    private No inserirRec(No no, Registro[] registros, String valor) {
        if (no == null) {
            return new No(registros, valor);
        }

        if (registros.hashCode() < no.registros.hashCode()) {
            no.esquerda = inserirRec(no.esquerda, registros, valor);
        } else if (registros.hashCode() > no.registros.hashCode()) {
            no.direita = inserirRec(no.direita, registros, valor);
        } else {
            colisoes++; // Incrementa o contador de colisões
            return no;  // Retorna o nó sem alterações em caso de colisão
        }

        // Atualiza a altura do nó atual
        no.altura = 1 + Math.max(getAltura(no.esquerda), getAltura(no.direita));

        // Calcula o fator de balanceamento
        int balanceamento = getBalanceamento(no);

        // Realiza as rotações conforme necessário para manter a propriedade AVL
        if (balanceamento > 1) {
            if (registros.hashCode() < no.esquerda.registros.hashCode()) {
                return rotacaoDireita(no);
            } else {
                no.esquerda = rotacaoEsquerda(no.esquerda);
                return rotacaoDireita(no);
            }
        }
        if (balanceamento < -1) {
            if (registros.hashCode() > no.direita.registros.hashCode()) {
                return rotacaoEsquerda(no);
            } else {
                no.direita = rotacaoDireita(no.direita);
                return rotacaoEsquerda(no);
            }
        }

        return no;
    }

    private int getAltura(No no) {
        if (no == null) {
            return -1;
        }
        return no.altura;
    }

    private int getBalanceamento(No no) {
        if (no == null) {
            return 0;
        }
        return getAltura(no.esquerda) - getAltura(no.direita);
    }

    private No rotacaoDireita(No y) {
        if (y == null || y.esquerda == null) {
            return y;
        }

        No x = y.esquerda;
        No T2 = x.direita;

        // Realiza a rotação
        x.direita = y;
        y.esquerda = T2;

        // Atualiza alturas
        y.altura = 1 + Math.max(getAltura(y.esquerda), getAltura(y.direita));
        x.altura = 1 + Math.max(getAltura(x.esquerda), getAltura(x.direita));

        return x;
    }

    private No rotacaoEsquerda(No x) {
        if (x == null || x.direita == null) {
            return x;
        }

        No y = x.direita;
        No T2 = y.esquerda;

        // Realiza a rotação
        y.esquerda = x;
        x.direita = T2;

        // Atualiza alturas
        x.altura = 1 + Math.max(getAltura(x.esquerda), getAltura(x.direita));
        y.altura = 1 + Math.max(getAltura(y.esquerda), getAltura(y.direita));

        return y;
    }


    public String buscar(Registro[] registros) {
        return buscarRec(raiz, registros);
    }

    private String buscarRec(No no, Registro[] registros) {
        if (no == null || no.registros.hashCode() == registros.hashCode()) {
            return no != null ? no.valor : null;
        }

        if (registros.hashCode() < no.registros.hashCode()) {
            return buscarRec(no.esquerda, registros);
        }

        return buscarRec(no.direita, registros);
    }

    public int obterColisoes() {
        return colisoes;
    }

    class No {
        Registro[] registros;
        String valor;
        No esquerda, direita;
        int altura;

        public No(Registro[] registros, String valor) {
            this.registros = registros;
            this.valor = valor;
            this.altura = 1; // Inicializa a altura como 1 para o novo nó
        }
    }
}
