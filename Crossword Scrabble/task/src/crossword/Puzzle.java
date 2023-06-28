package crossword;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Puzzle {
    @JsonProperty("letter")
    private String letter;
    private List<Word> words;

    public Iterable<? extends Word> getWords() {
        return words;
    }

    public String getLetter() {
        return letter;
    }

    public static class Word {
        @JsonProperty("word")
        private String word;
        @JsonProperty("position")
        private String position;
        @JsonProperty("direction")
        private String direction;
        @JsonProperty("hint")
        private String hint;

        public String getWord() {
            return word;
        }

        public String getPosition() {
            return position;
        }

        public String getDirection() {
            return direction;
        }

        public String getHint() {
            return hint;
        }
    }
}