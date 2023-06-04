package com.example.testplugin.codeexplanation;

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

import static com.example.testplugin.Utils.appendStringToTextFile;
import static com.example.testplugin.Utils.createFileAndShiftExistingFilesIfAny;

public abstract class CodeCommentingAction extends AnAction {

    private final AiCodeCommentator codeCommenting = new AiCodeCommentator(getModelName());

    protected abstract String getModelName();

    @Override
    public void update(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        e.getPresentation().setEnabledAndVisible(virtualFile != null && "java".equals(virtualFile.getExtension()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        VirtualFile javaClassFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);

        Task.Backgroundable task = new Task.Backgroundable(project, "Add explaining comments to existing code " + javaClassFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    ApplicationManager.getApplication().runReadAction(() -> {
                        // needs read action
                        PsiFile specPsiFile = PsiManager.getInstance(project).findFile(javaClassFile);
                        String javaCode = specPsiFile.getText();

                        // needs read action
                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(javaClassFile.getParent());

                        ApplicationManager.getApplication().invokeLater(() -> {
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                // needs write action
                                String commentedCodeFileName = javaClassFile.getName().replace(".java", "");
                                PsiFile file = createFileAndShiftExistingFilesIfAny(commentedCodeFileName, "", ".java", directory, project);
                                VirtualFile virtualFile = file.getVirtualFile();
                                FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                                codeCommenting.coverWithComments(javaCode, new StreamingResponseHandler() {
                                    @Override
                                    public void onPartialResponse(String partialResponse) {
                                        WriteCommandAction.runWriteCommandAction(project, () -> {

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
}
