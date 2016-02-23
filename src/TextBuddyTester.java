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

    @Test
    public void testDisplayUnit() {

        String expectedCase1 = "%1$s is empty";
        String expectedCase2 = "1. I've made up my mind\n\n2. Don't need to think it over"
                + "\n\n3. If I'm wrong, I am right\n\n4. add";

        expectedCase1 = String.format(expectedCase1, filename);

        Assert.assertEquals(expectedCase1, myBuddy.display());
        
        myBuddy.add("I've made up my mind");
        myBuddy.add("Don't need to think it over");
        myBuddy.add("If I'm wrong, I am right");
        myBuddy.add("add");
        
        Assert.assertEquals(expectedCase2, myBuddy.display());
    }

    @Test
    public void testDeleteUnit() {
        
        int cases = 5;
        
        String[] inputs = new String[5];
        inputs[0] = "1";
        inputs[1] = "Adele";
        inputs[2] = "2";
        inputs[3] = "100000";
        inputs[4] = "-5";
        
        String content1 = "There's a fire starting in my heart";
        String content2 = "Reaching a fever pitch, it's bringing me out the dark";
        String content3 = "Finally I can see you crystal clear";

        
        String[] expected = new String[5];
        expected[0] = "the specified line number %1$s exceeds the range of %2$s";
        expected[1] = "\"%1$s\" cannot be parsed as a line number";
        expected[2] = "deleted from %1$s: \"%2$s\"";
        expected[3] = "the specified line number %1$s exceeds the range of %2$s";
        expected[4] = "the specified line number %1$s exceeds the range of %2$s";

        expected[0] = String.format(expected[0], inputs[0], filename);
        expected[1] = String.format(expected[1], inputs[1]);
        expected[2] = String.format(expected[2], filename, content2);
        expected[3] = String.format(expected[3], inputs[3], filename);
        expected[4] = String.format(expected[4], inputs[4], filename);
        
        Assert.assertEquals(expected[0], myBuddy.delete(inputs[0]));
        
        myBuddy.add(content1);
        myBuddy.add(content2);
        myBuddy.add(content3);
        
        for (int i = 1; i < cases; ++i) {
            Assert.assertEquals(expected[i], myBuddy.delete(inputs[i]));
        }
    }

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
