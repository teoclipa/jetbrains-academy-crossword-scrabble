# Crossword Scrabble Game

Crossword Scrabble is a command-line based game where players solve crossword puzzles in an interactive, fun, and challenging manner. The game offers users the ability to place words on a board, receive hints to help solve the puzzle, load new puzzles, or exit the game. 

## Features

1. **Puzzle Loading**: The game loads crossword puzzles from a provided JSON file. Each puzzle consists of words, their positions on the grid, their direction (across or down), and the hints associated with each word. 

2. **Word Placement**: Users can place words on the crossword puzzle by specifying the word, its starting position (row and column in hexadecimal format), and direction (across or down). If the word fits correctly, it will be placed; if not, an error message will be displayed.

3. **Hints**: At any point, users can request a hint for a particular word by specifying the starting position and direction of the word. The game will then display the hint associated with that word.

4. **New Puzzle**: Users can load a new puzzle at any time. The game will randomly select a new puzzle from the remaining puzzles in the JSON file.

5. **Exit**: Users can exit the game whenever they want.

6. **Completion Check**: After each word placement, the game checks if the puzzle is solved. If so, it congratulates the user and asks if they want to load another puzzle or exit the game.

## Requirements

The game requires Java (version 8 or later) and the Jackson library to parse JSON files.

## How to Run

1. Compile the Java files using a Java compiler.
2. Run the `Main` class and provide the name of the JSON file containing the puzzles as a command-line argument.

## Future Enhancements

Potential future enhancements include adding a scoring system, timing how long it takes to solve each puzzle, or creating a graphical user interface to make the game more visually engaging.
