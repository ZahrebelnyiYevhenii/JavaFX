package trudvbolshom.desktop.model.list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import trudvbolshom.desktop.model.reader.FileReader;
import trudvbolshom.desktop.model.reader.excel.ExcelReader;

import static trudvbolshom.constants.ConstantsClass.ROW_NUMBER_WITHOUT_TITLES;

public class RowList {
    private final ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
    private final FileReader fileReader;

    public RowList(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public void fillRows() {
        for (int rowNumber = ROW_NUMBER_WITHOUT_TITLES; rowNumber < getCountRows(); rowNumber++) {
            rows.add(getExcelRowData(rowNumber));
        }
    }

    private int getCountRows() {
        return ((ExcelReader) fileReader).getDataByRows().size();
    }

    private ObservableList<String> getExcelRowData(int rowNumber) {
        ObservableList<String> row = FXCollections.observableArrayList();

        row.addAll(fileReader.getRowData(rowNumber));

        return row;
    }

    public ObservableList<ObservableList<String>> getRows() {
        return rows;
    }
}
