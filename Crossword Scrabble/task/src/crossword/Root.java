package crossword;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Root {

    @JsonProperty("puzzles")
    private List<Puzzle> puzzles;

    // Getters and setters
    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

}
