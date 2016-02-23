import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

/*
 * TextBuddyTester is a JUnit test module that will test TextBuddy.
 */
public class TextBuddyTester {

    private static final String filename = "testfile.txt";
    private static TextBuddy myBuddy = null;

    /*
     * preprocessTest will construct a TextBuddy and clear the file contents
     * before any of the following tests.
     * 
     */
    @Before
    public void preprocessTest() {
        myBuddy = new TextBuddy(filename);
        myBuddy.clear();
    }

    /*
     * testAddUnit tests the add function
     */
    @Test
    public void testAddUnit() {

        String input1 = "little brown fox";
        String input2 = "";

        String expectedCase1 = "added to %1$s: \"little brown fox\"";
        expectedCase1 = String.format(expectedCase1, filename);
        String expectedCase2 = "you may not add an empty string";

        // Test for normal input
        Assert.assertEquals(expectedCase1, myBuddy.add(input1));
        // Test for null input
        Assert.assertEquals(expectedCase2, myBuddy.add(input2));
    }

    /*
     * testDisplayUnit tests the display function
     */
    @Test
    public void testDisplayUnit() {

        String expectedCase1 = "%1$s is empty";
        expectedCase1 = String.format(expectedCase1, filename);
        String expectedCase2 = "1. I've made up my mind\n\n2. Don't need to think it over"
                + "\n\n3. If I'm wrong, I am right\n\n4. add";

        // Test for empty list display
        Assert.assertEquals(expectedCase1, myBuddy.display());

        myBuddy.add("I've made up my mind");
        myBuddy.add("Don't need to think it over");
        myBuddy.add("If I'm wrong, I am right");
        myBuddy.add("add");

        // Test for normal display
        Assert.assertEquals(expectedCase2, myBuddy.display());
    }

    /*
     * testDeleteUnit tests the delete function
     */
    @Test
    public void testDeleteUnit() {

        int cases = 5;

        String content1 = "There's a fire starting in my heart";
        String content2 = "Reaching a fever pitch, it's bringing me out the dark";
        String content3 = "Finally I can see you crystal clear";

        String[] inputs = new String[5];
        inputs[0] = "1";
        inputs[1] = "Adele";
        inputs[2] = "2";
        inputs[3] = "100000";
        inputs[4] = "-5";

        String[] expected = new String[5];
        expected[0] = "the specified line number %1$s exceeds the range of %2$s";
        expected[0] = String.format(expected[0], inputs[0], filename);
        expected[1] = "\"%1$s\" cannot be parsed as a line number";
        expected[1] = String.format(expected[1], inputs[1]);
        expected[2] = "deleted from %1$s: \"%2$s\"";
        expected[2] = String.format(expected[2], filename, content2);
        expected[3] = "the specified line number %1$s exceeds the range of %2$s";
        expected[3] = String.format(expected[3], inputs[3], filename);
        expected[4] = "the specified line number %1$s exceeds the range of %2$s";
        expected[4] = String.format(expected[4], inputs[4], filename);

        // Test for out-of-range delete
        Assert.assertEquals(expected[0], myBuddy.delete(inputs[0]));

        myBuddy.add(content1);
        myBuddy.add(content2);
        myBuddy.add(content3);

        // Test for normal and non-numeric inputs
        for (int i = 1; i < cases; ++i) {
            Assert.assertEquals(expected[i], myBuddy.delete(inputs[i]));
        }
    }

    /*
     * testClearUnit tests the clear function
     */
    @Test
    public void testClearUnit() {

        int sizeBefore = 3;
        int sizeAfter = 0;

        String content1 = "hello from the other side";
        String content2 = "i must have called a thousand times";
        String content3 = "to tell you that i'm sorry";

        String expectedCase1 = "all content deleted from %1$s";
        expectedCase1 = String.format(expectedCase1, filename);

        myBuddy.add(content1);
        myBuddy.add(content2);
        myBuddy.add(content3);

        // Test for list size before clearing
        Assert.assertEquals(sizeBefore, myBuddy.getNumOfLines());
        
        // Test for normal clear feedback
        Assert.assertEquals(expectedCase1, myBuddy.clear());
        
        // Test for list size after clearing
        Assert.assertEquals(sizeAfter, myBuddy.getNumOfLines());
    }

    /*
     * testSortUnit tests the sort function
     */
    @Test
    public void testSortUnit() {

        String content1 = "I let it fall, my heart";
        String content2 = "And as it fell you rose to claim it";
        String content3 = "It was dark and I was over";
        String content4 = "Until you kissed my lips and you saved me";
        String content5 = "My hands, they're strong";
        String content6 = "it was dark and I was over";

        String expectedCase1 = "there is nothing in %1$s to sort";
        expectedCase1 = String.format(expectedCase1, filename);

        String expectedCase2 = "1. And as it fell you rose to claim it\n\n" + "2. I let it fall, my heart\n\n"
                + "3. It was dark and I was over\n\n" + "4. it was dark and I was over\n\n"
                + "5. My hands, they're strong\n\n" + "6. Until you kissed my lips and you saved me";

        // Test for sort on empty list
        Assert.assertEquals(expectedCase1, myBuddy.sort());

        myBuddy.add(content1);
        myBuddy.add(content2);
        myBuddy.add(content3);
        myBuddy.add(content4);
        myBuddy.add(content5);
        myBuddy.add(content6);

        // Test for locale-based sort
        Assert.assertEquals(expectedCase2, myBuddy.sort());
    }

    /*
     * testSearchUnit tests the search function
     */
    @Test
    public void testSearchUnit() {

        String content1 = "I heard that you're settled down";
        String content2 = "That you found a girl and you're married now";
        String content3 = "I heard that your dreams came true";
        String content4 = "Guess she gave you things I didn't give to you";

        String input1 = "heard";
        String input2 = "";
        String input3 = "Adele";
        String input4 = "tha";

        String expectedCase1 = "search for \"%1$s\" returns no result (search is CASE-SENSITIVE)";
        expectedCase1 = String.format(expectedCase1, input1);
        String expectedCase2 = "1. I heard that you're settled down\n\n"
                + "2. That you found a girl and you're married now\n\n" + "3. I heard that your dreams came true\n\n"
                + "4. Guess she gave you things I didn't give to you";
        String expectedCase3 = "search for \"%1$s\" returns no result (search is CASE-SENSITIVE)";
        expectedCase3 = String.format(expectedCase3, input3);
        String expectedCase4 = "1. I heard that you're settled down\n\n" + "2. I heard that your dreams came true";

        // Test for empty search result
        Assert.assertEquals(expectedCase1, myBuddy.search(input1));

        myBuddy.add(content1);
        myBuddy.add(content2);
        myBuddy.add(content3);
        myBuddy.add(content4);

        // Test for empty string search field
        Assert.assertEquals(expectedCase2, myBuddy.search(input2));
        
        // Test for empty search result
        Assert.assertEquals(expectedCase3, myBuddy.search(input3));
        
        // Test for substring and case-sensitive search result
        Assert.assertEquals(expectedCase4, myBuddy.search(input4));
    }

    /*
     * testMainUnit tests the executeCommand function
     */
    @Test
    public void testMain() {

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
        expected[0] = String.format(expected[0], filename);
        expected[1] = "1. little brown fox";
        expected[2] = "added to %1$s: \"jumped over the moon\"";
        expected[2] = String.format(expected[2], filename);
        expected[3] = "1. little brown fox\n\n2. jumped over the moon";
        expected[4] = "deleted from %1$s: \"jumped over the moon\"";
        expected[4] = String.format(expected[4], filename);
        expected[5] = "1. little brown fox";
        expected[6] = "all content deleted from %1$s";
        expected[6] = String.format(expected[6], filename);
        expected[7] = "%1$s is empty";
        expected[7] = String.format(expected[7], filename);

        for (int i = 0; i < input.length; i++) {
            Assert.assertEquals(expected[i], myBuddy.executeCommand(input[i]));
        }
    }
}
