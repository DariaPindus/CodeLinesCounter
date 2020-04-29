import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodeLinesCounterTest {

    @Test
    public void testFileExists() {
        CodeLinesCounter counter = new CodeLinesCounter("");
        //assertThrows(FileNotFoundException.class, () -> )
    }
}
