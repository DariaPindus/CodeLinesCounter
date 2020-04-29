public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please, specify one parameter (file or folder name)");
            return;
        }

        String filename = args[0];

        JavaCodeLinesCounter codeLinesCounter = new JavaCodeLinesCounter(filename);
        ConsoleLineCounterPrinter printer = new ConsoleLineCounterPrinter(new PrettyResultFormatter(), codeLinesCounter.countLinesInFile());
        printer.print();
    }
}
