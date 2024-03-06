package todolist.keywordfinder;
import java.time.LocalDateTime;

import todolist.task.DeadLinesTask;
import todolist.task.EventsTask;
import todolist.task.Task;
import todolist.task.ToDoTask;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordPatternMatcher {

    private final String inputText;
    private final Keyword keywordType;

    //CORRECT PATTERN
    private static final String MARK_FEATURE_PATTERN = "^mark \\d+$"; //mark
    private static final String UNMARK_FEATURE_PATTERN = "^unmark \\d+$"; //unmark
    private static final String DELETE_TASK_FEATURE_PATTERN = "^delete \\d+$"; //delete
    private static final String FIND_TASK_FEATURE_PATTERN = "^find .+"; //find
    private static final String TODOTASK_FEATURE_PATTERN = "^todo .+"; //todoItem
    private static final String DEADLINESTASK_FEATURE_PATTERN = "^deadline .*"; //todoItem
    private static final String EVENTSTASK_PATTERN = "^event .*"; //todoItem
    //INCORRECT PATTERN
    private static final String TODOTASK_FEATURE_PATTERN_INCORRECT = "^todo\\s+$";

    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HH:mm:ss"; //date time pattern

    private static final int SPACE_OFFSET = 1; //keyword " " offset
    private static final int BY_OFFSET = 4; //keyword "/by" offset
    private static final int FROM_OFFSET = 6; //keyword "/from" offset
    private static final int TO_OFFSET = 4; //keyword "/to" offset

    /**
     * Constructor for KeywordPatternMatcher
     * @param input the input string
     */
    public KeywordPatternMatcher(String input) {
        this.inputText = input;
        this.keywordType = checkKeywordType();
    }

    /**
     * Get the keyword type
     * @return the keyword type
     */
    public Keyword getKeywordType() {
        return keywordType;
    }

    /**
     * Get the position of the space character
     * @param inputText the input string
     * @return the position of the space character
     */
    private int getSpaceCharacterPosition(String inputText) {
        return inputText.indexOf(" ");
    }

    /**
     * Check the keyword type
     * @return the keyword type
     */
    public Keyword checkKeywordType() {
        if (matchesPattern(this.inputText, MARK_FEATURE_PATTERN)) {
            return Keyword.mark;
        } else if (matchesPattern(this.inputText, UNMARK_FEATURE_PATTERN)) {
            return Keyword.unmark;
        } else if (matchesPattern(this.inputText, DEADLINESTASK_FEATURE_PATTERN)) {
            return Keyword.deadline;
        } else if (matchesPattern(this.inputText, EVENTSTASK_PATTERN)) {
            return Keyword.event;
        } else if (matchesPattern(this.inputText, TODOTASK_FEATURE_PATTERN_INCORRECT)) {
            return Keyword.todoError;
        } else if (matchesPattern(this.inputText, TODOTASK_FEATURE_PATTERN)) {
            return Keyword.todo;
        } else if (matchesPattern(this.inputText, DELETE_TASK_FEATURE_PATTERN)) {
            return Keyword.delete;
        } else if (matchesPattern(this.inputText,FIND_TASK_FEATURE_PATTERN)) {
          return Keyword.find;
        } else {
            return Keyword.none;
        }
    }

    /**
     * Find the search input
     * @return the search input
     */
    public String findSearchInput() {
        int spacePosition = getSpaceCharacterPosition(this.inputText);
        return this.inputText.substring(spacePosition + SPACE_OFFSET);
    }

    /**
     * Find the number index
     * @return the number index
     */
    public int findNumberIndex() {
        int spacePosition = getSpaceCharacterPosition(this.inputText);
        return Integer.parseInt(this.inputText.substring(spacePosition + SPACE_OFFSET));
    }

    /**
     * Process the keyword input
     * @return the task
     */
    public Task processKeywordInput() {
        int spacePosition = getSpaceCharacterPosition(this.inputText);
        try {
            if (this.keywordType == Keyword.todo) {
                return new ToDoTask(this.inputText.substring(spacePosition + SPACE_OFFSET));
            } else if (this.keywordType == Keyword.deadline) {
                int keywordPosition = this.inputText.indexOf("/by");
                String name = this.inputText.substring(spacePosition + SPACE_OFFSET, keywordPosition - 1);
                String deadline = this.inputText.substring(keywordPosition + BY_OFFSET);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
                LocalDateTime parsedDateTime = LocalDateTime.parse(deadline, formatter);
                return new DeadLinesTask(name, parsedDateTime);
            } else {
                int keywordPosition1 = this.inputText.indexOf("/from"); // check /from keyword position
                int keywordPosition2 = this.inputText.indexOf("/to"); // check /to keyword position
                String name = this.inputText.substring(spacePosition + SPACE_OFFSET, keywordPosition1 - 1);
                String startDateTime = this.inputText.substring(keywordPosition1 + FROM_OFFSET, keywordPosition2 - 1);
                String endDateTime = this.inputText.substring(keywordPosition2 + TO_OFFSET);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
                LocalDateTime parsedStartDateTime = LocalDateTime.parse(startDateTime, formatter);
                LocalDateTime parsedEndDateTime = LocalDateTime.parse(endDateTime, formatter);
                return new EventsTask(this.inputText.substring(spacePosition + SPACE_OFFSET, keywordPosition1 - 1),
                        parsedStartDateTime, parsedEndDateTime);
            }
        } catch (StringIndexOutOfBoundsException | DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Check if the input matches the pattern
     * @param input the input string
     * @param pattern the pattern
     * @return true if the input matches the pattern, false otherwise
     */
    private static boolean matchesPattern(String input, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

}
