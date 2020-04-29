import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodeLinesCounterTest {

    @Test
    public void testFileNames() {
        assertThrows(IllegalArgumentException.class, () -> new JavaCodeLinesCounter(".\\suchfile\\doesnt_exist.java"), "Incorrect filename");
        assertThrows(IllegalArgumentException.class, () -> new JavaCodeLinesCounter(null), "Empty source name");
    }

    @Test
    public void testEmptySources() {
        String path = "src\\test\\resources\\dir1\\empty.java";
        JavaCodeLinesCounter codeLinesCounter = new JavaCodeLinesCounter(path);
        assertEquals(0, codeLinesCounter.countLinesInFile().getCodeLinesCount());

        String folderPath = "src\\test\\resources\\emptydir";
        JavaCodeLinesCounter codeLinesCounter2 = new JavaCodeLinesCounter(folderPath);
        assertEquals(0, codeLinesCounter2.countLinesInFile().getCodeLinesCount());
    }

    @Test
    public void testSingleFile() {
        String path1 = "src\\test\\resources\\dir1\\subdir1\\1.java";
        String path2 = "src\\test\\resources\\dir1\\2.txt";

        JavaCodeLinesCounter counter = new JavaCodeLinesCounter(path1);
        assertEquals(3,  counter.countLinesInFile().getCodeLinesCount());
        counter = new JavaCodeLinesCounter(path2);
        assertEquals(5,  counter.countLinesInFile().getCodeLinesCount());
    }

    @Test
    public void testRussianEncoding() {
        String path1 = "src\\test\\resources\\russian.java";
        JavaCodeLinesCounter codeLinesCounter = new JavaCodeLinesCounter(path1);
        assertEquals(9,  codeLinesCounter.countLinesInFile().getCodeLinesCount());
    }

    @Test
    public void testResultOutput() {
        String nestedPath = "src\\test\\resources\\dir1\\subdir1";
        JavaCodeLinesCounter codeLinesCounter = new JavaCodeLinesCounter(nestedPath);
        CodeLineResult result = codeLinesCounter.countLinesInFile();
        ResultFormatter formatter = new PrettyResultFormatter();
        List<String> formattedResults = formatter.formatCodeLineResult(result);
        assertEquals(formattedResults.size(), 2);
        assertEquals(formattedResults.get(0), "subdir1: 3");
        assertEquals(formattedResults.get(1).trim(), "1.java: 3");
    }
}
