package crossword;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Scanner sc = new Scanner(System.in).useDelimiter("\n");
    private static final String labels = "123456789ABCDEF";
    private static final String EMPTY = " ", BLACK = "#";
    private static final int size = labels.length();
    private static final String[][] grid = new String[size][size];
    private static List<String> tray = new ArrayList<>();
    private static final Random random = new Random();
    private static List<Puzzle> puzzles = new ArrayList<>();
    private static Puzzle currentPuzzle;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please provide the name of the crossword puzzle file as a command-line argument.");
            System.exit(0);
        }
        String filePath = args[0];
        System.out.println("Welcome to Crossword Scrabble");

        ObjectMapper mapper = new ObjectMapper();
        Root root = mapper.readValue(new File(filePath), Root.class); // Read and parse the JSON file

        puzzles = root.getPuzzles(); // get puzzles list from the root object
        prepareGrid();
        loadNewPuzzle();
        gameErrorInfo("");

        while (true) {
            System.out.println("\nWhat would you like to do?\nA. Place\nB. Hint\nC. New Puzzle\nD. Exit\n");
            String input = sc.nextLine().trim();
            String[] inputWords = input.split("\\s+");
            if (inputWords.length > 0) {
                String action = inputWords[0].toLowerCase();
                switch (action) {
                    case "a", "place" -> {
                        if (inputWords.length >= 5) {
                            String word = inputWords[1].toUpperCase();
                            try {
                                int row = Integer.parseInt(inputWords[2], 16);
                                int col = Integer.parseInt(inputWords[3], 16);
                                String direction = inputWords[4];

                                if (!direction.matches("ACR|DWN|acr|dwn")) {
                                    gameErrorInfo("\nInvalid direction.");
                                    continue;
                                }

                                if (row < 1 || row > 15 || col < 1 || col > 15) {
                                    gameErrorInfo("\nInvalid position. Position out of range.");
                                    continue;
                                }

                                //if the word contains letters that are not in the tray, print "Invalid letter." and continue
                                if (!new HashSet<>(tray).containsAll(Arrays.asList(word.split("")))) {
                                    gameErrorInfo("\nInvalid letter.");
                                    continue;
                                }
                                placeWord(word, row, col, direction);

                            } catch (NumberFormatException e) {
                                gameErrorInfo("\nInvalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.");
                            }
                        } else {
                            gameErrorInfo("\nInvalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.");
                        }
                    }

                    case "b", "hint" -> {
                        if (inputWords.length >= 4) {
                            try {
                                int row = Integer.parseInt(inputWords[1], 16);
                                int col = Integer.parseInt(inputWords[2], 16);
                                String direction = inputWords[3];
                                printHint(row, col, direction);
                            } catch (NumberFormatException e) {
                                gameErrorInfo("\nInvalid command. Use 'Hint row(Hexadecimal) column(Hexadecimal) DIRECTION'.");
                            }
                        } else {
                            gameErrorInfo("\nInvalid command. Use 'Hint row(Hexadecimal) column(Hexadecimal) DIRECTION'.");
                        }
                    }

                    case "c", "new", "puzzle" -> {
                        loadNewPuzzle();
                        gameErrorInfo("");
                    }

                    case "d", "exit" -> {
                        System.out.println("\nBye.");
                        System.exit(0);
                    }

                    default -> {
                        gameErrorInfo("\nInvalid command.");
                    }
                }
            }
        }

    }

    private static void gameErrorInfo(String errorMessage) {
        printGrid();
        System.out.println(errorMessage);
        if (!tray.isEmpty()) {
            System.out.printf("\n%s: %s\n", "Tray", String.join("", tray));
        }
    }

    private static void loadNewPuzzle() {
        if (puzzles.isEmpty()) {
            System.out.println("No more puzzles. Bye.");
            System.exit(0);
        }
        prepareGrid();
        currentPuzzle = puzzles.remove(random.nextInt(puzzles.size()));
        //set the tray
        tray = Arrays.asList(currentPuzzle.getLetter().toUpperCase().split(""));
        prepareGrid();
        markWordSpaces();
    }


    private static void markWordSpaces() {
        for (Puzzle.Word word : currentPuzzle.getWords()) {
            String[] pos = word.getPosition().split(" ");
            // Subtract 1 from the row and column indices to convert from 1-based indexing to 0-based indexing
            int row = Integer.parseInt(pos[0]) - 1;
            int col = Integer.parseInt(pos[1]) - 1;
            String direction = word.getDirection();

            if ("ACR".equalsIgnoreCase(direction)) {
                for (int j = 0; j < word.getWord().length(); j++) {
                    grid[row][col + j] = EMPTY;
                }
            } else {
                for (int j = 0; j < word.getWord().length(); j++) {
                    grid[row + j][col] = EMPTY;
                }
            }
        }
    }

    private static void placeWord(String word, int row, int col, String direction) {

        int wordLength = word.length();
        // convert from 1-based to 0-based indexing
        row--;
        col--;

        int availableSpaces = getAvailableSpaces(row, col, direction);
        if (wordLength > availableSpaces) {
            gameErrorInfo("Incorrect word. The word is too long.");
            return;
        } else if (wordLength < availableSpaces) {
            gameErrorInfo("The word is shorter than expected.");
            return;
        }
        for (Puzzle.Word puzzleWord : currentPuzzle.getWords()) {
            if (puzzleWord.getWord().equalsIgnoreCase(word)) {

                if (direction.equalsIgnoreCase("ACR")) {
                    for (int j = 0; j < wordLength; j++) {
                        grid[row][col + j] = String.valueOf(word.charAt(j));
                    }
                } else {
                    for (int j = 0; j < wordLength; j++) {
                        grid[row + j][col] = String.valueOf(word.charAt(j));
                    }
                }
                gameErrorInfo("");

                // Check if the puzzle is solved
                if (isPuzzleSolved()) {
                    System.out.println("Congratulations! You have solved the puzzle. Would you like to load another puzzle? Yes/No");
                    String response = sc.nextLine().trim().toLowerCase();
                    if (response.equals("yes")) {
                        loadNewPuzzle();
                        gameErrorInfo("");
                    } else {
                        System.out.println("Bye.");
                        System.exit(0);
                    }
                }


                return;
            }
        }

        System.out.println("Wrong word, please try again.");
        // Display the hint for the word
        printHint(row, col, direction);

    }


    private static int getAvailableSpaces(int row, int col, String direction) {
        int availableSpaces = 0;
        if (direction.equalsIgnoreCase("ACR")) {

            while (col < size && !grid[row][col].equals(BLACK)) {
                availableSpaces++;
                col++;
            }
        } else { // direction is down
            while (row < size && !grid[row][col].equals(BLACK)) {
                availableSpaces++;
                row++;
            }
        }
        return availableSpaces;
    }


    private static void printHint(int row, int col, String direction) {
        for (Puzzle.Word puzzleWord : currentPuzzle.getWords()) {
            String[] position = puzzleWord.getPosition().split(" ");
            if (Integer.parseInt(position[0]) == row && Integer.parseInt(position[1]) == col && puzzleWord.getDirection().equalsIgnoreCase(direction)) {
                System.out.println(puzzleWord.getHint());
                return;
            }
        }
        System.out.println("No word found at the specified position and direction.");
    }

    private static void printGrid() {
        System.out.println();
        for (int i = 0; i <= size; i++)
            System.out.print(i == 0 ? "  " : i == size ? labels.charAt(i - 1) + "\n" : labels.charAt(i - 1) + " ");
        for (int row = 0; row < size; row++) {
            System.out.print(labels.charAt(row));
            for (int col = 0; col < size; col++) {
                System.out.printf("%2s", grid[row][col]);
                if (col == size - 1) System.out.println();
            }
        }
    }

    private static void prepareGrid() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = BLACK;
            }
        }
    }

    private static boolean isPuzzleSolved() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col].equals(EMPTY)) {
                    return false;
                }
            }
        }
        return true;
    }

}

