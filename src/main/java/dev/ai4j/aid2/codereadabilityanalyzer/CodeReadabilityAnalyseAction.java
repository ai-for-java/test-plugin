package dev.ai4j.aid2.codereadabilityanalyzer;

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
import dev.ai4j.aid2.Utils;
import dev.ai4j.aid2.ui.error.Errors;
import org.jetbrains.annotations.NotNull;

import static dev.ai4j.aid2.Utils.createFileAndShiftExistingFilesIfAny;

public abstract class CodeReadabilityAnalyseAction extends AnAction {

    private final AiCodeReadabilityAnalyzer readabilityAnalyzer = new AiCodeReadabilityAnalyzer(getModelName());

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
        VirtualFile smellyCodeFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);

        Task.Backgroundable task = new Task.Backgroundable(project, "Analyze readability of existing code " + smellyCodeFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    ApplicationManager.getApplication().runReadAction(() -> {
                        // needs read action
                        PsiFile specPsiFile = PsiManager.getInstance(project).findFile(smellyCodeFile);
                        String smellyCode = specPsiFile.getText();

                        // needs read action
                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(smellyCodeFile.getParent());

                        ApplicationManager.getApplication().invokeLater(() -> {
                            ApplicationManager.getApplication().runWriteAction(() -> {
                                // needs write action
                                String requirementsToRefactorFileName = smellyCodeFile.getName() + ".txt";
                                PsiFile file = Utils.createFileAndShiftExistingFilesIfAny(requirementsToRefactorFileName, "", ".txt", directory, project);
                                VirtualFile virtualFile = file.getVirtualFile();
                                FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                                readabilityAnalyzer.analyzeReadabilityOfCode(smellyCode, new StreamingResponseHandler() {
                                    @Override
                                    public void onPartialResponse(String partialResponse) {
                                        WriteCommandAction.runWriteCommandAction(project, () -> {

                                            // needs write action
                                            Utils.appendStringToTextFile(virtualFile, partialResponse);
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

                } catch (Exception ex) {
                    Errors.showNotification(ex, project);
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }
}
