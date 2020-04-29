import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

enum CODE_STATE {
    MULTILINE_OPENED,
    CODE
}

public class FileCodeLinesCounter {
    private final Path filePath;
    private int lineCounter = 0;

    private List<FileCodeLinesCounter> children;

    FileCodeLinesCounter(String fileName)  {
        validateFileInput(fileName);
        this.filePath = Paths.get(fileName);
        if (Files.isDirectory(this.filePath)) {
            try {
                children = Files.list(this.filePath).map(path -> new FileCodeLinesCounter(path.toString())).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateFileInput(String sourceName) {
        if (!Files.exists(Paths.get(sourceName)))
            throw new IllegalArgumentException("Incorrect filename");
    }

    private void countLines() {
        try {
            CODE_STATE currentCodeState = CODE_STATE.CODE;

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8); //TODO: charset
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty())
                    continue;

                CODE_STATE thisLineState = getLineState(currentCodeState, trimmed);
                currentCodeState = thisLineState;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    CodeLineResult getLines() {
        if (Files.isDirectory(filePath)) {
            CodeLineResult result = new CodeLineResult(filePath);
            children.forEach(childCounter -> {
                result.addChild(childCounter.getLines());
            });
            return result;
        }
        else {
            countLines();
            return new CodeLineResult(filePath, lineCounter);
        }
    }

    private CODE_STATE getLineState(CODE_STATE currentState, String codeLine) {
        if (codeLine.isEmpty())
            return currentState;

        int endMultilineIndex = indexOfOccurrenceNotInString(codeLine, "*/");
        if (currentState == CODE_STATE.MULTILINE_OPENED && endMultilineIndex < 0)
            return CODE_STATE.MULTILINE_OPENED;

        if (currentState == CODE_STATE.MULTILINE_OPENED && endMultilineIndex > -1)
            return getLineState(CODE_STATE.CODE, codeLine.substring(endMultilineIndex + 2));

        String enclosedCommentTrimmed = codeLine.replaceAll("\\/\\*.*\\*\\/", "");
        String oneLineCommentTrimmed = enclosedCommentTrimmed.replaceAll("\\/\\/.*", "");

        int startMultilineIndex = indexOfOccurrenceNotInString(oneLineCommentTrimmed, "/*");

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
        if (enquotedGroup.find()){
            return (indexOfMatch > enquotedGroup.start() && indexOfMatch < enquotedGroup.end()) ?
                    -1 : indexOfMatch;
        }
        return indexOfMatch;
    }
}
