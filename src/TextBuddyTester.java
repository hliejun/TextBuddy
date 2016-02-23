import org.junit.Test;
import org.junit.Assert;

public class TextBuddyTester {
	
	private static final String filename = "testfile.txt";

	@Test
	public void testAdd() {
		String testCase0 = "clear";
		String expectedCase0 = "all content deleted from %1$s";
		String testCase1 = "add little brown fox";
		String expectedCase1 = "added to %1$s: \"little brown fox\"";
		String testCase2 = "display";
		String expectedCase2 = "1. little brown fox";
		String testCase3 = "clear";
		String expectedCase3 = "all content deleted from %1$s";
		
		TextBuddy myBuddy = new TextBuddy(filename);
		
		expectedCase0 = String.format(expectedCase0, filename);
		expectedCase1 = String.format(expectedCase1, filename);
		expectedCase3 = String.format(expectedCase3, filename);
		
		Assert.assertEquals(expectedCase0, myBuddy.executeCommand(testCase0));
		Assert.assertEquals(expectedCase1, myBuddy.executeCommand(testCase1));
		Assert.assertEquals(expectedCase2, myBuddy.executeCommand(testCase2));
		Assert.assertEquals(expectedCase3, myBuddy.executeCommand(testCase3));
	}
	
	@Test
	public void testAddFunction() {

		String expectedCase1 = "added to %1$s: \"little brown fox\"";
		TextBuddy myBuddy = new TextBuddy(filename);
		
		expectedCase1 = String.format(expectedCase1, filename);
		myBuddy.clear();
		
		Assert.assertEquals(expectedCase1, myBuddy.add("little brown fox"));
	}
	
	@Test
	public void testFull() {
		
		int testSize = 8;
		
		String[] input = new String[testSize];
		input[0] = "add little brown fox";
		input[1] = "display";
		input[2] = "add jumped over the moon";
		input[3] = "display";
		input[4] = "delete 2";
		input[5] = "display";
		input[6] = "clear";
		input[7] = "display";
		
		String[] expected = new String[testSize];
		expected[0] = "added to %1$s: \"little brown fox\"";
		expected[1] = "1. little brown fox";
		expected[2] = "added to %1$s: \"jumped over the moon\"";
		expected[3] = "1. little brown fox\n\n2. jumped over the moon";
		expected[4] = "deleted from %1$s: \"jumped over the moon\"";
		expected[5] = "1. little brown fox";
		expected[6] = "all content deleted from %1$s";
		expected[7] = "%1$s is empty";
		
		TextBuddy myBuddy = new TextBuddy(filename);
		
		expected[0] = String.format(expected[0], filename);
		expected[2] = String.format(expected[2], filename);
		expected[4] = String.format(expected[4], filename);
		expected[6] = String.format(expected[6], filename);
		expected[7] = String.format(expected[7], filename);
		
		for (int i = 0; i < input.length; i++) {
			Assert.assertEquals(expected[i], myBuddy.executeCommand(input[i]));
		}

	}
}
