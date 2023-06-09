package dev.ai4j.aid2.impl;

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
import dev.ai4j.aid2.ui.error.Errors;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.ai4j.aid2.Utils.appendStringToTextFile;
import static dev.ai4j.aid2.Utils.createFileAndShiftExistingFilesIfAny;

public class GenerateImplementationAction extends AnAction {

    private final AiImplementationGenerator aiImplementationGenerator = new AiImplementationGenerator(); // TODO memory leak

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

        // TODO check testFileExists

        Task.Backgroundable task = new Task.Backgroundable(project, "Generating implementation from " + specFile.getName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    ApplicationManager.getApplication().runReadAction(() -> {
                        // needs read action
                        PsiFile specPsiFile = PsiManager.getInstance(project).findFile(specFile);
                        String spec = specPsiFile.getText();

                        // needs read action
                        PsiFile testClassPsiFile = PsiManager.getInstance(project).findFile(specFile);
                        String testClassContents = testClassPsiFile.getText();

                        String implClassName = specFile.getName().replace(".spec", "");

                        // needs read action
                        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(specFile.getParent());

                        ApplicationManager.getApplication().invokeLater(() -> {
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                PsiFile implementationClassFile = createFileAndShiftExistingFilesIfAny(implClassName, "", ".java", directory, project);
//                                String implementationClassPackage = ((PsiJavaFile)implementationClassFile).getPackageName();
                                VirtualFile virtualFile = implementationClassFile.getVirtualFile();
                                FileEditorManager.getInstance(project).openFile(virtualFile, false); // TODO try true?

                                AtomicBoolean skipNextFragmentIfJava = new AtomicBoolean(false);

                                aiImplementationGenerator.generateImplementationClassContents(spec, testClassContents, implClassName, new StreamingResponseHandler() {
                                    @Override
                                    public void onPartialResponse(String partialResponse) {
                                        WriteCommandAction.runWriteCommandAction(project, () -> {

                                            if (partialResponse == null || partialResponse.isEmpty()) {
                                                return;
                                            }

                                            if (partialResponse.contains("`")) {
                                                skipNextFragmentIfJava.set(true);
                                                return;
                                            }

                                            if ("java".equals(partialResponse) && skipNextFragmentIfJava.get()) {
                                                skipNextFragmentIfJava.set(false);
                                                return;
                                            }

                                            // needs write action
                                            appendStringToTextFile(virtualFile, partialResponse);
                                        });
                                    }

                                    @Override
                                    public void onComplete() {

//                                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//                                        int result = compiler.run(null, null, null, "path/to/your/java/file");

                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        Errors.showNotification(error, project);
                                    }
                                });
                            });
                        });
                    });

                    //  TODO runTests(testClassFile);
                } catch (Exception ex) {
                    Errors.showNotification(ex, project);
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }
}
