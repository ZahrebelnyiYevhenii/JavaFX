package trudvbolshom.desktop.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import trudvbolshom.desktop.model.ExcelWorker;
import trudvbolshom.desktop.starter.AppFX;

import java.io.File;

public class MainController {
    private static final String SUCCESS = "Файл успешно загружен";
    private static final String UNSUCCESSFUL = "Ошибка файла не загружен";
    private AppFX appFX;
    private ExcelWorker excelWorker;
    private ObservableList<ObservableList> data;

    @FXML
    private MenuItem load;
    @FXML
    private AnchorPane smallPanel;
    @FXML
    private Label loadLabel;
    @FXML
    private Button ok;
    @FXML
    private TableView table;

    @FXML
    private void loader() {
        loadExcelFile();
        fillTable();
    }

    private void loadExcelFile(){
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(appFX.getPrimaryStage());
            excelWorker = new ExcelWorker(file.getPath());
            excelWorker.initData();
            fillLoadLabel(true);
        } catch (RuntimeException runException) {
            fillLoadLabel(false);
        }
    }

    private void fillLoadLabel(boolean isSuccess){
        smallPanel.setVisible(true);
        loadLabel.setText(isSuccess ? SUCCESS : UNSUCCESSFUL);
    }

    private void fillTableColumns() {
        int counter = 0;
        for (String title : excelWorker.getTitles()) {
            TableColumn column = new TableColumn(title);

            int columnNumber = counter;
            column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(columnNumber).toString()));
            counter++;

            table.getColumns().addAll(column);
        }
    }

    private void fillColumnCell() {
        for (int i = 0; i < excelWorker.getCountRows(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (String title : excelWorker.getTitles()) {
                row.add(String.valueOf(excelWorker.getTableData().get(title).get(i)));
            }
            data.add(row);
        }
        table.setItems(data);
    }

    private void fillTable() {
        table.getColumns().clear();
        data = FXCollections.observableArrayList();
        fillColumnCell();
        fillTableColumns();
    }

    @FXML
    private void close() {
        smallPanel.setVisible(false);
    }

    public AppFX getAppFX() {
        return appFX;
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
}
