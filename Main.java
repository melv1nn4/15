import java.util.Scanner;
import java.io.*;

public class Main {
    static int boardSize = 3;
    static String playerX = "Гравець X";
    static String playerO = "Гравець O";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean codeIsRunning = true;

        loadConfig();

        while (codeIsRunning) {
            System.out.println("\n-- ГОЛОВНЕ МЕНЮ --");
            System.out.println("1 - Почати гру");
            System.out.println("2 - Налаштування");
            System.out.println("3 - Переглянути статистику");
            System.out.println("4 - Вихід");
            System.out.print("Ваш вибір: ");
            char inputSwitch = scanner.nextLine().charAt(0);

            if (inputSwitch == '1') {
                playGame(scanner);
            } else if (inputSwitch == '2') {
                settingsMenu(scanner);
            } else if (inputSwitch == '3') {
                showStatistics();
            } else if (inputSwitch == '4') {
                codeIsRunning = false;
            } else {
                System.out.println("Невірний вибір, спробуйте ще.");
            }
        }

        System.out.println("Гру завершено.");
        scanner.close();
    }

    static void playGame(Scanner scanner) {
        int rows = boardSize * 2 - 1;
        int cols = boardSize * 2 - 1;
        char[][] board = new char[rows][cols];
        char currentPlayer = 'X';
        boolean gameEnd = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i % 2 == 1) board[i][j] = '-';
                else if (j % 2 == 1) board[i][j] = '|';
                else board[i][j] = ' ';
            }
        }

        while (!gameEnd) {
            printBoard(board);

            System.out.print("Гравець " + (currentPlayer == 'X' ? playerX : playerO) + ", введіть рядок і колонку (від 1 до " + boardSize + "): ");
            String input = scanner.nextLine();
            if (input.length() == 1 && input.charAt(0) == '2') return;

            int row = -1, col = -1;
            boolean valid = false;

            while (!valid) {
                Scanner inputScanner = new Scanner(input);
                if (inputScanner.hasNextInt()) {
                    row = inputScanner.nextInt() - 1;
                    if (inputScanner.hasNextInt()) {
                        col = inputScanner.nextInt() - 1;
                        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                            row *= 2;
                            col *= 2;
                            if (board[row][col] == ' ') {
                                valid = true;
                            } else {
                                System.out.println("Клітинка зайнята. Введіть інші координати:");
                            }
                        } else {
                            System.out.println("Невірні координати. Спробуйте знову:");
                        }
                    } else {
                        System.out.println("Потрібно ввести два числа:");
                    }
                } else {
                    System.out.println("Потрібно ввести два числа:");
                }
                if (!valid) input = scanner.nextLine();
            }

            board[row][col] = currentPlayer;

            if (checkWin(board, currentPlayer)) {
                printBoard(board);
                System.out.println("Гравець " + (currentPlayer == 'X' ? playerX : playerO) + " переміг!");
                saveStatistics(currentPlayer);
                gameEnd = true;
            } else if (isBoardFull(board)) {
                printBoard(board);
                System.out.println("Нічия!");
                saveStatistics('D');
                gameEnd = true;
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        }
    }

    static void printBoard(char[][] board) {
        System.out.print("   ");
        for (int i = 0; i < boardSize; i++) System.out.print((i + 1) + " ");
        System.out.println();

        for (int i = 0; i < board.length; i++) {
            if (i % 2 == 0) System.out.print((i / 2 + 1) + " ");
            else System.out.print("  ");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    static boolean checkWin(char[][] board, char player) {
        for (int i = 0; i < boardSize; i++) {
            boolean rowWin = true, colWin = true;
            for (int j = 0; j < boardSize; j++) {
                if (board[i * 2][j * 2] != player) rowWin = false;
                if (board[j * 2][i * 2] != player) colWin = false;
            }
            if (rowWin || colWin) return true;
        }

        boolean diag1 = true, diag2 = true;
        for (int i = 0; i < boardSize; i++) {
            if (board[i * 2][i * 2] != player) diag1 = false;
            if (board[i * 2][(boardSize - 1 - i) * 2] != player) diag2 = false;
        }
        return diag1 || diag2;
    }

    static boolean isBoardFull(char[][] board) {
        for (int i = 0; i < board.length; i += 2)
            for (int j = 0; j < board[i].length; j += 2)
                if (board[i][j] == ' ') return false;
        return true;
    }

    static void settingsMenu(Scanner scanner) {
        System.out.print("Ім'я гравця X: ");
        playerX = scanner.nextLine();
        System.out.print("Ім'я гравця O: ");
        playerO = scanner.nextLine();
        System.out.println("Розмір поля: 3(1), 5(2), 7(3), 9(4): ");
        char input = scanner.nextLine().charAt(0);
        if (input == '1') boardSize = 3;
        else if (input == '2') boardSize = 5;
        else if (input == '3') boardSize = 7;
        else if (input == '4') boardSize = 9;

        saveConfig();
    }

    static void saveConfig() {
        try {
            FileWriter writer = new FileWriter("config.txt");
            writer.write(playerX + "\n");
            writer.write(playerO + "\n");
            writer.write(String.valueOf(boardSize) + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Помилка при збереженні конфігурації.");
        }
    }

    static void loadConfig() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("config.txt"));
            playerX = reader.readLine();
            playerO = reader.readLine();
            boardSize = Integer.parseInt(reader.readLine());
            reader.close();
        } catch (Exception e) {
        }
    }

    static void saveStatistics(char winner) {
        try {
            FileWriter writer = new FileWriter("stats.txt", true); // append
            String result;
            if (winner == 'D') result = "Нічия";
            else result = (winner == 'X' ? playerX : playerO) + " переміг";

            writer.write(result + ", розмір: " + boardSize + "x" + boardSize + ", дата: " + java.time.LocalDateTime.now() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Помилка при збереженні статистики.");
        }
    }

    static void showStatistics() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("stats.txt"));
            String line;
            System.out.println("\n--- ІСТОРІЯ ІГОР ---");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Статистика відсутня.");
        }
    }
}
