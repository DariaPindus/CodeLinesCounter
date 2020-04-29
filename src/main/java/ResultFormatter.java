import java.util.List;

@FunctionalInterface
public interface ResultFormatter {
    List<String> formatCodeLineResult(CodeLineResult result);
}
