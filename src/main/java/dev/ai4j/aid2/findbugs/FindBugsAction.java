package dev.ai4j.aid2.findbugs;

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

public abstract class FindBugsAction extends AnAction {

    private final AiBugFinder bugFinder = new AiBugFinder(getModelName());

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

        try {
            ApplicationManager.getApplication().runReadAction(() -> {

                // needs read action
                PsiFile javaClassPsiFile = PsiManager.getInstance(project).findFile(javaClassFile);
                String javaCode = javaClassPsiFile.getText();

                long appenderId = System.currentTimeMillis();
                Aid2ToolWindow.init(appenderId, "AID2:\n");
                Aid2ToolWindow.open(project);

                bugFinder.findBugs(javaCode, new StreamingResponseHandler() {

                    @Override
                    public void onPartialResponse(String partialResponse) {
                        Aid2ToolWindow.appendText(appenderId, partialResponse);
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
