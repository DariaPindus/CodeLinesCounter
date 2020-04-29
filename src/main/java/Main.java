public class Main {
    public static void main(String[] args) {

        String filename = "C:\\Users\\Asus\\Documents\\JavaProgs\\CodeLinesCounter\\src\\main\\resources\\dir1";
        FileCodeLinesCounter codeLinesCounter = new FileCodeLinesCounter(filename);
        CodeLineResult res = codeLinesCounter.getLines();
        printResult("", res);
    }

    private static void printResult(String padding, CodeLineResult result) {
        System.out.println(padding + result.getFileName().toFile().getParent() + ": " + result.getCodeLinesCount());
        for (CodeLineResult childResult : result.getChildren()) {
            printResult(padding + "    ", childResult);
        }
    }
}
