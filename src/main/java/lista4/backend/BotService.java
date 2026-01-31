package lista4.backend; // Upewnij się, że paczka się zgadza

import org.springframework.stereotype.Service;
import lista4.gameLogic.PlayerColor;
import java.math.*;

/**
 * Service responsible for calculating the bot's moves.
 * It uses an influence map algorithm to evaluate territory control on the board
 * and select the most strategic position.
 */
@Service
public class BotService {

    /**
     * Calculates the best move for the bot based on the current board state.
     *
     * The algorithm operates in the following steps:
     * 1. Influence Map Initialization: Black stones are assigned a value of 128,
     * and White stones a value of -128.
     * 2. Dilation (Blurring): The influence is spread to neighboring cells over
     * 8 iterations. After each iteration, the values of occupied cells are
     * reset to their maximum to act as permanent sources of influence.
     * 3. Move Selection: The bot scans for an empty cell with the highest
     * influence value favorable to its color.
     *
     * @param board    A 2D integer array representing the board, where 0 is empty,
     *                 1 is a black stone, and 2 is a white stone.
     * @param botColor The color of the player the bot is simulating (BLACK or
     *                 WHITE).
     * @return A string representing the coordinates of the best move in "x y"
     *         format.
     */
    public String calculateBestMove(int[][] board, PlayerColor botColor) {
        int size = board.length;
        double[][] influenceMap = new double[size][size];

        // 1. Map initialization based on stones
        // Black = 128, White = -128
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

        // 2. Dylatacja - 5 iteracji
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

        // 3. Move selection: Bot looks for an empty spot with the highest influence
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
        // Debugging output
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print("[" + influenceMap[j][i] + "]");
            }
            System.out.print("\n");
        }

        return bestX + " " + bestY;
    }

    /**
     * Performs a single iteration of dilation (blurring) on the influence map.
     *
     * Calculates the new value of each cell as the average of the cell itself
     * and its immediate neighbors (up, down, left, right).
     * The result is rounded to the nearest integer.
     *
     * @param map The current influence map.
     * @return A new 2D array containing the processed map values.
     */
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