package iaroslav.baranov.zerolambda.app;

import com.google.common.io.Files;
import iaroslav.baranov.zerolambda.Term;
import iaroslav.baranov.zerolambda.TermService;
import iaroslav.baranov.zerolambda.eval.EvaluationContext;
import iaroslav.baranov.zerolambda.parser.Parser;
import iaroslav.baranov.zerolambda.parser.ParsingEnv;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import io.vavr.collection.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ReplApp {
    public static void main(String[] args) throws IOException {
        System.setProperty("org.jline.terminal.dumb", "true");
        if(args.length > 0) {
            List<String> lines = List.ofAll(Files.readLines(new File(args[0]), UTF_8));
            new ReplApp(lines);
        } else {
            new ReplApp(List.empty());
        }
    }
    Terminal terminal = TerminalBuilder.builder()
            .system(true)
            .build();

    LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build();

    Parser parser = new Parser();

    TermService termService = new TermService();

    public ReplApp(List<String> lines) throws IOException {
        EvaluationContext context = new EvaluationContext();
        for (String line : lines) {
            eval(line, context);
        }
        while (true) {
            terminal.writer().print("λ> ");
            String input = reader.readLine();
            if (input.equals("exit")) {
                break;
            }
            eval(input, context);
        }
    }

    private void eval(String input, EvaluationContext context) {
        if(input.isEmpty() || input.startsWith("--")) {
            return;
        }
        try {
            if(input.contains(":=")) {
                String[] inputArray = input.split(":=");
                String key = inputArray[0].trim();
                String value = inputArray[1].trim();
                Term parsedValue = parser.parse(value, new ParsingEnv(context.getDefinitions()));
                context.addDefinition(key, parsedValue);
                context.setLastComputation(parsedValue);
                println(key + " := " + parsedValue, AttributedStyle.BLUE);
            } else if(input.startsWith("#")) {
                String[] commandAndArguments = input.substring(1).split(" ");
                String command = commandAndArguments[0].trim();
                if(command.equals("assertNumber")) {
                    String expectedNumberStr = commandAndArguments[1];
                    int expectedNumber = Integer.parseInt(expectedNumberStr);
                    Term actualNumberTerm = context.getLastComputation();
                    int actualNumber = termService.termToNumber(actualNumberTerm);
                    if(expectedNumber != actualNumber) {
                        throw new AssertionError("Expected: " + expectedNumber + ", actual: " + actualNumber);
                    } else {
                        println("Assertion passed: " + expectedNumber + " = " + actualNumber, AttributedStyle.CYAN);
                    }
                } else  if(command.equals("assertPrintable")) {
                    String expectedStr = input.substring(input.indexOf(' ') + 1);
                    Term actualTerm = context.getLastComputation();
                    Term normalForm = termService.getNormalFormUsingNormalOrder(actualTerm);
                    String actualStr = termService.toPrintableMain(normalForm);
                    if(!expectedStr.equals(actualStr)) {
                        throw new AssertionError("Expected Printable: " + expectedStr + ", actual: " + actualStr);
                    } else {
                        println("Assertion passed: " + expectedStr + " = " + actualStr, AttributedStyle.CYAN);
                    }
                } else if(command.equals("assertTrue")) {
                    Term actualTerm = context.getLastComputation();
                    boolean actual = termService.termToBoolean(actualTerm);
                    if(!actual) {
                        throw new AssertionError("Expected: " + true + ", actual: " + actual);
                    } else {
                        println("Assertion passed: " + true + " = " + true, AttributedStyle.CYAN);
                    }
                } else if(command.equals("assertFalse")) {
                    Term actualTerm = context.getLastComputation();
                    boolean actual = termService.termToBoolean(actualTerm);
                    if(actual) {
                        throw new AssertionError("Expected: " + false + ", actual: " + actual);
                    } else {
                        println("Assertion passed: " + false + " = " + false, AttributedStyle.CYAN);
                    }
                }
            } else {
                Term term = parser.parse(input, new ParsingEnv(context.getDefinitions()));
                println("Parsed:" + term);

                Term normalForm = termService.getNormalFormUsingNormalOrder(term);
                context.setLastComputation(normalForm);
                println("Normal Form: " + normalForm);
                if (termService.isNumber(normalForm)) {
                    println("Number: " + termService.termToNumber(normalForm));
                }
                if (termService.isBoolean(normalForm)) {
                    println("Boolean: " + termService.termToBoolean(normalForm));
                }

                if (termService.isPrintable(normalForm)) {
                    println("Printable: " + termService.toPrintableMain(normalForm));
                }

            }
        } catch (Exception e) {
            println("Error:" + e, AttributedStyle.RED);
            println("Input: " + input, AttributedStyle.RED);
            e.printStackTrace();
        }
    }

    private void println(String text, int color) {
        PrintWriter out = terminal.writer();
        out.println(new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(color))
                .append(text)
                .style(AttributedStyle.DEFAULT)
                .toAnsi()
        );
        out.flush();
    }

    private void println(String text) {
       println(text, AttributedStyle.GREEN);
    }
}
