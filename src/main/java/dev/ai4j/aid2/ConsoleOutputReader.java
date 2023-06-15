package dev.ai4j.aid2;

import dev.ai4j.aid2.implfixer.AiImplementationFixer;
import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.ui.error.Errors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.ai4j.aid2.Utils.*;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class ConsoleOutputReader implements ConsoleFilterProvider {

    private final AiImplementationFixer aiImplementationFixer = new AiImplementationFixer(GPT_4);

    private final List<String> lines = new ArrayList<>();

    private final AtomicBoolean collectingLines = new AtomicBoolean(true);
    private String testClassName;
    private String testClassNameWithPackage;

    @Override
    public Filter[] getDefaultFilters(@NotNull Project project) {
        return new Filter[]{new Filter() {
            @Override
            public Result applyFilter(String line, int entireLength) {
                if (lines.isEmpty() && line != null && (line.endsWith("Test\n") || line.endsWith("Test\n\n"))) {
                    String[] splited = line.split(" ");

                    testClassNameWithPackage = splited[splited.length - 1].trim();

                    String[] spl = testClassNameWithPackage.split("\\.");
                    testClassName = spl[spl.length - 1];
                } else if (line != null && testClassName != null && line.contains(testClassName)) {
                    collectingLines.set(false);
                } else if (line != null && (line.contains("Error") || line.contains("Exception"))) {
                    collectingLines.set(true);
                }

                if (line != null && line.startsWith("Process finished with exit code")) {
                    if (!line.startsWith("Process finished with exit code 0")) {
                        fix(project);
                    }

                    lines.clear();
                }

                if (collectingLines.get()) {
                    lines.add(line);
                }

                return null;
            }
        }};
    }

    private void fix(@NotNull Project project) {
        String implClassNameWithPackage = testClassNameWithPackage.replace("Test", "");
        String[] spl = implClassNameWithPackage.split("\\.");
        String implClassName = spl[spl.length - 1];

        String consoleOutput = String.join("", lines);
        ApplicationManager.getApplication().runReadAction(() -> {

            String testClassContents = readFileContents(testClassNameWithPackage, project);
            String implClassContents = readFileContents(implClassNameWithPackage, project);

            ApplicationManager.getApplication().invokeLater(() -> {
                WriteCommandAction.runWriteCommandAction(project, () -> {

                    PsiDirectory psiDirectory = Utils.getPsiDirectoryFromClassName(implClassNameWithPackage, project);

                    PsiFile implementationClassFile = createFileAndShiftExistingFilesIfAny(implClassName, "", ".java", psiDirectory, project);
                    VirtualFile virtualFile = implementationClassFile.getVirtualFile();
                    FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                    AtomicBoolean skipNextFragmentIfJava = new AtomicBoolean(false);

                    AtomicBoolean skipUntilFirstSemicolon = new AtomicBoolean(true);

                    aiImplementationFixer.fix(testClassContents, consoleOutput, implClassContents, new StreamingResponseHandler() {
                        @Override
                        public void onPartialResponse(String partialResponse) {
                            WriteCommandAction.runWriteCommandAction(project, () -> {

                                if (partialResponse == null || partialResponse.isEmpty()) {
                                    return;
                                }

                                if (partialResponse.contains("`")) {
                                    skipNextFragmentIfJava.set(true);
                                    return;
                                }

                                if ("java".equals(partialResponse) && skipNextFragmentIfJava.get()) {
                                    skipNextFragmentIfJava.set(false);
                                    return;
                                }

                                if (partialResponse.contains(";") && skipUntilFirstSemicolon.get()) {
                                    skipUntilFirstSemicolon.set(false);
//                                    appendStringToTextFile(virtualFile, "\n\n");
                                    return;
                                }

                                if (skipUntilFirstSemicolon.get()) {
                                    return;
                                }

                                appendStringToTextFile(virtualFile, partialResponse);
                            });
                        }

                        @Override
                        public void onError(Throwable error) {
                            Errors.showNotification(error, project);
                        }
                    });
                });
            });
        });
    }

    private static String readFileContents(String testClassNameWithPackage, Project project) {
        VirtualFile virtualFile = getVirtualFileFromClass(project, testClassNameWithPackage);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        return psiFile.getText();
    }
}
