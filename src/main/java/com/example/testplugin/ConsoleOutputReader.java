package com.example.testplugin;

import com.example.testplugin.implfixer.AiImplementationFixer;
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
import dev.ai4j.model.ModelResponseHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.testplugin.Utils.appendStringToTextFile;
import static com.example.testplugin.Utils.createFileAndShiftExistingFilesIfAny;
import static com.example.testplugin.Utils.getVirtualFileFromClass;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class ConsoleOutputReader implements ConsoleFilterProvider {

    private final AiImplementationFixer aiImplementationFixer = new AiImplementationFixer(GPT_4);

    private final List<String> lines = new ArrayList<>();

    @Override
    public Filter[] getDefaultFilters(@NotNull Project project) {
        return new Filter[]{new Filter() {
            @Override
            public Result applyFilter(String line, int entireLength) {
                lines.add(line);

                if (line != null && line.startsWith("Process finished with exit code")) {
                    if (!line.startsWith("Process finished with exit code 0")) {

                        String[] splited = lines.stream().filter(l -> l != null && (l.endsWith("Test\n") || l.endsWith("Test\n\n"))).findFirst().get().split(" ");
                        String testClassNameWithPackage = splited[splited.length - 1].trim();
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

                                    AtomicReference<StringBuilder> lineBuilder = new AtomicReference<>(new StringBuilder());

                                    aiImplementationFixer.fix(testClassContents, consoleOutput, implClassContents, new ModelResponseHandler() {
                                        @Override
                                        public void handleResponseFragment(String responseFragment) {
                                            WriteCommandAction.runWriteCommandAction(project, () -> {

                                                if (responseFragment == null || responseFragment.isEmpty()) {
                                                    return;
                                                }

                                                if ("```".equals(responseFragment)) {
                                                    skipNextFragmentIfJava.set(true);
                                                    return;
                                                }

                                                if ("java".equals(responseFragment) && skipNextFragmentIfJava.get()) {
                                                    skipNextFragmentIfJava.set(false);
                                                    return;
                                                }

                                                lineBuilder.get().append(responseFragment);

                                                if (responseFragment.contains("\n")) {
                                                    String oneLine = lineBuilder.get().toString();
                                                    if (!oneLine.startsWith("package")) {
                                                        appendStringToTextFile(virtualFile, oneLine);
                                                    }
                                                    lineBuilder.set(new StringBuilder());
                                                }
                                            });
                                        }
                                    });
                                });
                            });

                            System.out.println();
                        });
                    }

                    lines.clear();
                }

                return null;
            }
        }};
    }

    private static String readFileContents(String testClassNameWithPackage, Project project) {
        VirtualFile virtualFile = getVirtualFileFromClass(project, testClassNameWithPackage);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        return psiFile.getText();
    }
}
