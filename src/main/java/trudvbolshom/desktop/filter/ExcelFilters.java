package trudvbolshom.desktop.filter;

import org.apache.poi.ss.usermodel.Cell;
import trudvbolshom.desktop.model.ExcelWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelFilters {
    private ExcelWorker excelWorker;

    public ExcelFilters(ExcelWorker excelWorker) {
        this.excelWorker = excelWorker;
    }

    public Map<Integer, List<Cell>> getByEmployer(String name) {
        List<Cell> employers = excelWorker.getColumnData(0)
                .stream()
                .filter(cell -> cell.getStringCellValue().equals(name))
                .collect(Collectors.toList());

        List<Integer> columnIndexes = employers.stream().map(Cell::getRowIndex).collect(Collectors.toList());
        Map<Integer, List<Cell>> findEmployers = new HashMap<>();

        for(int index : columnIndexes){
            findEmployers.put(index, excelWorker.getExcelDocumentData().getDataByRows().get(index + 1));
        }

        return findEmployers;
    }
}
