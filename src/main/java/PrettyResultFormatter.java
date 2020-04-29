import java.util.ArrayList;
import java.util.List;

public class PrettyResultFormatter implements ResultFormatter {
    private List<String> formatted = new ArrayList<>();

    @Override
    public List<String> formatCodeLineResult(CodeLineResult result) {
        getPaddedStrings("", result);
        return formatted;
    }

    private void getPaddedStrings(String padding, CodeLineResult result) {
        formatted.add(padding + result.getFileName().toFile().getName() + ": " + result.getCodeLinesCount());
        for (CodeLineResult childResult : result.getChildren()) {
            getPaddedStrings(padding + "    ", childResult);
        }
    }
}
