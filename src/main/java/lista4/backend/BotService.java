package lista4.backend; // Upewnij się, że paczka się zgadza

import org.springframework.stereotype.Service;
import lista4.gameLogic.PlayerColor;
import java.math.*;

@Service
public class BotService {

    public String calculateBestMove(int[][] board, PlayerColor botColor) {
        int size = board.length;
        double[][] influenceMap = new double[size][size];

        // 1. Inicjalizacja mapy na podstawie kamieni
        // Czarny = 128, Biały = -128
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (board[x][y] == 1)
                    influenceMap[x][y] = 128; // Czarny
                else if (board[x][y] == 2)
                    influenceMap[x][y] = -128; // Biały
                // System.out.print("[" + influenceMap[x][y] + "]");
            }
            // System.out.print("\n");
        }

        // 2. Dylatacja (Rozprzestrzenianie wpływów) - 5 iteracji
        for (int iter = 0; iter < 8; iter++) {
            influenceMap = dilate(influenceMap);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[j][i] == 1)
                        influenceMap[j][i] = 128; // Czarny
                    else if (board[j][i] == 2)
                        influenceMap[j][i] = -128; // Biały
                    // System.out.print("[" + influenceMap[i][j] + "]");
                }
                // System.out.print("\n");
            }

        }

        // 3. Wybór ruchu: Bot szuka wolnego miejsca z największym wpływem dla siebie
        int bestX = -1, bestY = -1;
        double maxInfluence = -Double.MAX_VALUE;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[j][i] == 0) { // Tylko wolne pola
                    double val = (botColor == PlayerColor.BLACK) ? influenceMap[j][i] : -influenceMap[j][i];
                    if (val > maxInfluence) {
                        maxInfluence = val;
                        bestX = j;
                        bestY = i;
                    }
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print("[" + influenceMap[j][i] + "]");
            }
            System.out.print("\n");
        }

        return bestX + " " + bestY;
    }

    private double[][] dilate(double[][] map) {
        int size = map.length;
        double[][] newMap = new double[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Średnia z sąsiadów
                double sum = map[x][y];
                int count = 1;
                if (x > 0) {
                    sum += map[x - 1][y];
                    count++;
                }
                if (x < size - 1) {
                    sum += map[x + 1][y];
                    count++;
                }
                if (y > 0) {
                    sum += map[x][y - 1];
                    count++;
                }
                if (y < size - 1) {
                    sum += map[x][y + 1];
                    count++;
                }
                newMap[x][y] = Math.round(sum / count);
            }
        }
        return newMap;
    }
}