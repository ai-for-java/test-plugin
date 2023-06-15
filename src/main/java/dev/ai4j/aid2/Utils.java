package dev.ai4j.aid2;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import dev.ai4j.aid2.ui.error.Errors;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static PsiFile createFileAndShiftExistingFilesIfAny(String baseFileName,
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
            PsiFile newPsiFile = fileFactory.createFileFromText(newFileName, fileContents.replace("\r\n", "\n"));
            return (PsiFile) directory.add(newPsiFile);

        } catch (Exception ex) {
            Errors.showNotification(ex, project);
        }
        return null;
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

                    String fileContents;
                    if (".java".equals(fileExtension)) {
                        fileContents = appendOrChangeNumberToClassName(oldFile.getText(), i + 1);
                    } else {
                        fileContents = oldFile.getText();
                    }

                    PsiFile newFile = fileFactory.createFileFromText(newFileName, fileContents);
                    directory.add(newFile);
                    oldFile.delete();
                }
            }

            // Create the new file and add it to the directory
            String newFileName = baseFileName + fileExtension;
            PsiFile newPsiFile = fileFactory.createFileFromText(newFileName, ".java".equals(fileExtension) ? "\n\n" : "");
            return (PsiFile) directory.add(newPsiFile);

        } catch (Exception ex) {
            Errors.showNotification(ex, project);
        }
        return null;
    }

    private static String appendOrChangeNumberToClassName(String javaCode, int number) {
        // Regex pattern to match class definition
        Pattern pattern = Pattern.compile("public class ([A-Za-z]+)(\\d*)");
        Matcher matcher = pattern.matcher(javaCode);

        // Replace class names with digits (if any) at the end with the new number
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String className = matcher.group(1);
            matcher.appendReplacement(sb, "public class " + className + number);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static void appendStringToTextFile(VirtualFile virtualFile, String contentToAppend) {
        // TODO optimize?
        // TODO write to PsiFile? consider caching
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (document != null && contentToAppend != null) {
            int documentLength = document.getTextLength();
            document.insertString(documentLength, contentToAppend);
            FileDocumentManager.getInstance().saveDocument(document);
        }
    }

    public static VirtualFile getVirtualFileFromClass(Project project, String fullClassName) {
        String[] parts = fullClassName.split("\\.");
        String className = parts[parts.length - 1] + ".java";
        Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, className, GlobalSearchScope.allScope(project));
        return virtualFiles.iterator().next();
    }

    public static PsiDirectory getPsiDirectoryFromClassName(String classNameWithPackageName, Project project) {
        // Get the package name from the class name
        int lastDotIndex = classNameWithPackageName.lastIndexOf('.');
        String packageName = lastDotIndex != -1 ? classNameWithPackageName.substring(0, lastDotIndex) : "";

        // Get the package directory
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentSourceRoots();
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiDirectory psiDirectory = null;

        for (VirtualFile root : contentRoots) {
            String packagePath = packageName.replace('.', '/');
            VirtualFile packageDir = root.findFileByRelativePath(packagePath);

            if (packageDir != null && packageDir.isDirectory()) {
                psiDirectory = psiManager.findDirectory(packageDir);
                break;
            }
        }

        return psiDirectory;
    }

    public static void appendTo(Document document, String text) {
        if (text == null)
            return;

        int documentLength = document.getTextLength();
        document.insertString(documentLength, text);
    }
}
