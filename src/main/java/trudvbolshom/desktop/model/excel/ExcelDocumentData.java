package trudvbolshom.desktop.model.excel;

import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDocumentData {

    private Map<Integer, List<Cell>> dataByRows;

    public ExcelDocumentData() {
        dataByRows = new HashMap<>();
    }

    public Map<Integer, List<Cell>> getDataByRows() {
        return dataByRows;
    }
}
