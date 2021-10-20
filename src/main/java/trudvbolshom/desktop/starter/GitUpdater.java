package trudvbolshom.desktop.starter;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.nio.file.Paths;
import java.util.Locale;

import static trudvbolshom.constants.ConstantsClass.*;

public class GitUpdater {
    private Git git;

    public GitUpdater() {
        try {
            FileUtils.deleteDirectory(Paths.get(TEMP_FOLDER).toFile());
            git = Git.cloneRepository()
                    .setURI(GIT_REPOSITORY)
                    .setDirectory(new File(TEMP_FOLDER))
                    .call();
        } catch (GitAPIException | IOException e) {
            e.getStackTrace();
        }
    }

    public void updater(Stage primaryStage) {
        boolean hasUpdate = hasNewVersion();

        if (hasUpdate) {
            updateFile();

            primaryStage.close();
            Platform.runLater(() -> new AppFX().start(new Stage()));
        }

        git.getRepository().close();
        git.close();
    }

    private void updateFile() {
        try {
            writeFile(EXE_FILE);
            writeFile(VERSION_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e + " version file or exe file has problem");
        }
    }

    public boolean hasNewVersion() {
        try (
                ByteArrayOutputStream repoOutputStream = readFile(TEMP_FOLDER + "/" + VERSION_FILE);
                ByteArrayOutputStream localOutputStream = readFile(VERSION_FILE);) {

            return !repoOutputStream.toString().toLowerCase(Locale.ROOT).equals(localOutputStream.toString().toLowerCase(Locale.ROOT));
        } catch (IOException e) {
            throw new RuntimeException(e + " version file has problem");
        }
    }

    private void writeFile(String nameFile) throws IOException {
        File repoEXEFile = new File(TEMP_FOLDER + "/" + nameFile);
        FileInputStream repoInputStream = new FileInputStream(repoEXEFile);

        File localEXEFile = new File(nameFile);
        FileOutputStream localOutputStream = new FileOutputStream(localEXEFile);
        byte[] allFile = new byte[BUFFERED_SIZE];

        while (repoInputStream.read(allFile) != -1) {
            localOutputStream.write(allFile);
        }
        localOutputStream.close();
        repoInputStream.close();
    }

    private ByteArrayOutputStream readFile(String nameFile) throws IOException {
        File repoVersionFile = new File(nameFile);
        FileInputStream repoInputStream = new FileInputStream(repoVersionFile);
        ByteArrayOutputStream repoOutputStream = new ByteArrayOutputStream();
        byte[] allFile = new byte[BUFFERED_SIZE];

        while (repoInputStream.read(allFile) != -1) {
            repoOutputStream.write(allFile);
        }
        repoInputStream.close();
        return repoOutputStream;
    }
}
