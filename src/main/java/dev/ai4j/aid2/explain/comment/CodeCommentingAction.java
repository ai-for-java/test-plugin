package dev.ai4j.aid2.explain.comment;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.ui.error.Errors;
import dev.ai4j.aid2.ui.window.Aid2ToolWindow;
import org.jetbrains.annotations.NotNull;

public class CodeCommentingAction extends AnAction {

    private final AiCodeCommenter codeCommenter = new AiCodeCommenter();

    @Override
    public void update(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        e.getPresentation().setEnabledAndVisible(virtualFile != null && "java".equals(virtualFile.getExtension()));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        VirtualFile javaClassFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);

        try {
            ApplicationManager.getApplication().runReadAction(() -> {

                PsiFile javaClassPsiFile = PsiManager.getInstance(project).findFile(javaClassFile);
                String javaCode = javaClassPsiFile.getText();

                Aid2ToolWindow.reset("[ AID2 ]\n");
                Aid2ToolWindow.open(project);

                codeCommenter.coverWithComments(javaCode, new StreamingResponseHandler() {

                    @Override
                    public void onPartialResponse(String partialResponse) {
                        Aid2ToolWindow.appendText(partialResponse);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Errors.showNotification(error, project);
                    }
                });
            });

        } catch (Exception ex) {
            Errors.showNotification(ex, project);
        }
    }
}
