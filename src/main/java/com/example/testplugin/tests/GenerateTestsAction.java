package com.example.testplugin.tests;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import dev.ai4j.StreamingResponseHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.testplugin.Utils.appendStringToTextFile;
import static com.example.testplugin.Utils.createFileAndShiftExistingFilesIfAny;
import static com.example.testplugin.testcases.GenerateTestCasesAction.TESTCASES;
import static com.example.testplugin.testcases.GenerateTestCasesAction.TXT;

public abstract class GenerateTestsAction extends AnAction {

    private final AiTestGenerator aiTestGenerator = new AiTestGenerator(getModelName()); // TODO memory leak

    protected abstract String getModelName();

    @Override
    public void update(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        e.getPresentation().setEnabledAndVisible(virtualFile != null && "spec".equals(virtualFile.getExtension()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        VirtualFile specFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);

        Task.Backgroundable task = new Task.Backgroundable(project, "Generating tests from " + specFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    ApplicationManager.getApplication().runReadAction(() -> {
                        // needs read action
                        PsiFile specPsiFile = PsiManager.getInstance(project).findFile(specFile);
                        String spec = specPsiFile.getText();

                        String implClassName = specFile.getName().replace(".spec", "");
                        String testClassName = implClassName + "Test";

                        PsiFile testCasesPsiFile = getTestCasesFileRelatedTo(specFile, project);
                        String testCases = testCasesPsiFile.getText();

                        // needs read action
                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(specFile.getParent());

                        ApplicationManager.getApplication().invokeLater(() -> {
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                PsiFile file = createFileAndShiftExistingFilesIfAny(testClassName, "", ".java", directory, project);
                                VirtualFile virtualFile = file.getVirtualFile();
                                FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                                AtomicBoolean skipNextFragmentIfJava = new AtomicBoolean(false);

                                aiTestGenerator.generateTestClassContents(spec, testCases, testClassName, new StreamingResponseHandler() {
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

                                            // needs write action
                                            appendStringToTextFile(virtualFile, partialResponse);
                                        });
                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        // TODO
                                        error.printStackTrace();
                                    }
                                });
                            });
                        });
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }

    private PsiFile getTestCasesFileRelatedTo(VirtualFile specFile, Project project) {
        VirtualFile parentDir = specFile.getParent();
        String targetFileName = specFile.getNameWithoutExtension() + TESTCASES + TXT;
        VirtualFile testCasesFile = parentDir.findChild(targetFileName);
        return PsiManager.getInstance(project).findFile(testCasesFile);
    }

    private static List<String> parseTestCases(String testCasesText) {
        List<String> testCases = new ArrayList<>();
        for (String testCase : testCasesText.split("Test case #")) {
            testCases.add("Test case #" + testCase.trim());
        }
        return testCases;
    }
}
