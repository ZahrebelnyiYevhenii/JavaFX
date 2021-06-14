package trudvbolshom.desktop.starter;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.nio.file.Paths;
import java.util.Locale;

import static trudvbolshom.constants.ConstantsClass.*;

public class GitUpdater {

    public GitUpdater() {
        try {
            FileUtils.deleteDirectory(Paths.get(TEMP_FOLDER).toFile());

            Git.cloneRepository()
                    .setURI(GIT_REPOSITORY)
                    .setDirectory(new File(TEMP_FOLDER))
                    .call();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        GitUpdater gitUpdater = new GitUpdater();

        gitUpdater.updateNewVersion();
    }


    public void updateNewVersion() throws IOException {
        boolean hasUpdate = hasNewVersion();

        if (hasUpdate) {
            updateFile();
        }
    }

    private void updateFile() throws IOException {
        writeFile(EXE_FILE);
        writeFile(VERSION_FILE);
    }

    private boolean hasNewVersion() throws IOException {
        ByteArrayOutputStream repoOutputStream = readFile(TEMP_FOLDER + "/" + VERSION_FILE);
        ByteArrayOutputStream localOutputStream = readFile(VERSION_FILE);

        return !repoOutputStream.toString().toLowerCase(Locale.ROOT).equals(localOutputStream.toString().toLowerCase(Locale.ROOT));
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
        return repoOutputStream;
    }

}
