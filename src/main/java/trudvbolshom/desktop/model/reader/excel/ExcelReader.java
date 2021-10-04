package trudvbolshom.desktop.model.reader.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import trudvbolshom.desktop.model.reader.FileReader;
import trudvbolshom.desktop.model.reader.excel.factory.ExcelFactory;
import trudvbolshom.desktop.model.reader.excel.factory.ExcelFactoryImpl;
import trudvbolshom.exception.ExcelWorkerException;
import trudvbolshom.exception.InvalidCellTypeException;
import trudvbolshom.exception.InvalidExcelTypeException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static trudvbolshom.constants.ConstantsClass.NUMBER_FIRST_SHEET;
import static trudvbolshom.constants.DateFormat.MEDIUM;

public class ExcelReader implements FileReader {
    private final Map<Integer, List<Cell>> dataByRows = new HashMap<>();
    private Workbook excelBook;

    public ExcelReader(String pathExcel) {
        try {
            readExcel(pathExcel);
        } catch (InvalidExcelTypeException | InvalidFormatException | IOException e) {
            throw new ExcelWorkerException("$$$$$ excel file - " + pathExcel + " is not found $$$$$");
        }
    }

    private void readExcel(String pathExcel) throws InvalidExcelTypeException, IOException, InvalidFormatException {
        ExcelFactory excelFactory = new ExcelFactoryImpl();

        excelBook = excelFactory.makeWorkBook(pathExcel);

        fillDataByRows();
    }

    @Override
    public void fillDataByRows() {
        int rowNumber = 0;
        Sheet sheet = excelBook.getSheetAt(NUMBER_FIRST_SHEET);

        for (Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            List<Cell> cellsOfRow = getListOfCells(rowIterator.next());

            dataByRows.put(rowNumber++, cellsOfRow);
        }
    }

    private List<Cell> getListOfCells(Row row) {
        List<Cell> cellsOfRow = new ArrayList<>();

        for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
            cellsOfRow.add(cellIterator.next());
        }

        return cellsOfRow;
    }

    @Override
    public List<String> getTitles() {
        return dataByRows.get(0).stream().map(this::parseCellData).collect(Collectors.toList());
    }

    @Override
    public List<String> getColumnData(int columnNumber) {
        List<String> cellsOfColumn = new ArrayList<>();

        dataByRows.values()
                .stream()
                .skip(1)
                .filter(row -> row.size() > 0)
                .forEach(row -> cellsOfColumn.add(parseCellData(row.get(columnNumber))));

        return cellsOfColumn;
    }

    @Override
    public List<String> getRowData(int rowNumber) {
        List<String> cellsOfRow = new ArrayList<>();

        dataByRows.get(rowNumber)
                .forEach(cell -> cellsOfRow.add(parseCellData(cell)));

        return cellsOfRow;
    }

    private String parseCellData(Cell cell) {
        if (cell.getCellType().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
            return parseNumericType(cell);
        }
        throw new InvalidCellTypeException("$$$$ cell type is not supported ");
    }

    private String parseNumericType(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return getFormatDate(cell);
        } else {
            return String.valueOf(cell.getNumericCellValue());
        }
    }

    private String getFormatDate(Cell cell) {
        return MEDIUM.format(cell.getLocalDateTimeCellValue().toLocalDate());
    }

    public Map<Integer, List<Cell>> getDataByRows() {
        return dataByRows;
    }
}
