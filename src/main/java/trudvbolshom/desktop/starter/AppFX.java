package trudvbolshom.desktop.starter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import trudvbolshom.desktop.controller.MainController;
import trudvbolshom.desktop.model.ExcelWorker;

import java.io.IOException;

public class AppFX extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ExcelWorker excelWorker;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TrudVBolshomGorode");
        showBaseWindow();
    }

    private void showBaseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppFX.class.getResource("/xml/mainScene.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            Image image = new Image("/images/someImage.jpg");

            MainController mainController = loader.getController();
            mainController.setAppFX(this);
            mainController.setExcelWorker(excelWorker);

            primaryStage.getIcons().add(image);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
