import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class SavingTester {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        SavingTesters.nonMainSavingTester();
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        SavingTesters.viaMainSavingTester();
    }
}
