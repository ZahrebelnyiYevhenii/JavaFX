package trudvbolshom.desktop.starter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import trudvbolshom.desktop.controller.MainController;

import java.io.IOException;

import static trudvbolshom.constants.ConstantsClass.*;

public class AppFX extends Application {
    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(PROGRAM_NAME);
        showBaseWindow();
    }

    private void showBaseWindow() {
        try {
            initBaseWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBaseWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppFX.class.getResource(SCENE_DIR + "/" + MAIN_SCENE));

        Scene main = new Scene(loader.load());
        primaryStage.setScene(main);
        Image icon = new Image(PROGRAM_ICON);
        
        MainController mainController = loader.getController();
        mainController.setAppFX(this);

        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }
}
