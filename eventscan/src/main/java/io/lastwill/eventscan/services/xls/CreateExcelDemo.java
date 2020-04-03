package io.lastwill.eventscan.services.xls;

import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import io.lastwill.eventscan.services.RandomMd5Generator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class CreateExcelDemo {
    private final String weight = "Weight";
    private final String countryOfOrigin = "Country of Origin";
    private final String certifiedAssayer = "Certified Assayer";
    private final String purchaseDate = "Purchase date";
    private final String secretCode = "Secret Code";
    private final String goldValueAtPurchaseDate = "Gold value at Purchase Date";
    private final String DUCvalue = "DUC value";
    @Value("${io.lastwill.eventscan.open-file-name}")
    private String openPath;
    @Value("${io.lastwill.eventscan.save-file-name}")
    private String savePath;
    Map<String, Integer> rowByType = new HashMap<>();

    private final int stopGenerate = 100;
    @Autowired
    private RandomMd5Generator generator;
    @Autowired
    private TokenEntryRepository tokenRepository;

    @PostConstruct
    public void init() throws IOException {
        addSecretCode();
        saveIntoDb();
    }

    public void addSecretCode() throws IOException {
        File file = new File(openPath);
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
                    rowByType.put(secretCode, cell.getRowIndex());
                }
                case weight: {
                    rowByType.put(weight, cell.getRowIndex());
                    break;
                }
                case countryOfOrigin: {
                    rowByType.put(countryOfOrigin, cell.getRowIndex());
                    break;
                }
                case certifiedAssayer: {
                    rowByType.put(certifiedAssayer, cell.getRowIndex());
                    break;
                }
                case purchaseDate: {
                    rowByType.put(purchaseDate, cell.getRowIndex());
                    break;
                }
                case goldValueAtPurchaseDate: {
                    rowByType.put(goldValueAtPurchaseDate, cell.getRowIndex());
                    break;
                }
                case DUCvalue: {
                    rowByType.put(DUCvalue, cell.getRowIndex());
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
            Cell cell = sheet.getRow(i).getCell(rowByType.get(secretCode));
            cell.setCellValue(code);

        }
        inputStream.close();
        // Write File
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }

    private void saveIntoDb() throws IOException {
//        File file = new File(openPath);
//        // Read XSL file
//        FileInputStream inputStream = new FileInputStream(file);
//
//        // Get the workbook instance for XLS file
//        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
//
//        // Get first sheet from the workbook
//        HSSFSheet sheet = workbook.getSheetAt(0);
//        Iterator<Row> rowIterator = sheet.iterator();
//        int rows = sheet.getLastRowNum();
//        Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
//        List<TokenInfo> tokens = new ArrayList<>();
//        for (int i = 1; i <= rows; i++) {
//            HSSFRow row = sheet.getRow(i);
//
//            tokens.add(new TokenInfo());
//        }
    }

    public Set<String> generateUnique(int rows, int count) {
        Set<String> result = new HashSet<>();
        if (count > stopGenerate) {
            return result;
        }
        Set<String> codes = generator.generateMoreMd5Random(rows);
        List<TokenInfo> repeats = tokenRepository.findAllBySecretCode(codes);
        if (repeats != null && repeats.size() > 0) {
            result = this.generateUnique(rows, ++count);
        } else {
            result = codes;
        }
        return result;
    }
}