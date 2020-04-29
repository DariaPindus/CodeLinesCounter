import java.util.List;

public class ConsoleLineCounterPrinter {
    private final ResultFormatter formatter;
    private final CodeLineResult result;

    public ConsoleLineCounterPrinter(ResultFormatter formatter, CodeLineResult result) {
        this.formatter = formatter;
        this.result = result;
    }

    public void print() {
        List<String> formattedResults = formatter.formatCodeLineResult(result);
        formattedResults.forEach(System.out::println);
    }

}
