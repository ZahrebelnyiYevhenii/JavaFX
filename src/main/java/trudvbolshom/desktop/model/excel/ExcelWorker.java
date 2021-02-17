package trudvbolshom.desktop.model.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelWorker {
    private XSSFWorkbook excelBook;
    private ExcelDocumentData excelDocumentData;

    public ExcelWorker(String pathExcel) {
        File file = new File(pathExcel);
        excelDocumentData = new ExcelDocumentData();

        try {
            OPCPackage pkg = OPCPackage.open(file);
            excelBook = new XSSFWorkbook(pkg);
            pkg.close();
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        initData();
    }

    public void initData() {
        XSSFSheet sheet = excelBook.getSheetAt(0);
        int counter = 0;

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

    public void showExcelData() {
        List<Cell> title = excelDocumentData.getDataByRows().get(1);
        title.forEach(cell -> System.out.print(cell + " | "));

        excelDocumentData.getDataByRows().remove(0);
        System.out.println();

        excelDocumentData.getDataByRows().forEach((in, list) -> {
            list.forEach(cell -> System.out.print(cell + " | "));
            System.out.println();
        });
    }

    public ExcelDocumentData getExcelDocumentData() {
        return excelDocumentData;
    }

    public List<String> getTitles(){
        return excelDocumentData.getDataByRows().get(1).stream().map(Cell::getStringCellValue).collect(Collectors.toList());
    }

    public int getCountRows(){
        int result = 0;

        for(String title : getTitles()){
            int row = getColumnData(title).size();

            if(result < row){
                result = row;
            }
        }

        return result;
    }

    public List<Cell> getColumnData(int indexColumnData){
        return excelDocumentData.getDataByRows().values().stream().skip(1).map(row -> row.get(indexColumnData)).collect(Collectors.toList());
    }

    public List<String> getColumnData(String columnName){
        for(int i = 0; i <= getTitles().size(); i++){
            if(getTitles().get(i).equals(columnName)) {
                int finalI = i;
                return excelDocumentData.getDataByRows().values().stream().skip(1).map(row -> row.get(finalI).getStringCellValue()).collect(Collectors.toList());
            }
        }
        return null;
    }

    public Map<String, List<String>> getTableData(){
        Map<String, List<String>> tableData = new HashMap<>();
        getTitles().forEach(title -> tableData.put(title, getColumnData(title)));
        return tableData;
    }
}
