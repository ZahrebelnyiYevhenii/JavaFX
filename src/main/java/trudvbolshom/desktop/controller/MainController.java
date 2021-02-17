package trudvbolshom.desktop.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import trudvbolshom.desktop.model.excel.ExcelWorker;
import trudvbolshom.desktop.model.word.WordWorker;
import trudvbolshom.desktop.starter.AppFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static trudvbolshom.constants.ConstantsClass.*;

public class MainController {
    private AppFX appFX;
    private ExcelWorker excelWorker;
    private WordWorker wordWorker;

    @FXML
    private void loader() {
        loadExcelFile();
        fillTable();
        wordWorker = new WordWorker();
        fillDisplayElement();
    }

    private void loadExcelFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(appFX.getPrimaryStage());

            excelWorker = new ExcelWorker(file.getPath());

            fillLoadLabel(true);
        } catch (RuntimeException runException) {
            fillLoadLabel(false);
        }
    }

    private void fillLoadLabel(boolean isSuccess) {
        smallPanel.setVisible(true);
        loadLabel.setText(isSuccess ? SUCCESS : UNSUCCESSFUL);
    }

    private void fillTable() {
        table.setEditable(true);
        table.getColumns().clear();
        fillColumnCell();
        fillTableColumns();
    }

    private void fillColumnCell() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        for (int rowNumber = 0; rowNumber < excelWorker.getCountRows(); rowNumber++) {
            data.add(getExcelRowData(rowNumber));
        }

        table.setItems(data);
    }

    private ObservableList<String> getExcelRowData(int rowNumber){
        ObservableList<String> row = FXCollections.observableArrayList();

        for (String title : excelWorker.getTitles()) {
            row.add(excelWorker.getTableData().get(title).get(rowNumber));
        }

        return row;
    }

    private void fillTableColumns() {
        int columnNumber = 0;

        for (String title : excelWorker.getTitles()) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(title);

            int finalColumnNumber = columnNumber;
            column.setEditable(true);
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(finalColumnNumber)));
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> System.out.println(event.getTablePosition().getTableColumn().getCellData(event.getTablePosition().getRow())));

            table.getColumns().add(column);
            columnNumber++;
        }
    }

    @FXML
    private void createWordTemplate() throws FileNotFoundException {
        String templateFilePath = RESOURCE_FOLDER + TEMPLATE_DIR + choiceTemplate.getValue() + WORD_DOCUMENT;
        Map<Integer, String> rowData = getRowData();
        String reportFilePath = RESOURCE_FOLDER + REPORT_DIR + choiceTemplate.getValue() + "/" + rowData.get(NUMBER_NAME_COLUMN) + WORD_DOCUMENT;

        wordWorker.createWordDocument(templateFilePath, reportFilePath, rowData);
    }

    private Map<Integer, String> getRowData() {
        ObservableList<String> selectedRow = table.getSelectionModel().getSelectedItem();
        Map<Integer, String> rowData = new HashMap<>();

        for (String cellOnRow : selectedRow) {
            rowData.put(selectedRow.indexOf(cellOnRow), cellOnRow);
        }

        return rowData;
    }

    private void fillDisplayElement() {
        choiceTitle.setItems(FXCollections.observableArrayList(excelWorker.getTitles()));
        choiceTitle.setValue(excelWorker.getTitles().get(0));
        choiceTemplate.setItems(FXCollections.observableArrayList(wordWorker.getAllTemplateName()));
        choiceTemplate.setValue(wordWorker.getAllTemplateName().get(0));
    }

    @FXML
    private void close() {
        smallPanel.setVisible(false);
    }

    public void setAppFX(AppFX appFX) {
        this.appFX = appFX;
    }

    public ExcelWorker getExcelWorker() {
        return excelWorker;
    }

    public void setExcelWorker(ExcelWorker excelWorker) {
        this.excelWorker = excelWorker;
    }


    @FXML
    private AnchorPane smallPanel;
    @FXML
    private Label loadLabel;
    @FXML
    private TableView<ObservableList<String>> table;
    @FXML
    private ChoiceBox<String> choiceTitle;
    @FXML
    private ChoiceBox<String> choiceTemplate;
}
