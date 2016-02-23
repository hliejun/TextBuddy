import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

public class TextBuddyTester {

    private static final String filename = "testfile.txt";

    private static TextBuddy myBuddy = null;

    @Before
    public void preprocessTest() {
        myBuddy = new TextBuddy(filename);
        myBuddy.clear();
    }

    @Test
    public void testAddUnit() {

        String input1 = "little brown fox";
        String expectedCase1 = "added to %1$s: \"little brown fox\"";

        expectedCase1 = String.format(expectedCase1, filename);

        Assert.assertEquals(expectedCase1, myBuddy.add(input1));
    }

//    @Test
//    public void testDisplayUnit() {
//
//        String input1 = "little brown fox";
//        String expectedCase1 = "added to %1$s: \"little brown fox\"";
//
//        expectedCase1 = String.format(expectedCase1, filename);
//
//        Assert.assertEquals(expectedCase1, myBuddy.add(input1));
//    }

//    @Test
//    public void testDeleteUnit() {
//
//        String input1 = "little brown fox";
//        String expectedCase1 = "added to %1$s: \"little brown fox\"";
//
//        expectedCase1 = String.format(expectedCase1, filename);
//
//        Assert.assertEquals(expectedCase1, myBuddy.add(input1));
//    }

    @Test
    public void testClearUnit() {

        String expectedCase1 = "all content deleted from %1$s";
        String content1 = "hello from the other side";
        String content2 = "i must have called a thousand times";
        String content3 = "to tell you that i'm sorry";
        int sizeBefore = 3;
        int sizeAfter = 0;

        expectedCase1 = String.format(expectedCase1, filename);

        myBuddy.add(content1);
        myBuddy.add(content2);
        myBuddy.add(content3);

        Assert.assertEquals(sizeBefore, myBuddy.getNumOfLines());
        Assert.assertEquals(expectedCase1, myBuddy.clear());
        Assert.assertEquals(sizeAfter, myBuddy.getNumOfLines());
    }

//    @Test
//    public void testSortUnit() {
//
//        String input1 = "little brown fox";
//        String expectedCase1 = "added to %1$s: \"little brown fox\"";
//
//        expectedCase1 = String.format(expectedCase1, filename);
//
//        Assert.assertEquals(expectedCase1, myBuddy.add(input1));
//    }

//    @Test
//    public void testSearchUnit() {
//
//        String input1 = "little brown fox";
//        String expectedCase1 = "added to %1$s: \"little brown fox\"";
//
//        expectedCase1 = String.format(expectedCase1, filename);
//
//        Assert.assertEquals(expectedCase1, myBuddy.add(input1));
//    }

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
        expected[1] = "1. little brown fox";
        expected[2] = "added to %1$s: \"jumped over the moon\"";
        expected[3] = "1. little brown fox\n\n2. jumped over the moon";
        expected[4] = "deleted from %1$s: \"jumped over the moon\"";
        expected[5] = "1. little brown fox";
        expected[6] = "all content deleted from %1$s";
        expected[7] = "%1$s is empty";

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
