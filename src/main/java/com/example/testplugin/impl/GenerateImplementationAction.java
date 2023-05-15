package com.example.testplugin.impl;

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
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.openai.OpenAiModelName;
import org.jetbrains.annotations.NotNull;

import static com.example.testplugin.Utils.appendStringToTextFile;
import static com.example.testplugin.Utils.createFileAndShiftExistingFilesIfAny;

public abstract class GenerateImplementationAction extends AnAction {

    private final AiImplementationGenerator aiImplementationGenerator = new AiImplementationGenerator(getModelName()); // TODO memory leak

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
        VirtualFile testFile = getTestFileRelatedTo(specFile);

        // TODO check testFileExists

        Task.Backgroundable task = new Task.Backgroundable(project, "Generating implementation from " + specFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    PsiFile specPsiFile = PsiManager.getInstance(project).findFile(specFile);
                    String spec = specPsiFile.getText();

                    PsiFile testClassPsiFile = PsiManager.getInstance(project).findFile(specFile);
                    String testClassContents = testClassPsiFile.getText();

                    String implClassName = specFile.getName().replace(".spec", "");

                    ApplicationManager.getApplication().runReadAction(() -> {
                        // needs read action
                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(specFile.getParent());

                        ApplicationManager.getApplication().invokeLater(() -> {
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                PsiFile file = createFileAndShiftExistingFilesIfAny(implClassName, "", ".java", directory, project);
                                VirtualFile virtualFile = file.getVirtualFile();
                                FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                                aiImplementationGenerator.generateImplementationClassContents(spec, testClassContents, implClassName, new ModelResponseHandler() {
                                    @Override
                                    public void handleResponseFragment(String responseFragment) {
                                        WriteCommandAction.runWriteCommandAction(project, () -> {

                                            // needs write action
                                            appendStringToTextFile(virtualFile, responseFragment);
                                        });
                                    }
                                });
                            });
                        });
                    });

                    //  TODO runTests(testClassFile);
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
