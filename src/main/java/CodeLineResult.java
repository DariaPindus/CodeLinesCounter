import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CodeLineResult {
    private final Path fileName;
    private int codeLinesCount;

    private List<CodeLineResult> children = new ArrayList<>();

    public CodeLineResult(Path fileName) {
        this.fileName = fileName;
    }

    public CodeLineResult(Path fileName, int codeLinesCount) {
        this(fileName);
        this.codeLinesCount = codeLinesCount;
    }

    public Path getFileName() {
        return fileName;
    }

    public int getCodeLinesCount() {
        if (children.isEmpty())
            return codeLinesCount;
        else return children.stream().map(CodeLineResult::getCodeLinesCount).reduce(0, Integer::sum);
    }

    public void addChild(CodeLineResult child) {
        children.add(child);
    }

    public List<CodeLineResult> getChildren() {
        return children;
    }
}
