public class NQueens {

    public static void preencherDiagonais(int[][] tab, int y, int x) {
        tab[y][x] = 2;

        int de1x = x, de1y = y;
        int de2x = x, de2y = y;
        int dd1x = x, dd1y = y;
        int dd2x = x, dd2y = y;

        int[] diagonaisPreenchidas = { 0, 0, 0, 0 };

        while (true) {
            // diagonal esquerda cima
            if (de1x > 0 && de1y > 0) {
                de1x--;
                de1y--;
                tab[de1y][de1x] = 1;
            } else {
                diagonaisPreenchidas[0] = 1;
            }

            // diagonal direita baixo
            if (de2x < tab.length - 1 && de2y < tab.length - 1) {
                de2x++;
                de2y++;
                tab[de2y][de2x] = 1;
            } else {
                diagonaisPreenchidas[1] = 1;
            }

            // diagonal direita cima
            if (dd1x < tab[0].length - 1 && dd1y > 0) {
                dd1x++;
                dd1y--;
                tab[dd1y][dd1x] = 1;
            } else {
                diagonaisPreenchidas[2] = 1;
            }

            // diagonal esquerda baixo
            if (dd2x > 0 && dd2y < tab.length - 1) {
                dd2x--;
                dd2y++;
                tab[dd2y][dd2x] = 1;
            } else {
                diagonaisPreenchidas[3] = 1;
            }

            if (diagonaisPreenchidas[0] == 1 &&
                    diagonaisPreenchidas[1] == 1 &&
                    diagonaisPreenchidas[2] == 1 &&
                    diagonaisPreenchidas[3] == 1) {
                break;
            }
        }
    }

    public static void preencherVerticalHorizontal(int[][] tab, int y, int x) {
        // vertical
        for (int i = 0; i < tab.length; i++) {
            tab[i][x] = 1;
        }

        // horizontal
        for (int j = 0; j < tab[0].length; j++) {
            tab[y][j] = 1;
        }
    }

    public static int funcaoHeuristica(int[][] tab) {
        int contVazia = 0;

        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                if (tab[i][j] == 0) {
                    contVazia++;
                }
            }
        }

        return contVazia;
    }

    public static void printTab(int[][] tab) {
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                System.out.print(tab[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] copiarMatriz(int[][] tab) {
        int[][] copia = new int[tab.length][tab[0].length];
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                copia[i][j] = tab[i][j];
            }
        }
        return copia;
    }

    public static void main(String[] args) {
        int qntRainhas = 8;
        int[][] tab = new int[qntRainhas][qntRainhas];
        int qntRainhasPosicionadas = 0;

        while (qntRainhasPosicionadas < qntRainhas) {
            int melhorLinha = -1;
            int melhorColuna = -1;
            int melhorHeuristica = Integer.MIN_VALUE;

            for (int i = 0; i < qntRainhas; i++) {
                for (int j = 0; j < qntRainhas; j++) {
                    if (tab[i][j] != 0) {
                        continue;
                    }

                    int[][] tabCopy = copiarMatriz(tab);
                    tabCopy[i][j] = 2;
                    preencherVerticalHorizontal(tabCopy, i, j);
                    preencherDiagonais(tabCopy, i, j);

                    int heuristicaAtual = funcaoHeuristica(tabCopy);

                    if (heuristicaAtual > melhorHeuristica) {
                        melhorHeuristica = heuristicaAtual;
                        melhorLinha = i;
                        melhorColuna = j;
                    }
                }
            }

            // não encontrou mais posição válida
            if (melhorLinha == -1 || melhorColuna == -1) {
                break;
            }

            // aplica no tabuleiro real
            preencherVerticalHorizontal(tab, melhorLinha, melhorColuna);
            preencherDiagonais(tab, melhorLinha, melhorColuna);
            tab[melhorLinha][melhorColuna] = 2;

            qntRainhasPosicionadas++;
        }

        printTab(tab);
        System.out.println("Heurística: " + funcaoHeuristica(tab));
        System.out.println("Quantidade rainhas colocadas: "+qntRainhasPosicionadas);
    }

}