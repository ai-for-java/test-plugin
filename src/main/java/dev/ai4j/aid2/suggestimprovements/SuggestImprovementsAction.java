package dev.ai4j.aid2.suggestimprovements;

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

public class SuggestImprovementsAction extends AnAction {

    private final AiImprovementsSuggester improvementsSuggester = new AiImprovementsSuggester();

    @Override
    public void update(AnActionEvent e) {
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

                // needs read action
                PsiFile javaClassPsiFile = PsiManager.getInstance(project).findFile(javaClassFile);
                String javaCode = javaClassPsiFile.getText();

                Aid2ToolWindow.reset("[ AID2 ]\n");
                Aid2ToolWindow.open(project);

                improvementsSuggester.suggestImprovements(javaCode, new StreamingResponseHandler() {

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
