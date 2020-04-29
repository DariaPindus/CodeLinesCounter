import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JavaCodeLinesCounter {

    private static final String END_OF_MULTILINE_COMMENT = "*/";
    private static final String BEGGING_OF_MULTILINE_COMMENT = "/*";
    private static final String INLINE_COMMENT_REGEX = "\\/\\*.*\\*\\/";
    private static final String ONE_LINE_COMMENT_REGEX = "\\/\\/.*";
    
    private enum CODE_STATE {
        MULTILINE_OPENED,
        CODE
    }

    private final Path filePath;
    private int lineCounter = 0;

    private List<JavaCodeLinesCounter> children;

    public JavaCodeLinesCounter(String fileName) {
        validateFileInput(fileName);
        this.filePath = Paths.get(fileName);
        setChildrenIfNeeded();
    }

    private void setChildrenIfNeeded() {
        if (!Files.isDirectory(this.filePath))
            return;

        try {
            children = Files.list(this.filePath).map(path -> new JavaCodeLinesCounter(path.toString())).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Error iterating subfolder " + e.getMessage());
        }
    }

    private void validateFileInput(String sourceName) {
        if (sourceName == null || sourceName.isEmpty())
            throw new IllegalArgumentException("Empty source name");
        if (!Files.exists(Paths.get(sourceName)))
            throw new IllegalArgumentException("Incorrect filename: " + sourceName);
    }

    public CodeLineResult countLinesInFile() {
        if (Files.isDirectory(filePath)) {
            CodeLineResult result = new CodeLineResult(filePath);
            children.forEach(childCounter -> {
                result.addChild(childCounter.countLinesInFile());
            });
            return result;
        } else {
            countLines();
            return new CodeLineResult(filePath, lineCounter);
        }
    }

    private void countLines() {
        try {
            CODE_STATE currentCodeState = CODE_STATE.CODE;

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty())
                    continue;

                currentCodeState = getLineState(currentCodeState, trimmed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CODE_STATE getLineState(CODE_STATE currentState, String codeLine) {
        if (codeLine.isEmpty())
            return currentState;

        int endMultilineIndex = indexOfOccurrenceNotInString(codeLine, END_OF_MULTILINE_COMMENT);
        if (currentState == CODE_STATE.MULTILINE_OPENED && endMultilineIndex < 0)
            return CODE_STATE.MULTILINE_OPENED;

        if (currentState == CODE_STATE.MULTILINE_OPENED)
            return getLineState(CODE_STATE.CODE, codeLine.substring(endMultilineIndex + 2));

        String enclosedCommentTrimmed = codeLine.replaceAll(INLINE_COMMENT_REGEX, "");
        String oneLineCommentTrimmed = enclosedCommentTrimmed.replaceAll(ONE_LINE_COMMENT_REGEX, "");

        int startMultilineIndex = indexOfOccurrenceNotInString(oneLineCommentTrimmed, BEGGING_OF_MULTILINE_COMMENT);

        if (startMultilineIndex == 0)
            return CODE_STATE.MULTILINE_OPENED;
        if (!oneLineCommentTrimmed.isEmpty())
            lineCounter++;
        return startMultilineIndex > 0 ? CODE_STATE.MULTILINE_OPENED : CODE_STATE.CODE;
    }

    // multiline comment characters inside string should not affect result
    private int indexOfOccurrenceNotInString(String source, String match) {
        int indexOfMatch = source.indexOf(match);
        Matcher enquotedGroup = Pattern.compile("(\"([^\"]|\"\")*\")").matcher(source);
        if (enquotedGroup.find()) {
            return (indexOfMatch > enquotedGroup.start() && indexOfMatch < enquotedGroup.end()) ?
                    -1 : indexOfMatch;
        }
        return indexOfMatch;
    }
}
