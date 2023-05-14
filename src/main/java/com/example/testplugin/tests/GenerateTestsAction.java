package com.example.testplugin.tests;

import com.example.testplugin.Utils;
import com.intellij.ide.actions.OpenFileAction;
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
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import static com.example.testplugin.testcases.GenerateTestCasesAction.TESTCASES;
import static com.example.testplugin.testcases.GenerateTestCasesAction.TXT;

public class GenerateTestsAction extends AnAction {

    private final AiTestGenerator aiTestGenerator = new AiTestGenerator(); // TODO memory leak

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
                    String spec = VfsUtil.loadText(specFile);

                    String implClassName = specFile.getName().replace(".spec", "");
                    String testClassName = implClassName + "Test";

                    String testCases = VfsUtil.loadText(getTestCasesFileRelatedTo(specFile));
                    String testClassContent = aiTestGenerator.generateTestClassContents(spec, testCases, testClassName)
                            .replace("```java", "")
                            .replace("```", "");

                    ApplicationManager.getApplication().invokeLater(() -> {
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(specFile.getParent());
                            Utils.createFileAndShiftExistingFilesIfAny(testClassName, ".java", testClassContent, directory, project);
                        });
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }

    private VirtualFile getTestCasesFileRelatedTo(VirtualFile specFile) {
        VirtualFile parentDir = specFile.getParent();
        String targetFileName = specFile.getNameWithoutExtension() + TESTCASES + TXT;
        return parentDir.findChild(targetFileName);
    }
}
