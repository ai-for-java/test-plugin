package com.example.testplugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

public class Utils {

    public static void createFileAndShiftExistingFilesIfAny(String baseFileName,
                                                            String fileExtension,
                                                            String fileContents,
                                                            PsiDirectory directory,
                                                            Project project) {
        try {
            PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

            int counter = 0;
            boolean fileExists;

            // Find the highest counter value for existing files
            do {
                fileExists = false;
                String fileName = baseFileName + (counter == 0 ? "" : "-" + counter) + fileExtension;
                PsiFile existingFile = directory.findFile(fileName);

                if (existingFile != null) {
                    fileExists = true;
                    counter++;
                }
            } while (fileExists);

            // Rename all existing files by incrementing their counter values
            for (int i = counter - 1; i >= 0; i--) {
                String oldFileName = baseFileName + (i == 0 ? "" : "-" + i) + fileExtension;
                String newFileName = baseFileName + "-" + (i + 1) + fileExtension;
                PsiFile oldFile = directory.findFile(oldFileName);

                if (oldFile != null) {
                    PsiFile newFile = fileFactory.createFileFromText(newFileName, oldFile.getText());
                    directory.add(newFile);
                    oldFile.delete();
                }
            }

            // Create the new file and add it to the directory
            String newFileName = baseFileName + fileExtension;
            PsiFile specVerificationFile = fileFactory.createFileFromText(newFileName, fileContents.replace("\r\n", "\n"));
            directory.add(specVerificationFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
