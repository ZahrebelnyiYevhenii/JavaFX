package trudvbolshom.desktop.utils;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;

import static trudvbolshom.constants.ConstantsClass.PROGRAM_FILE;

class WindowsCommands {
    private static final String CMD_COMMAND = "cmd /c";
    private static final String START_EXE = "START";
    private static final String DELETE_FILE = "DEL";
    private static final String COPY_FILE = "COPY";

    static void removeOldVersionAndStartNew(File newProgramVersion) throws IOException {
        if (newProgramVersion.exists()) {

            final String copyNewFileToOld = COPY_FILE + " " + newProgramVersion.getCanonicalPath() + " " + new File(PROGRAM_FILE).getCanonicalPath();
            final String startNewFile = START_EXE + " " + new File(PROGRAM_FILE).getCanonicalPath();
            final String removeOldFile = DELETE_FILE + " " + newProgramVersion.getCanonicalPath();

            useCommands(copyNewFileToOld, startNewFile, removeOldFile);
        }
    }

    private static void useCommands(String... commandArray) throws IOException {
        StringBuilder allCommand = new StringBuilder();

        allCommand.append(CMD_COMMAND + " ");
        for (String command : commandArray) {
            allCommand.append(command);
            if (!(commandArray[commandArray.length - 1].equals(command) || commandArray.length == 1)) {
                allCommand.append(" && ");
            }
        }

        Runtime.getRuntime().exec(allCommand.toString());
        Platform.exit();
    }
}
