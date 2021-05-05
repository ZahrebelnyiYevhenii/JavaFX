package trudvbolshom.desktop.model.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import trudvbolshom.exception.ExcelWorkerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

import static trudvbolshom.constants.ConstantsClass.XLS;
import static trudvbolshom.constants.ConstantsClass.XLSX;

public class ExcelWorker {
    private Workbook excelBook;
    private ExcelDocumentData excelDocumentData;
    private int counter = 0;

    public ExcelWorker(String pathExcel) throws ExcelWorkerException {
        File file = new File(pathExcel);
        excelDocumentData = new ExcelDocumentData();

        try {
            if (pathExcel.endsWith(XLSX)) {
                excelBook = new XSSFWorkbook(file);
                initData();
            } else if (pathExcel.endsWith(XLS)) {
                excelBook = new HSSFWorkbook(new FileInputStream(file));
                initData();
            } else {
                throw new InvalidFormatException("");
            }
        } catch (InvalidFormatException | IOException e) {
            throw new ExcelWorkerException("$$$$$ excel file - " + pathExcel + " is not found $$$$$");
        }
    }

    public void initData() {
        Sheet sheet = excelBook.getSheetAt(0);
        counter = 0;

        for (Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = rowIterator.next();
            List<Cell> cellOfRow = new ArrayList<>();
            counter++;
            for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                Cell cell = cellIterator.next();
                cellOfRow.add(cell);
            }

            excelDocumentData.getDataByRows().put(counter, cellOfRow);
        }
    }

    public List<String> getTitles() {
        return excelDocumentData.getDataByRows().get(1).stream().map(Cell::getStringCellValue).collect(Collectors.toList());
    }

    public List<Cell> getColumnData(int indexColumnData) {
        return excelDocumentData.getDataByRows().values().stream().skip(1).map(row -> row.get(indexColumnData)).collect(Collectors.toList());
    }

    public List<String> getColumnData(String columnName) {
        for (int i = 0; i <= getTitles().size(); i++) {
            if (getTitles().get(i).equals(columnName)) {
                int finalI = i;
                List<String> result = new ArrayList<>();

                excelDocumentData.getDataByRows().values().stream().skip(1).filter(row -> row.size() > 0).forEach((x) -> {
                    if (x.get(finalI).getCellType().equals(CellType.STRING)) {
                        result.add(String.valueOf(x.get(finalI).getStringCellValue()));
                    } else if (x.get(finalI).getCellType().equals(CellType.NUMERIC)) {
                        if (DateUtil.isCellDateFormatted(x.get(finalI))) {
                            result.add(getFormatDate(finalI, x));
                        } else {
                            result.add((String.valueOf(x.get(finalI).getNumericCellValue())));
                        }
                    }
                });
                return result;
            }
        }
        return null;
    }

    private String getFormatDate(int finalI, List<Cell> x) {
        return DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(new Locale("ru"))
                .format(x.get(finalI).getLocalDateTimeCellValue().toLocalDate());
    }

    public Map<String, List<String>> getTableData() {
        Map<String, List<String>> tableData = new HashMap<>();
        getTitles().forEach(title -> tableData.put(title, getColumnData(title)));
        return tableData;
    }

    public ExcelDocumentData getExcelDocumentData() {
        return excelDocumentData;
    }

    public int getCountRows() {
        return counter - 1;
    }
}
