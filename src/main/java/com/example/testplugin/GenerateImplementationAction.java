package com.example.testplugin;

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

public class GenerateImplementationAction extends AnAction {

    private final AiCoder aiCoder = new AiCoder(); // TODO memory leak

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
        VirtualFile testFile = getTestFileRelatedTo(specFile);

        // TODO check testFileExists

        Task.Backgroundable task = new Task.Backgroundable(project, "Generating implementation from " + specFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    String spec = VfsUtil.loadText(specFile);
                    String testClassContents = VfsUtil.loadText(testFile);
                    String implClassName = specFile.getName().replace(".spec", "");

                    String implClassContents = aiCoder.generateImplementationClassContents(spec, testClassContents, implClassName)
                            .replace("```java", "")
                            .replace("```", "");

                    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            PsiFile implClassFile = psiFileFactory.createFileFromText(implClassName + ".java", implClassContents);
                            PsiDirectory implClassDirectory = PsiManager.getInstance(project).findDirectory(specFile.getParent());
                            PsiFile implClassJavaFile = (PsiFile) implClassDirectory.add(implClassFile); // TODO?
                            OpenFileAction.openFile(implClassJavaFile.getVirtualFile(), project); // TODO ?
                        });
                    });

//                    runTests(testClassFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }

    private VirtualFile getTestFileRelatedTo(VirtualFile specFile) {
        VirtualFile parentDir = specFile.getParent();
        String targetFileName = specFile.getNameWithoutExtension() + "Test.java";
        return parentDir.findChild(targetFileName);
    }
}
