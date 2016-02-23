import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
 * @author Huang Lie Jun
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
	private static final String MESSAGE_DISPLAY_ERROR = "error parsing and displaying %1$s";
	private static final String MESSAGE_DELETE_RANGE_ERROR = "the specified line number %1$d exceeds the number of lines in %2$s";
	private static final String MESSAGE_DELETE_FILE_ERROR = "error deleting from %1$s";
	private static final String MESSAGE_DELETE_FORMAT_ERROR = "%1$s cannot be parsed as a line number";
	private static final String MESSAGE_CLEAR_ERROR = "unable to clear contents of %1$s";

	// Temporary file name format
	private static final String MESSAGE_TEMP_FILE_NAME = "~%1$s.tmp";

	// Regex expression for one or more whitespace
	private static final String REGEX_WHITESPACES = "\\s+";

	// System expression for line break (OS dependent)
	private static final String LINE_BREAK = System
			.getProperty("line.separator");

	// These are the possible command types
	enum COMMAND_TYPE {
		ADD, DISPLAY, DELETE, CLEAR, EXIT, INVALID
	};

	/*
	 * These variable are declared global for the whole class to facilitate
	 * automated testing using the I/O redirection technique.
	 */
	private static Scanner scanner = new Scanner(System.in);
	private static File file;
	private static File temporaryFile;
	private static FileOutputStream outFile;
	private static PrintWriter writer;
	private static FileOutputStream tempOutFile;
	private static PrintWriter tempWriter;

	public static void main(String[] args) {
		String path = args[0];

		try {
			file = new File(path);

			openFile(file);

			String welcomeMessage = String.format(WELCOME_MESSAGE, file);
			showToUser(welcomeMessage);

			while (true) {
				readAndExecuteCommand();
			}
		} catch (IOException ioException) {
			String error = String.format(MESSAGE_FILE_ERROR, file);
			showToUser(error);
		} catch (Exception exception) {
			System.exit(-1);
		}
	}

	/**
	 * This operation opens the file specified for editing. If does not
	 * already exists, a new file will be created. It also initializes 
	 * output streams for the file.
	 *
	 * @param file
	 *         is the file to be used with TextBuddy.
	 */
	private static void openFile(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		outFile = new FileOutputStream(file, true);
		writer = new PrintWriter(outFile);
	}

	/**
	 * This operation displays and prints the feedbacks or error messages 
	 * returned by auxillary functions to the main process.
	 *
	 * @param text
	 *         is the string to be displayed to the user.
	 */
	private static void showToUser(String text) {
		System.out.println(text);
		System.out.println();
	}

	/**
	 * This operation displays and prints the feedbacks or error messages 
	 * returned by auxillary functions to the main process, without any
	 * line breaks.
	 *
	 * @param text
	 *         is the string to be displayed to the user.
	 */
	private static void showToUserOnLine(String text) {
		System.out.print(text);
	}

	/**
	 * This operation displays and prints the feedbacks or error messages 
	 * returned by auxillary functions to the main process, with additional
	 * line break.
	 *
	 * @param text
	 *         is the string to be displayed to the user.
	 */
	private static void showToUserWithBreak(String text) {
		System.out.println();
		System.out.println(text);
		System.out.println();
	}

	/**
	 * This operation reads and executes the command entered by the user, if valid.
	 * The resulting feedback will be displayed to the user.
	 */
	private static void readAndExecuteCommand() {
		String userCommand = readCommand();

		String feedback = executeCommand(userCommand);

		showToUserWithBreak(feedback);
	}

	/**
	 * This operation reads the command entered by the user.
	 * 
	 * @return the command entered by the user.
	 */
	private static String readCommand() {
		showToUserOnLine(MESSAGE_INPUT_PROMPT);

		String command = scanner.nextLine();

		return command;
	}

	/**
	 * This operation checks the format of the command and validity of the command type
	 * and executes the command accordingly to the command type.
	 *
	 * @param userCommand
	 *         is the command string entered by the user.
	 *         
	 * @return the feedback resulted from the execution of the command.
	 */
	private static String executeCommand(String userCommand) {
		boolean isProperFormat = checkCommandFormat(userCommand);

		if (!isProperFormat) {
			return String.format(MESSAGE_INVALID_COMMAND_FORMAT, userCommand);
		}

		String commandTypeString = getFirstWord(userCommand);

		String remainingCommand = getRemainingCommand(userCommand);

		COMMAND_TYPE commandType = determineCommandType(commandTypeString);

		switch (commandType) {
		case ADD:
			return add(commandTypeString, remainingCommand);
			// Fallthrough

		case DISPLAY:
			return display(commandTypeString);
			// Fallthrough

		case DELETE:
			return delete(commandTypeString, remainingCommand);
			// Fallthrough

		case CLEAR:
			return clear(commandTypeString);
			// Fallthrough

		case EXIT:
			exit();
			// Fallthrough

		default:
			return UNRECOGNIZED_COMMAND_ERROR;
		}
	}

	/**
	 * This operation checks that the command is not null.
	 *
	 * @param string
	 *         is the command entered by the user.
	 *         
	 * @return true if the command is not null.
	 */
	private static boolean checkCommandFormat(String string) {
		return !string.trim().equals("");
	}

	/**
	 * This operation determines the command type specified by the user.
	 *
	 * @param commandTypeString
	 *         is the command type specified by the user.
	 *         
	 * @return the enumerated value of the command type.
	 */
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
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
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}

	/**
	 * This operation writes a line to the end of the file used with TextBuddy.
	 *
	 * @param commandType
	 *         is the command type.
	 *         
	 * @param remainingCommand
	 *         is the remaining of the command or the parameter to be used with
	 *         the execution of a specific command type.
	 *         
	 * @return feedback that the string has been added.
	 */
	private static String add(String commandType, String remainingCommand) {
		writer.println(remainingCommand);
		writer.flush();

		String feedback = String.format(MESSAGE_ADD_FEEDBACK, file,
				remainingCommand);

		return feedback;
	}

	/**
	 * This operation reads the file into an array of lines and display a numbered list.
	 *
	 * @param commandType
	 *         is the command type.
	 *         
	 * @return an empty list feedback if the file is empty, or a string specifying the
	 * list of lines in the file. An error message will be returned if there are problems
	 * reading the file.
	 */
	private static String display(String commandType) {
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
	 *         is the command type.
	 *         
	 * @param remainingCommand
	 *         is the line number representing the line to be deleted.
	 *         
	 * @return a feedback upon successful deletion, or an error message if
	 * file operations fail.
	 */
	private static String delete(String commandType, String remainingCommand) {
		try {
			String[] lines = readFileIntoLines();
			int lineToDelete = Integer.parseInt(remainingCommand);

			if (lineToDelete > lines.length) {
				return String.format(MESSAGE_DELETE_RANGE_ERROR, lineToDelete,
						file);
			}

			String stringToDelete = getDeleteString(lines, lineToDelete);

			createTemporaryFile();

			writeToTemporaryFile(lines, lineToDelete);

			closeTemporaryFile();

			boolean overwritten = overwriteOldFile();

			if (!overwritten) {
				return String.format(MESSAGE_DELETE_FILE_ERROR, file);
			} else {
				return String.format(MESSAGE_DELETE_SUCCESS, file,
						stringToDelete);
			}
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
	 *         is the command type.
	 *         
	 * @return a feedback upon successful clearing, or an error message if clearing fails.
	 */
	private static String clear(String commandType) {
		try {
			createTemporaryFile();
			closeTemporaryFile();

			boolean overwritten = overwriteOldFile();

			if (!overwritten) {
				return String.format(MESSAGE_CLEAR_ERROR, file);
			} else {
				return String.format(MESSAGE_CLEAR_SUCCESS, file);
			}
		} catch (IOException ioException) {
			return String.format(MESSAGE_CLEAR_ERROR, file);
		}
	}

	/**
	 * This operation closes file streams and terminates the application.
	 */
	private static void exit() {
		int status = 0;
		try {
			writer.close();
			outFile.close();
			System.exit(status);
		} catch (IOException ioException) {
			status = 1;
		} finally {
			System.exit(status);
		}
	}

	/**
	 * This operation gets the first word in a sentence of command.
	 *
	 * @param userCommand
	 *         is the command entered by the user.
	 *         
	 * @return the command type entered by the user.
	 */
	private static String getFirstWord(String userCommand) {
		String trimmedCommand = userCommand.trim();
		String commandTypeString = trimmedCommand.split(REGEX_WHITESPACES)[0];

		return commandTypeString;
	}

	/**
	 * This operation gets the remaining of a command entered by the user, without
	 * the command type.
	 *
	 * @param userCommand
	 *         is the command entered by the user.
	 *         
	 * @return the command without the command type.
	 */
	private static String getRemainingCommand(String userCommand) {
		String firstWord = getFirstWord(userCommand);
		String remainingCommand = userCommand.replace(firstWord, "");

		return remainingCommand.trim();
	}

	/**
	 * This operation reads the file into an array of lines.
	 *         
	 * @return an array of strings or lines (tokenized file), or throws an exception
	 * upon error.
	 */
	private static String[] readFileIntoLines() throws IOException {
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
	 * This operation parses the array of lines for feedback display, with numberings.
	 *
	 * @param lines
	 *         is the array of lines to be displayed at feedback.
	 *         
	 * @return a collated feedback string of all the lines in the array, numbered.
	 */
	private static String collateDisplay(String[] lines) {
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
	 *         is the arrray of lines to be displayed at feedback.
	 *         
	 * @param lineNumber
	 *         is the line number of the current line.
	 *         
	 * @return a feedback for the current line, numbered.
	 */
	private static String getLineFeedback(String[] lines, int lineNumber) {
		String currentLine = lines[lineNumber - 1];
		String lineFeedback = String.format(MESSAGE_DISPLAY_LINE_FEEDBACK,
				lineNumber, currentLine);

		if (lineNumber != lines.length) {
			lineFeedback += LINE_BREAK;
		}

		return lineFeedback;
	}

	/**
	 * This operation creates a temporary file for the file being used with TextBuddy, and 
	 * initializes output streams. On error, it throws an exception to the calling method.
	 */
	private static void createTemporaryFile() throws IOException {
		String temporaryName = String.format(MESSAGE_TEMP_FILE_NAME,
				file.getName());
		temporaryFile = new File(temporaryName);

		temporaryFile.createNewFile();

		tempOutFile = new FileOutputStream(temporaryFile, true);
		tempWriter = new PrintWriter(tempOutFile);
	}

	/**
	 * This operation writes to the temporary file created for the file being used with
	 * TextBuddy by copying everything except for the line to be deleted into the temporary
	 * file.
	 *
	 * @param lines
	 *         is the array of lines from the original file.
	 *         
	 * @param lineToDelete is the line number of the line to be removed from the original file.
	 */
	private static void writeToTemporaryFile(String[] lines, int lineToDelete) {
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
	 * This operation closes the temporary file streams and throws exception on error.
	 */
	private static void closeTemporaryFile() throws IOException {
		tempWriter.close();
		tempOutFile.close();
	}

	/**
	 * This operation renames the temporary file to overwrite the original file. 
	 *
	 * @return true if the renaming is successful, and throws error if a file error occurs.
	 */
	private static boolean overwriteOldFile() throws IOException {
		boolean overwritten = temporaryFile.renameTo(file);

		if (overwritten) {
			outFile.close();
			writer.close();
			outFile = new FileOutputStream(file, true);
			writer = new PrintWriter(outFile);
		}

		return overwritten;
	}

	/**
	 * This operation gets the contents of the string to be deleted from the file.
	 *
	 * @param lines
	 *         is the array of lines in the file.
	 *         
	 * @param lineNumber
	 *         is the line number of the line to be deleted.
	 *         
	 * @return the string to be deleted.
	 */
	private static String getDeleteString(String[] lines, int lineNumber) {
		return lines[lineNumber - 1];
	}
}
