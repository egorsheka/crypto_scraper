import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CryptoScrapper {

    private final static String DEBANK_MAIN_URL = "https://debank.com/profile/";
    private final static String INPUT_FILE_NAME = "codes.txt";
    private final static String OUTPUT_FILE_NAME = "result.txt";
    private final static String BALANCE_XPATH_VALUE = "//div[@class='HeaderInfo_totalAssetInner__1mOQs HeaderInfo_curveEnable__3Q3u3']";

    public static void main(String[] args) throws InterruptedException {
        ChromeDriver chromeDriver = setupChromeDriver();
        WebDriverWait waitingDriver = new WebDriverWait(chromeDriver, Duration.ofMillis(15000L));

        List<String> codes = readLinesFromFile(INPUT_FILE_NAME);

        System.out.println("Reading is finished.");
        System.out.println("==================================================");
        System.out.println("Starting scraping process...");

        List<Pair> pairList = new ArrayList<>();
        for (String code : codes) {
            String url = DEBANK_MAIN_URL + code;
            Thread.sleep(2000);
            chromeDriver.get(url);

            System.out.println("Processing URL: " + url);

            WebElement element = waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BALANCE_XPATH_VALUE)));
            String value = element.getText();

            System.out.print("\nValue is " + value);

            if (value.contains("$0")) {

                System.out.println(". So, if value = 0, trying again...");

                Thread.sleep(7000);
                element = waitingDriver.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BALANCE_XPATH_VALUE)));
                value = element.getText();

                System.out.println("Value is " + value);
            }
            if (value.contains("\n")) {
                value = value.substring(value.indexOf("$") + 1, value.indexOf("\n"));
            } else {
                value = value.substring(value.indexOf("$") + 1);
            }
            pairList.add(new Pair(code, value));
        }
        chromeDriver.quit();
        System.out.println("\n==================================================");
        System.out.println("Starting writing data to file ...");

        try (FileWriter fileWriter = new FileWriter(OUTPUT_FILE_NAME);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (Pair pair : pairList) {
                bufferedWriter.write(pair.toString());
                bufferedWriter.newLine();
            }

            System.out.println("Successfully written lines to the file.");

        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
        }

        System.out.println("Writing has been finished..");
        System.out.println("==================================================");

        System.out.println("Press Enter to exit..");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ChromeDriver setupChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        return new ChromeDriver(options);
    }

    public static List<String> readLinesFromFile(String fileName) {
        System.out.println("==================================================");
        System.out.println("Reading from file...");
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lines.add(line.trim());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static String getFilePath(String fileName) throws URISyntaxException {
        Path path = Paths.get(CryptoScrapper.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String jarDir = path.getParent().toString();
        return jarDir + "/" + fileName;
    }

    public static class Pair {
        public String code;
        public String value;

        public Pair(String code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public String toString() {
            return code + ':' + value;
        }
    }
}
