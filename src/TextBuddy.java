import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * This class is used to add, display, delete and clear text in a file. The
 * command format is given by the example interaction below:
 *
 * Welcome to TextBuddy. mytextfile.txt is ready for use
 *
 * command: add little brown fox
 *
 * added to mytextfile.txt: "little brown fox"
 *
 * command: display
 *
 * 1. little brown fox
 *
 * command: add jumped over the moon
 *
 * added to mytextfile.txt: "jumped over the moon"
 *
 * command: display
 *
 * 1. little brown fox
 *
 * 2. jumped over the moon
 *
 * command: delete 2
 *
 * deleted from mytextfile.txt: "jumped over the moon"
 *
 * command: display
 *
 * 1. little brown fox
 *
 * command: clear
 *
 * all content deleted from mytextfile.txt
 *
 * command: display
 *
 * mytextfile.txt is empty
 *
 * command: exit
 *
 * @author Huang Lie Jun (A0123994W)
 * 
 * Assumptions:
 * 1) Adding of empty string to file is not allowed.
 * 2) Results of sorting will not be stored into text file.
 * 3) Results of searching will not be stored into text file.
 * 4) Search is cap-sensitive.
 * 5) Search will return all lines that contain matching substring(s).
 * 
 */
public class TextBuddy {

    // Prompts and feedbacks
    private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %1$s is ready for use";
    private static final String MESSAGE_INPUT_PROMPT = "command: ";
    private static final String MESSAGE_ADD_FEEDBACK = "added to %1$s: \"%2$s\"";
    private static final String MESSAGE_DISPLAY_LINE_FEEDBACK = "%1$d. %2$s";
    private static final String MESSAGE_DISPLAY_EMPTY = "%1$s is empty";
    private static final String MESSAGE_DELETE_SUCCESS = "deleted from %1$s: \"%2$s\"";
    private static final String MESSAGE_CLEAR_SUCCESS = "all content deleted from %1$s";

    // Error messages
    private static final String MESSAGE_FILE_ERROR = "error reading %1$s; file does not exist";
    private static final String MESSAGE_INVALID_COMMAND_FORMAT = "invalid command format: %1$s";
    private static final String UNRECOGNIZED_COMMAND_ERROR = "unrecognized command type";
    private static final String NULL_COMMAND_STRING_ERROR = "command type string cannot be null!";
    private static final String MESSAGE_ADD_EMPTY_COMMAND = "you may not add an empty string";
    private static final String MESSAGE_DISPLAY_ERROR = "error parsing and displaying %1$s";
    private static final String MESSAGE_DELETE_RANGE_ERROR = "the specified line number %1$d "
            + "exceeds the range of %2$s";
    private static final String MESSAGE_DELETE_FILE_ERROR = "error deleting from %1$s";
    private static final String MESSAGE_DELETE_FORMAT_ERROR = "\"%1$s\" cannot be parsed as a line number";
    private static final String MESSAGE_CLEAR_ERROR = "unable to clear contents of %1$s";
    private static final String MESSAGE_SORT_EMPTY = "there is nothing in %1$s to sort";
    private static final String MESSAGE_SORT_ERROR = "unable to sort contents of %1$s";
    private static final String MESSAGE_SEARCH_EMPTY = "search for \"%1$s\" returns no result (search is CASE-SENSITIVE)";
    private static final String MESSAGE_SEARCH_ERROR = "unable to search contents in %1$s";

    // Temporary file name format
    private static final String MESSAGE_TEMP_FILE_NAME = "~%1$s.tmp";

    // Regex expression for one or more whitespace
    private static final String REGEX_WHITESPACES = "\\s+";

    // System expression for line break (OS dependent)
    private static final String LINE_BREAK = System.getProperty("line.separator");

    // US Locale
    private static final Locale LOCALE = Locale.US;

    // Collator
    private static final Collator COLLATOR = Collator.getInstance(LOCALE);
    private static final int COLLATOR_STRENGTH = Collator.PRIMARY;

    // These are the possible command types
    enum COMMAND_TYPE {
        ADD, DISPLAY, DELETE, CLEAR, SEARCH, SORT, EXIT, INVALID
    };

    /*
     * These variable are declared global for the whole class to facilitate
     * automated testing using the I/O redirection technique.
     */
    private Scanner scanner = new Scanner(System.in);
    private File file;
    private File temporaryFile;
    private FileOutputStream outFile;
    private PrintWriter writer;
    private FileOutputStream tempOutFile;
    private PrintWriter tempWriter;

    /****************************/
    /* * KEY FUNCTIONS * */
    /****************************/

    /**
     * The constructor that initializes file variables.
     *
     * @param path
     *            the the file path to be used for TextBuddy.
     */
    public TextBuddy(String path) {
        try {
            file = new File(path);
            openFile(file);
            welcomeUser();
        } catch (IOException ioException) {
            String error = String.format(MESSAGE_FILE_ERROR, file);
            showToUser(error);
        } catch (Exception exception) {
            System.exit(-1);
        }
    }

    /*
     * The main function that constructs a TextBuddy and runs it til user exits.
     */
    public static void main(String[] args) {
        String path = args[0];
        TextBuddy myBuddy = new TextBuddy(path);

        runForever(myBuddy);
    }

    /*
     * This operation loops infinitely to read and execute user commands.
     *
     * @param myBuddy is the TextBuddy that is being looped.
     *
     */
    private static void runForever(TextBuddy myBuddy) {
        while (true) {
            try {
                myBuddy.readAndExecuteCommand();
            } catch (Exception exception) {
                exception.printStackTrace();
                continue;
            }
        }
    }

    /**
     * This operation reads and executes the command entered by the user, if
     * valid. The resulting feedback will be displayed to the user.
     */
    private void readAndExecuteCommand() {
        String userCommand = readCommand();
        String feedback = executeCommand(userCommand);

        showToUserWithBreak(feedback);
    }

    /**
     * This operation reads the command entered by the user.
     *
     * @return the command entered by the user.
     */
    private String readCommand() {
        showToUserOnLine(MESSAGE_INPUT_PROMPT);

        String command = scanner.nextLine();

        return command;
    }

    /**
     * This operation checks the format of the command and validity of the
     * command type and executes the command accordingly to the command type.
     *
     * @param userCommand
     *            is the command string entered by the user.
     *
     * @return the feedback resulted from the execution of the command.
     */
    public String executeCommand(String userCommand) {
        boolean isProperFormat = isValidCommandFormat(userCommand);

        if (!isProperFormat) {
            return String.format(MESSAGE_INVALID_COMMAND_FORMAT, userCommand);
        }

        COMMAND_TYPE commandType = parseCommandType(userCommand);

        String remainingCommand = getRemainingCommand(userCommand);

        switch (commandType) {
        case ADD:
            return add(remainingCommand);
        // Fallthrough

        case DISPLAY:
            return display();
        // Fallthrough

        case DELETE:
            return delete(remainingCommand);
        // Fallthrough

        case CLEAR:
            return clear();
        // Fallthrough

        case SEARCH:
            return search(remainingCommand);
        // Fallthrough

        case SORT:
            return sort();
        // Fallthrough

        case EXIT:
            exit();
            // Fallthrough

        default:
            return UNRECOGNIZED_COMMAND_ERROR;
        }
    }

    /**
     * This operation writes a line to the end of the file used with TextBuddy.
     *
     * @param commandType
     *            is the command type.
     *
     * @param remainingCommand
     *            is the remaining of the command or the parameter to be used
     *            with the execution of a specific command type.
     *
     * @return feedback that the string has been added.
     */
    public String add(String remainingCommand) {
        if (remainingCommand.isEmpty()) {
            return MESSAGE_ADD_EMPTY_COMMAND;
        }
        writer.println(remainingCommand);
        writer.flush();

        String feedback = String.format(MESSAGE_ADD_FEEDBACK, file, remainingCommand);

        return feedback;
    }

    /**
     * This operation reads the file into an array of lines and display a
     * numbered list.
     *
     * @param commandType
     *            is the command type.
     *
     * @return an empty list feedback if the file is empty, or a string
     *         specifying the list of lines in the file. An error message will
     *         be returned if there are problems reading the file.
     */
    public String display() {
        try {
            String[] lines = readFileIntoLines();
            String feedback;

            // Set feedback for empty files / non-empty files
            if (lines.length == 0) {
                feedback = String.format(MESSAGE_DISPLAY_EMPTY, file);
            } else {
                feedback = collateDisplay(lines);
            }

            return feedback;
        } catch (IOException ioException) {
            return String.format(MESSAGE_DISPLAY_ERROR, file);
        }
    }

    /**
     * This operation deletes a single line from the text file.
     *
     * @param commandType
     *            is the command type.
     *
     * @param remainingCommand
     *            is the line number representing the line to be deleted.
     *
     * @return a feedback upon successful deletion, or an error message if file
     *         operations fail.
     */
    public String delete(String remainingCommand) {
        try {
            String[] lines = readFileIntoLines();
            int lineToDelete = Integer.parseInt(remainingCommand);

            if (lineToDelete > lines.length || lineToDelete < 1) {
                return String.format(MESSAGE_DELETE_RANGE_ERROR, lineToDelete, file);
            }

            String stringToDelete = getDeleteString(lines, lineToDelete);

            createFileForOverwrite(lines, lineToDelete);

            return overwriteOriginal(stringToDelete);
        } catch (IOException ioException) {
            return String.format(MESSAGE_DELETE_FILE_ERROR, file);
        } catch (NumberFormatException numberFormatException) {
            return String.format(MESSAGE_DELETE_FORMAT_ERROR, remainingCommand);
        }
    }

    /**
     * This operation clears the entire file empty.
     *
     * @param commandType
     *            is the command type.
     *
     * @return a feedback upon successful clearing, or an error message if
     *         clearing fails.
     */
    public String clear() {
        try {
            createEmptyForOverwrite();

            return overwriteWithEmptyFile();
        } catch (IOException ioException) {
            return String.format(MESSAGE_CLEAR_ERROR, file);
        }
    }

    /*
     * This operation sorts and returns the sorted list.
     * 
     * @return the result of the sorting, or feedback messages if applicable.
     * 
     */
    public String sort() {
        try {
            String[] lines = readFileIntoLines();
            String feedback = null;

            // Set feedback for empty files / non-empty files
            if (lines.length == 0) {
                feedback = String.format(MESSAGE_SORT_EMPTY, file);
            } else {
                COLLATOR.setStrength(COLLATOR_STRENGTH);
                Arrays.sort(lines, COLLATOR);
                feedback = collateDisplay(lines);
            }

            return feedback;
        } catch (IOException ioException) {
            return String.format(MESSAGE_SORT_ERROR, file);
        }
    }

    /*
     * This operation searches the list in the file and returns lines containing
     * the search term as substring.
     * 
     * @param remainingCommand is the search term or the substring to search
     * for.
     * 
     * @return the result of the search, or feedback messages, if applicable.
     */
    public String search(String remainingCommand) {
        try {
            String[] lines = readFileIntoLines();
            ArrayList<String> searchResults = new ArrayList<String>();
            String feedback = null;

            for (int i = 0; i < lines.length; ++i) {
                if (lines[i].contains(remainingCommand)) {
                    searchResults.add(lines[i]);
                }
            }

            if (searchResults.size() == 0) {
                feedback = String.format(MESSAGE_SEARCH_EMPTY, remainingCommand);
            } else {
                String[] results = searchResults.toArray(new String[searchResults.size()]);
                feedback = collateDisplay(results);
            }

            return feedback;
        } catch (IOException ioException) {
            return String.format(MESSAGE_SEARCH_ERROR, file);
        }
    }

    /**
     * This operation closes file streams and terminates the application.
     */
    public void exit() {
        int status = 0;
        try {
            closeStreams();
            System.exit(status);
        } catch (IOException ioException) {
            status = 1;
        } finally {
            System.exit(status);
        }
    }

    /****************************/
    /* * AUXILLIARY FUNCTIONS * */
    /****************************/

    /*** String Handling and Parsing ***/

    /**
     * This operation gets the first word in a sentence of command.
     *
     * @param userCommand
     *            is the command entered by the user.
     *
     * @return the command type entered by the user.
     */
    private String getFirstWord(String userCommand) {
        String trimmedCommand = userCommand.trim();
        String commandTypeString = trimmedCommand.split(REGEX_WHITESPACES)[0];

        return commandTypeString;
    }

    /**
     * This operation gets the remaining of a command entered by the user,
     * without the command type.
     *
     * @param userCommand
     *            is the command entered by the user.
     *
     * @return the command without the command type.
     */
    private String getRemainingCommand(String userCommand) {
        String firstWord = getFirstWord(userCommand);
        String remainingCommand = userCommand.replaceFirst(firstWord, "");

        return remainingCommand.trim();
    }

    /**
     * This operation reads the file into an array of lines.
     *
     * @return an array of strings or lines (tokenized file), or throws an
     *         exception upon error.
     */
    private String[] readFileIntoLines() throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader lineReader = new BufferedReader(reader);

        List<String> lines = new ArrayList<String>();
        String line;

        while ((line = lineReader.readLine()) != null) {
            lines.add(line);
        }

        lineReader.close();
        reader.close();

        String[] listOfLines = new String[lines.size()];

        return lines.toArray(listOfLines);
    }

    /**
     * This operation parses the array of lines for feedback display, with
     * numberings.
     *
     * @param lines
     *            is the array of lines to be displayed at feedback.
     *
     * @return a collated feedback string of all the lines in the array,
     *         numbered.
     */
    private String collateDisplay(String[] lines) {
        String feedback = new String();

        for (int i = 1; i <= lines.length; ++i) {
            feedback += getLineFeedback(lines, i);

            if (i != lines.length) {
                feedback += LINE_BREAK;
            }
        }

        return feedback;
    }

    /**
     * This operation generates the feedback for displaying for a single line.
     *
     * @param lines
     *            is the array of lines to be displayed at feedback.
     *
     * @param lineNumber
     *            is the line number of the current line.
     *
     * @return a feedback for the current line, numbered.
     */
    private String getLineFeedback(String[] lines, int lineNumber) {
        String currentLine = lines[lineNumber - 1];
        String lineFeedback = String.format(MESSAGE_DISPLAY_LINE_FEEDBACK, lineNumber, currentLine);

        if (lineNumber != lines.length) {
            lineFeedback += LINE_BREAK;
        }

        return lineFeedback;
    }

    /**
     * This operation gets the contents of the string to be deleted from the
     * file.
     *
     * @param lines
     *            is the array of lines in the file.
     *
     * @param lineNumber
     *            is the line number of the line to be deleted.
     *
     * @return the string to be deleted.
     */
    private String getDeleteString(String[] lines, int lineNumber) {
        return lines[lineNumber - 1];
    }

    /*
     * This operation takes the string command type to determine the enumerated
     * value.
     *
     * @param userCommand is the string form of the command keyword
     *
     * @return the enumerated form of the command keyword
     */
    private COMMAND_TYPE parseCommandType(String userCommand) {
        String commandTypeString = getFirstWord(userCommand);
        COMMAND_TYPE commandType = determineCommandType(commandTypeString);
        return commandType;
    }

    /*** Checks ***/

    /**
     * This operation checks that the command is not null.
     *
     * @param string
     *            is the command entered by the user.
     *
     * @return true if the command is not null.
     */
    private boolean isValidCommandFormat(String string) {
        return !string.trim().equals("");
    }

    /**
     * This operation determines the command type specified by the user.
     *
     * @param commandTypeString
     *            is the command type specified by the user.
     *
     * @return the enumerated value of the command type.
     */
    private COMMAND_TYPE determineCommandType(String commandTypeString) {
        if (commandTypeString == null) {
            throw new Error(NULL_COMMAND_STRING_ERROR);
        }

        if (commandTypeString.equalsIgnoreCase("add")) {
            return COMMAND_TYPE.ADD;
        } else if (commandTypeString.equalsIgnoreCase("display")) {
            return COMMAND_TYPE.DISPLAY;
        } else if (commandTypeString.equalsIgnoreCase("delete")) {
            return COMMAND_TYPE.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("clear")) {
            return COMMAND_TYPE.CLEAR;
        } else if (commandTypeString.equalsIgnoreCase("search")) {
            return COMMAND_TYPE.SEARCH;
        } else if (commandTypeString.equalsIgnoreCase("sort")) {
            return COMMAND_TYPE.SORT;
        } else if (commandTypeString.equalsIgnoreCase("exit")) {
            return COMMAND_TYPE.EXIT;
        } else {
            return COMMAND_TYPE.INVALID;
        }
    }

    /*** Temporary File Handlers ***/

    /**
     * This operation creates a temporary file for the file being used with
     * TextBuddy, and initializes output streams. On error, it throws an
     * exception to the calling method.
     */
    private void createTemporaryFile() throws IOException {
        String temporaryName = String.format(MESSAGE_TEMP_FILE_NAME, file.getName());
        temporaryFile = new File(temporaryName);

        temporaryFile.createNewFile();

        tempOutFile = new FileOutputStream(temporaryFile, true);
        tempWriter = new PrintWriter(tempOutFile);
    }

    /**
     * This operation writes to the temporary file created for the file being
     * used with TextBuddy by copying everything except for the line to be
     * deleted into the temporary file.
     *
     * @param lines
     *            is the array of lines from the original file.
     *
     * @param lineToDelete
     *            is the line number of the line to be removed from the original
     *            file.
     */
    private void writeToTemporaryFile(String[] lines, int lineToDelete) {
        for (int i = 1; i <= lines.length; ++i) {
            if (i == lineToDelete) {
                continue;
            }

            String currentLine = lines[i - 1];

            tempWriter.println(currentLine);
            tempWriter.flush();
        }
    }

    /**
     * This operation closes the temporary file streams and throws exception on
     * error.
     */
    private void closeTemporaryFile() throws IOException {
        tempWriter.close();
        tempOutFile.close();
    }

    /*
     * This operation will rename the temporary file to replace the original
     * file.
     *
     * @param stringToDelete is the string that has been left out from the
     * original file.
     *
     * @return the status of overwriting the old file with the temporary file.
     */
    private String overwriteOriginal(String stringToDelete) throws IOException {
        boolean isOverwritten = hasOverwrittenOldFile();

        if (!isOverwritten) {
            return String.format(MESSAGE_DELETE_FILE_ERROR, file);
        } else {
            return String.format(MESSAGE_DELETE_SUCCESS, file, stringToDelete);
        }
    }

    /*
     * This operation creates the temporary file without the line to delete.
     *
     * @param lines is the array of lines obtained from the old file.
     *
     * @param lineToDelete is the index of the line to be deleted from the old
     * file.
     *
     */
    private void createFileForOverwrite(String[] lines, int lineToDelete) throws IOException {
        createTemporaryFile();
        writeToTemporaryFile(lines, lineToDelete);
        closeTemporaryFile();
    }

    /**
     * This operation renames the temporary file to overwrite the original file.
     *
     * @return true if the renaming is successful, and throws error if a file
     *         error occurs.
     */
    private boolean hasOverwrittenOldFile() throws IOException {
        boolean isOverwritten = temporaryFile.renameTo(file);

        if (isOverwritten) {
            closeStreams();
            renewStreams();
        }

        return isOverwritten;
    }

    /*
     * This operation creates an empty temporary file to overwrite the old file.
     */
    private void createEmptyForOverwrite() throws IOException {
        createTemporaryFile();
        closeTemporaryFile();
    }

    /*
     * This operation renames the empty temporary file to replace the old file.
     *
     * @return the status of the overwrite.
     */
    private String overwriteWithEmptyFile() throws IOException {
        boolean isOverwritten = hasOverwrittenOldFile();

        if (!isOverwritten) {
            return String.format(MESSAGE_CLEAR_ERROR, file);
        } else {
            return String.format(MESSAGE_CLEAR_SUCCESS, file);
        }
    }

    /*** Generic File and Streams Handler ***/

    /**
     * This operation opens the file specified for editing. If does not already
     * exists, a new file will be created. It also initializes output streams
     * for the file.
     *
     * @param file
     *            is the file to be used with TextBuddy.
     */
    private void openFile(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        renewStreams();
    }

    /*
     * This operation opens a new write stream for the data file.
     */
    private void renewStreams() throws FileNotFoundException {
        outFile = new FileOutputStream(file, true);
        writer = new PrintWriter(outFile);
    }

    /*
     * This operation closes the output stream for the data file.
     */
    private void closeStreams() throws IOException {
        writer.close();
        outFile.close();
    }

    /*** Display Handlers ***/

    /* This operation displays the welcome message to the user. */
    private void welcomeUser() {
        String welcomeMessage = String.format(WELCOME_MESSAGE, file);
        showToUser(welcomeMessage);
    }

    /**
     * This operation displays and prints the feedbacks or error messages
     * returned by auxillary functions to the main process.
     *
     * @param text
     *            is the string to be displayed to the user.
     */
    private void showToUser(String text) {
        System.out.println(text);
        System.out.println();
    }

    /**
     * This operation displays and prints the feedbacks or error messages
     * returned by auxillary functions to the main process, without any line
     * breaks.
     *
     * @param text
     *            is the string to be displayed to the user.
     */
    private void showToUserOnLine(String text) {
        System.out.print(text);
    }

    /**
     * This operation displays and prints the feedbacks or error messages
     * returned by auxillary functions to the main process, with additional line
     * break.
     *
     * @param text
     *            is the string to be displayed to the user.
     */
    private void showToUserWithBreak(String text) {
        System.out.println();
        System.out.println(text);
        System.out.println();
    }

    /*** JUnit Accessories ***/

    /*
     * This operation returns the number of lines present in the text file.
     * 
     * @return the number of lines presently stored in the file.
     */
    public int getNumOfLines() {
        int size = -1;
        try {
            String[] lines = readFileIntoLines();
            size = lines.length;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return size;
    }

}
