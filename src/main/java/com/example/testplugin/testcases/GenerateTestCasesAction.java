package com.example.testplugin.testcases;

import com.example.testplugin.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import dev.ai4j.model.openai.OpenAiModelName;
import org.jetbrains.annotations.NotNull;

public abstract class GenerateTestCasesAction extends AnAction {

    public static final String TESTCASES = ".testcases";
    public static final String TXT = ".txt";

    private final AiTestCaseGenerator aiTestCaseGenerator = new AiTestCaseGenerator(getModelName()); // TODO memory leak

    protected abstract OpenAiModelName getModelName();

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

        Task.Backgroundable task = new Task.Backgroundable(project, "Generating test cases from " + specFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    String spec = VfsUtil.loadText(specFile);

                    String implClassName = specFile.getName().replace(".spec", "");

                    String testCases = aiTestCaseGenerator.generateTestCases(spec, implClassName);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(specFile.getParent());
                            Utils.createFileAndShiftExistingFilesIfAny(implClassName + TESTCASES, "-", TXT, testCases, directory, project);
                        });
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }
}
