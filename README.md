# CryptoScrapper

CryptoScrapper is a Java application that scrapes data from the Debank website to retrieve the value of cryptocurrencies based on provided key.

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven

### Usage

1. Place the list of cryptocurrency codes in a text file named `codes.txt`. Each code should be on a separate line.
2. Open the `CryptoScrapper` class and update the following constants if needed:
- `DEBANK_MAIN_URL`: The base URL of the Debank website.
- `INPUT_FILE_NAME`: The name of the input file containing the cryptocurrency codes.
3. Run the application