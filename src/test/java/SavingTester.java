import scrimfinder.SavingTesters;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SavingTester {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        SavingTesters.nonMainSavingTester();
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        SavingTesters.viaMainSavingTester();
    }
}
