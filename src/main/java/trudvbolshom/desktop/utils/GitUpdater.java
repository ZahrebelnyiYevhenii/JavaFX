package trudvbolshom.desktop.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.nio.file.Paths;
import java.util.Locale;

import static trudvbolshom.constants.ConstantsClass.*;

public class GitUpdater {
    private static File LOCAL_REPOSITORY = Paths.get(TEMP_FOLDER).toFile();
    private Git git;

    public GitUpdater() {
        try {
            if (LOCAL_REPOSITORY.exists()) {
                git = Git.open(LOCAL_REPOSITORY);
                git.pull().call();
            } else {
                git = Git.cloneRepository()
                        .setURI(GIT_REPOSITORY)
                        .setDirectory(new File(TEMP_FOLDER))
                        .call();
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e + " git repository has problem");
        }
    }

    public void updater() throws IOException {
        boolean hasUpdate = hasNewVersion();

        if (hasUpdate) {
            updateFiles();

            File programNewVersion = new File(NEW_PROGRAM_FILE);
            WindowsCommands.removeOldVersionAndStartNew(programNewVersion);
        }

        git.getRepository().close();
        git.close();
    }

    private void updateFiles() {
        try {
            writeFile(NEW_PROGRAM_FILE);
            writeFile(VERSION_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e + " version file or exe file has problem");
        }
    }

    private boolean hasNewVersion() {
        try (
                ByteArrayOutputStream repoOutputStream = readFile(TEMP_FOLDER + "/" + VERSION_FILE);
                ByteArrayOutputStream localOutputStream = readFile(VERSION_FILE)) {

            return !repoOutputStream.toString().toLowerCase(Locale.ROOT).equals(localOutputStream.toString().toLowerCase(Locale.ROOT));
        } catch (IOException e) {
            throw new RuntimeException(e + " version file has problem");
        }
    }

    private ByteArrayOutputStream readFile(String versionFile) throws IOException {
        File repoVersionFile = new File(versionFile);
        FileInputStream repoInputStream = new FileInputStream(repoVersionFile);
        ByteArrayOutputStream repoOutputStream = new ByteArrayOutputStream();

        byteReadAndWrite(repoInputStream, repoOutputStream);

        repoInputStream.close();

        return repoOutputStream;
    }

    private void writeFile(String nameFile) throws IOException {
        File repoEXEFile = new File(TEMP_FOLDER + "/" + nameFile);
        FileInputStream repoInputStream = new FileInputStream(repoEXEFile);
        File localEXEFile = new File(nameFile);
        FileOutputStream localOutputStream = new FileOutputStream(localEXEFile);

        byteReadAndWrite(repoInputStream, localOutputStream);

        repoInputStream.close();
        localOutputStream.close();
    }

    private void byteReadAndWrite(FileInputStream repoInputStream, OutputStream localOutputStream) throws IOException {
        int bufferSize = BUFFERED_SIZE;
        byte[] buffer = new byte[BUFFERED_SIZE];

        while (repoInputStream.available() > 0) {
            if (repoInputStream.available() < BUFFERED_SIZE) bufferSize = repoInputStream.available();
            repoInputStream.read(buffer, 0, bufferSize);
            localOutputStream.write(buffer, 0, bufferSize);
        }
    }
}
