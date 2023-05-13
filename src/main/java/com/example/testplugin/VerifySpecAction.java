package com.example.testplugin;

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

public class VerifySpecAction extends AnAction {

    private final AiSpecVerifier aiSpecVerifier = new AiSpecVerifier(); // TODO memory leak

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

        Task.Backgroundable task = new Task.Backgroundable(project, "Verifying " + specFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    String spec = VfsUtil.loadText(specFile);

                    String specVerificationResult = aiSpecVerifier.verifySpecification(spec);

                    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            try {
                                PsiFile specVerificationFile = psiFileFactory.createFileFromText(specFile.getName() + ".verification.txt", specVerificationResult.replace("\r\n", "\n"));
                                PsiDirectory directory = PsiManager.getInstance(project).findDirectory(specFile.getParent());
                                directory.add(specVerificationFile);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
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
