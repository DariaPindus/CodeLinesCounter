public class Main {
    public static void main(String[] args) {
        String filename = args[0];

        JavaCodeLinesCounter codeLinesCounter = new JavaCodeLinesCounter(filename);
        ConsoleLineCounterPrinter printer = new ConsoleLineCounterPrinter(new PrettyResultFormatter(), codeLinesCounter.countLinesInFile());
        printer.print();
    }
}
