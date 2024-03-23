package errorFunction;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.numbers.gamma.Erf;
public class Integration {
    public static final String SEPARATOR = "----------------";

    public static double getIntegralValue(int method, double[] funcValues, double from, double to) {
        int intervalsCount = funcValues.length-1;

        double h = getIntervalLength(from, to, intervalsCount);
        double integral=0;
        switch (method) {
            case (1):
                integral = h * (funcValues[0] + funcValues[intervalsCount]) / 2;
                for (int i = 1; i < intervalsCount; i++) {
                    integral += h * funcValues[i];
                }
                break;
            case (2):
                integral = h * (funcValues[0] + funcValues[intervalsCount]) / 3;
                for (int i = 1; i < intervalsCount; i += 2) {
                    integral += h * 4 * funcValues[i] / 3;
                }
                for (int i = 2; i < intervalsCount - 1; i += 2) {
                    integral += h * 2 * funcValues[i] / 3;
                }
                break;

        }
        return integral;
    }

    public static double error ( int method, double from, double to, int intervalsCount){
        double[] funcValues1;
        double[] funcValues2;
        double err = 0;
        switch (method) {
            case (1):
                funcValues1 = trapezium(from, to, intervalsCount);
                funcValues2 = trapezium(from, to, intervalsCount / 2);
                err = Math.abs(getIntegralValue(method, funcValues1, from, to) - getIntegralValue(method, funcValues2, from, to)) / 3;
                break;
            case (2):
                funcValues1 = simpson(from, to, intervalsCount);
                funcValues2 = simpson(from, to, intervalsCount / 2);
                err = Math.abs(getIntegralValue(method, funcValues1, from, to) - getIntegralValue(method, funcValues2, from, to)) / 15;
                break;
        }
        return err;
    }
    public static void graphOfError ( int method, double from, double to, int intervalsCount) throws IOException {
        double h;
        FileOutputStream fileOutputStream = new FileOutputStream("graphOfError.txt");
        String str;
        double err = 0;
        for (int i = 4; i <= intervalsCount; i += 2) {
            err = error(method, from, to, i);
            h = getIntervalLength(from, to, i);
            str = String.format("%15.5f \t %15.7e %n", h, err);

            fileOutputStream.write(str.getBytes());
        }
        fileOutputStream.close();
    }

    public static void graphOfFunction ( int method) throws IOException {
        double h=0.004;
        FileOutputStream fileOutputStream = new FileOutputStream("graphOfFunction.txt");
        String str;
        double[] funcValues;
        double err = 0;
        int intervalsCount;
        double x=0;
        double y=0;
        double ytheor=0;
        str="\t   x      \t\t   y(x)     \t\ty_theory(x)\n";
        fileOutputStream.write(str.getBytes());
        str = String.format("%15.1f \t %15.1f \t %15.1f %n", x, y, ytheor);
        fileOutputStream.write(str.getBytes());
        for (intervalsCount = 2; intervalsCount <= 1000; intervalsCount+=2) {
            x=intervalsCount*h;
            switch (method) {
                case (1):
                    funcValues = trapezium(0, x, intervalsCount);
                    break;
                case (2):
                    funcValues = simpson(0, x, intervalsCount);
                    break;
                default:
                    funcValues = trapezium(0, x, intervalsCount);
            }
            y=getIntegralValue(method, funcValues, 0, x);
            ytheor=getDefiniteIntegral(x);
            str = String.format("%15.5f \t %15.7f \t %15.7f %n", x, y, ytheor);
            fileOutputStream.write(str.getBytes());
            if(intervalsCount==1000){
                err = error(method, 0, x, intervalsCount);
                fileOutputStream.write("Error of method: ".getBytes());
                fileOutputStream.write(String.valueOf(err).getBytes());}
            funcValues = null;
        }
        fileOutputStream.close();
    }
    public static double getDefiniteIntegral (double to){
        return Erf.value(to);
    }

    public static void main (String[]args) throws IOException {
        double from = 0, to = -0.02;
        int intervalsCount = 500;
        int method = 1;
        if (intervalsCount % 2 != 0)
            intervalsCount--;
        double[] funcValues;
while (true){
        Scanner scanner = new Scanner(System.in);
    System.out.println("Enter x to calculate the error function:");
    while (true){
        String str = scanner.nextLine();
        str = str.replace(',', '.');
    if (str != null && str.matches("-?\\d+(\\.\\d+)?")) {
        to = Double.parseDouble(str);
        break;
    } else {
        System.err.println("The entered number is incorrect");
        System.out.println("Enter x:");
    }}

        System.out.println("Choose a method:");
        System.out.println("1. trapezium  method");
        System.out.println("2. Simpson method");
        System.out.println("3. Exit");

        int choice =1;
    if (scanner.hasNextInt())
        choice = scanner.nextInt();
        switch (choice) {
            case (1):
                method = 1;
                funcValues = trapezium(from, to, intervalsCount);
                break;
            case (2):
                method = 2;
                funcValues = simpson(from, to, intervalsCount);
                break;
            case (3):
              return;
            default: continue;
        }


        table(from, intervalsCount, to, funcValues);
        System.out.println(getIntegralValue(method, funcValues, from, to));
        System.out.println("Error: " + error(method, from, to, intervalsCount));

        System.out.println("Definite integral:");

        System.out.println(getDefiniteIntegral(to));
        toFile(from, intervalsCount, to, funcValues);
        graphOfError(method, from, to, intervalsCount);
        graphOfFunction(method);
    System.out.println("Select next steps:");
    System.out.println("1. Continue");
    System.out.println("2. Exit");

    int choice2 =1;
    if (scanner.hasNextInt())
        choice2 = scanner.nextInt();
    switch (choice2) {
        case (1):
          break;
        case (2):
           return;
        default: return;
    }

}}

    public static double getIntervalLength ( double from, double to, int intervalsCount){
        return (to - from) / (intervalsCount);
    }

    public static double[] trapezium ( double from, double to, int intervalsCount){
        double[] funcValues = new double[intervalsCount+1];
        double intervalLength = getIntervalLength(from, to, intervalsCount);
        double x = from;
        for (int i = 0; i <= intervalsCount; i++, x += intervalLength) {
            funcValues[i] = 2/(Math.sqrt(Math.PI))*Math.exp(-x*x/2);
        }
        return funcValues;
    }

    public static double[] simpson ( double from, double to, int intervalsCount){
        if (intervalsCount % 2 != 0)
            intervalsCount--;
        double[] funcValues = new double[intervalsCount+1];
        double intervalLength = getIntervalLength(from, to, intervalsCount);
        double x = from;
        for (int i = 0; i <= intervalsCount; i++, x += intervalLength) {
            funcValues[i] = 2/(Math.sqrt(Math.PI))*Math.exp(-x*x/2);
        }
        return funcValues;
    }


    public static void table ( double from, int intervalsCount, double to, double[] funcValues){
        double x = from;
        double intervalLength = getIntervalLength(from, to, intervalsCount);
        String format = "|%1$-16s|%2$-16s|\n";
        System.out.format(format, SEPARATOR, SEPARATOR);
        System.out.format(format,  "        x       ", "      y(x)      ");
        System.out.format(format, SEPARATOR, SEPARATOR);
        for (int i = 0; i <= intervalsCount; i++, x += intervalLength) {
            System.out.format("|%15.7f |%15.7f |%n", x, funcValues[i]);
            System.out.format(format, SEPARATOR, SEPARATOR);

        }
    }

    public static void toFile ( double from, int intervalsCount, double to, double[] funcValues) throws IOException
    {
        double x = from;
        double intervalLength = getIntervalLength(from, to, intervalsCount);
        FileOutputStream fileOutputStream = new FileOutputStream("integralOfFunction.txt");
        String str;
        for (int i = 0; i <= intervalsCount; i++, x += intervalLength) {
            str = String.format("%15.7f \t %15.7f %n", x, funcValues[i]);

            fileOutputStream.write(str.getBytes());
        }
        fileOutputStream.close();

    }
}
