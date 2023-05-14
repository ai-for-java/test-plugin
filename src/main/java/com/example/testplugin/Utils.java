package com.example.testplugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

public class Utils {

    public static void createFileAndShiftExistingFilesIfAny(String baseFileName,
                                                            String divider,
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
                String fileName = baseFileName + (counter == 0 ? "" : divider + counter) + fileExtension;
                PsiFile existingFile = directory.findFile(fileName);

                if (existingFile != null) {
                    fileExists = true;
                    counter++;
                }
            } while (fileExists);

            // Rename all existing files by incrementing their counter values
            for (int i = counter - 1; i >= 0; i--) {
                String oldFileName = baseFileName + (i == 0 ? "" : divider + i) + fileExtension;
                String newFileName = baseFileName + divider + (i + 1) + fileExtension;
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

    public static PsiFile createFileAndShiftExistingFilesIfAny(String baseFileName,
                                                               String divider,
                                                               String fileExtension,
                                                               PsiDirectory directory,
                                                               Project project) {
        try {
            PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

            int counter = 0;
            boolean fileExists;

            // Find the highest counter value for existing files
            do {
                fileExists = false;
                String fileName = baseFileName + (counter == 0 ? "" : divider + counter) + fileExtension;
                PsiFile existingFile = directory.findFile(fileName);

                if (existingFile != null) {
                    fileExists = true;
                    counter++;
                }
            } while (fileExists);

            // Rename all existing files by incrementing their counter values
            for (int i = counter - 1; i >= 0; i--) {
                String oldFileName = baseFileName + (i == 0 ? "" : divider + i) + fileExtension;
                String newFileName = baseFileName + divider + (i + 1) + fileExtension;
                PsiFile oldFile = directory.findFile(oldFileName);

                if (oldFile != null) {
                    PsiFile newFile = fileFactory.createFileFromText(newFileName, oldFile.getText());
                    directory.add(newFile);
                    oldFile.delete();
                }
            }

            // Create the new file and add it to the directory
            String newFileName = baseFileName + fileExtension;
            PsiFile specVerificationFile = fileFactory.createFileFromText(newFileName, "");
            return (PsiFile) directory.add(specVerificationFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void appendStringToTextFile(VirtualFile virtualFile, String contentToAppend) {
        // TODO optimize?
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (document != null && contentToAppend != null) {
            int documentLength = document.getTextLength();
            document.insertString(documentLength, contentToAppend);
            FileDocumentManager.getInstance().saveDocument(document);
        }
    }
}
