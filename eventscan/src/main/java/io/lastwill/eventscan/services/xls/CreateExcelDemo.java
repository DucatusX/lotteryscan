package io.lastwill.eventscan.services.xls;

import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.TokenType;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import io.lastwill.eventscan.services.RandomMd5Generator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Component
@Slf4j
public class CreateExcelDemo {
    private final String weight = "Weight";
    private final String country = "Country";
    private final String certifiedAssayer = "Certified Assayer";
    private final String purchaseDate = "Purchase date";
    private final String secretCode = "Secret Code";
    private final String goldPrice = "Gold price";
    private final String ducValue = "DUC value";
    @Value("${io.lastwill.eventscan.open-file-name}")
    private String openPath;
    @Value("${io.lastwill.eventscan.save-file-path}")
    private String savePath;
    @Value("${io.lastwill.eventscan.save-file-name}")
    private String saveName;
    Map<String, Integer> rowByType = new HashMap<>();

    private final int stopGenerate = 100;
    @Autowired
    private RandomMd5Generator generator;
    @Autowired
    private TokenEntryRepository tokenRepository;

    @PostConstruct
    public void init() throws IOException {
        System.out.println(System.getProperty("user.dir"));
        addSecretCode();
        saveIntoDb();
    }

    public void addSecretCode() throws IOException {
        File file = new File(openPath);
        if (!file.exists()) {
            log.info("File for configure secret code is not exist");
            return;
        }
        // Read XSL file
        FileInputStream inputStream = new FileInputStream(file);

        // Get the workbook instance for XLS file
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

        // Get first sheet from the workbook
        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        int rows = sheet.getLastRowNum();
        Set<String> uniqueCodes = generateUnique(rows, 0);
        if (uniqueCodes.isEmpty()) {
            log.warn("Can't generate {} unique codes!", rows);
            return;
        }
        Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellName = cell.getStringCellValue();
            switch (cellName) {
                case secretCode: {
                    rowByType.put(secretCode, cell.getColumnIndex());
                    break;
                }
                case weight: {
                    rowByType.put(weight, cell.getColumnIndex());
                    break;
                }
                case country: {
                    rowByType.put(country, cell.getColumnIndex());
                    break;
                }
                case certifiedAssayer: {
                    rowByType.put(certifiedAssayer, cell.getColumnIndex());
                    break;
                }
                case purchaseDate: {
                    rowByType.put(purchaseDate, cell.getColumnIndex());
                    break;
                }
                case goldPrice: {
                    rowByType.put(goldPrice, cell.getColumnIndex());
                    break;
                }
                case ducValue: {
                    rowByType.put(ducValue, cell.getColumnIndex());
                    break;
                }
                default:
                    break;
            }
        }
        if (rowByType.size() != 7) {
            log.warn("Find only fields {}", rowByType.size());
            return;
        }
        Iterator<String> codeIterator = uniqueCodes.iterator();
        for (int i = 1; i <= rows; i++) {
            if (!codeIterator.hasNext()) {
                return;
            }
            String code = codeIterator.next();
            int secretCell = rowByType.get(secretCode);
            Row row = sheet.getRow(i);
            Cell cell = row.createCell(secretCell, CellType.STRING);
            cell.setCellValue(code);

        }
        log.info("Secret code successfully generate");
        inputStream.close();
        // Write File
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
        log.info("File with source code save");
    }

    private void saveIntoDb() throws IOException {
        File file = new File(openPath);
        if (!file.exists()) {
            log.info("File for saveDB is not exist");
            return;
        }
        // Read XSL file
        FileInputStream inputStream = new FileInputStream(file);

        // Get the workbook instance for XLS file
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

        // Get first sheet from the workbook
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum();
        List<TokenInfo> tokens = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            HSSFRow row = sheet.getRow(i);
            String tUserId = row.getCell(rowByType.get(secretCode)).getStringCellValue();
            String tokenName = row.getCell(rowByType.get(weight)).getStringCellValue();
            TokenType tTokenType = Stream.of(TokenType.values()).filter(t -> t.getName().equalsIgnoreCase(tokenName)).findFirst().get();
            String tAssayer = row.getCell(rowByType.get(certifiedAssayer)).getStringCellValue();
            String tCountry = row.getCell(rowByType.get(country)).getStringCellValue();
            String tPurchaseDate = row.getCell(rowByType.get(purchaseDate)).getStringCellValue();
            BigDecimal tDucValue = BigDecimal.valueOf(row.getCell(rowByType.get(ducValue)).getNumericCellValue());
            BigDecimal tGoldPrice = BigDecimal.valueOf(row.getCell(rowByType.get(goldPrice)).getNumericCellValue());

            tokens.add(new TokenInfo(tUserId, tTokenType, false, tAssayer, tCountry, tDucValue, tGoldPrice, tPurchaseDate));
        }
        tokenRepository.save(tokens);
        log.info("All new Token Info entry successfully save into DB");
        inputStream.close();
        // Write File
        String newDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String saveFileName = savePath + File.separator + newDate + File.separator + saveName;
        new File(savePath + File.separator + newDate).mkdirs();
        FileOutputStream out = new FileOutputStream(saveFileName);
        workbook.write(out);
        out.close();
        //Delete old File
        File fileForDelete = new File(openPath);
        fileForDelete.delete();
        log.info("New File with Secret code save into {}", saveFileName);
        log.info("Old file was  dropped {}", openPath);
    }

    public Set<String> generateUnique(int rows, int count) {
        Set<String> result = new HashSet<>();
        if (count > stopGenerate) {
            log.warn("Can't generate unique code more than {} times", stopGenerate);
            return result;
        }
        Set<String> codes = generator.generateMoreMd5Random(rows);
        List<TokenInfo> repeats = tokenRepository.findAllBySecretCode(codes);
        if (repeats != null && repeats.size() > 0) {
            result = this.generateUnique(rows, ++count);
        } else {
            result = codes;
        }
        log.info("Secret code successfully generated");
        return result;
    }
}