package dev.ai4j.aid2.refactoring;

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

import static dev.ai4j.aid2.Utils.appendStringToTextFile;
import static dev.ai4j.aid2.Utils.createFileAndShiftExistingFilesIfAny;

public abstract class CodeRefactoringAction extends AnAction {

    public static final String TXT = ".txt";
    public static final String REFACTORING = "refactoringRequirements";
    private final AiRefactoring refactoring = new AiRefactoring(getModelName());

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

        Task.Backgroundable task = new Task.Backgroundable(project, "Refactor existing code " + smellyCodeFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    ApplicationManager.getApplication().runReadAction(() -> {
                        // needs read action
                        PsiFile specPsiFile = PsiManager.getInstance(project).findFile(smellyCodeFile);
                        String smellyCode = specPsiFile.getText();

                        // needs read action
                        PsiFile requirementsPsiFile = getTestCasesFileRelatedTo(smellyCodeFile, project);
                        String requirements = requirementsPsiFile.getText();

                        // needs read action
                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(smellyCodeFile.getParent());

                        ApplicationManager.getApplication().invokeLater(() -> {
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                // needs write action
                                String refactoredCodeFileName = smellyCodeFile.getName().replace(".java", "");
                                PsiFile file = createFileAndShiftExistingFilesIfAny(refactoredCodeFileName, "", ".java", directory, project);
                                VirtualFile virtualFile = file.getVirtualFile();
                                FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                                refactoring.refactor(smellyCode, requirements, new StreamingResponseHandler() {
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

    private PsiFile getTestCasesFileRelatedTo(VirtualFile specFile, Project project) {
        VirtualFile parentDir = specFile.getParent();
        String targetFileName = specFile.getNameWithoutExtension() + REFACTORING + TXT;
        VirtualFile testCasesFile = parentDir.findChild(targetFileName);
        return PsiManager.getInstance(project).findFile(testCasesFile);
    }
}
