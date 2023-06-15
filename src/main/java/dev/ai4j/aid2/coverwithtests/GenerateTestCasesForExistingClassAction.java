package dev.ai4j.aid2.coverwithtests;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.ui.error.Errors;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.ai4j.aid2.Utils.appendTo;
import static dev.ai4j.aid2.Utils.createFileAndShiftExistingFilesIfAny;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public abstract class GenerateTestCasesForExistingClassAction extends AnAction {

    private final ClassOutliner classOutliner = new ClassOutliner(); // TODO memory leak
    private final AiTestCaseGenerator aiTestCaseGenerator = new AiTestCaseGenerator(getModelName()); // TODO memory leak

    protected abstract String getModelName();

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(virtualFile != null
                && "java".equals(virtualFile.getExtension())
                && !virtualFile.getName().endsWith("Test.java")
        );
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String taskTitle = "Generating test cases for " + PSI_FILE.getData(e.getDataContext()).getName();
        Task.Backgroundable task = new Task.Backgroundable(e.getProject(), taskTitle) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                handle(e);
                // TODO keep indicator running
            }
        };

        ProgressManager.getInstance().run(task);
    }

    public void handle(AnActionEvent e) {
        PsiFile psiFile = PSI_FILE.getData(e.getDataContext());
        String classContents = psiFile.getText();

        List<ClassMember> classMembers = classOutliner.getNonPrivateClassMembers(classContents);

        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            String testCasesFileName = psiFile.getName().replace(".java", "");
            PsiFile newTestCasesFile = createFileAndShiftExistingFilesIfAny(testCasesFileName, "", ".testcases.txt", "", psiFile.getContainingDirectory(), e.getProject());
            FileEditorManager.getInstance(e.getProject()).openFile(newTestCasesFile.getVirtualFile(), false); // TODO try true?
            Document newTestCasesDocument = FileDocumentManager.getInstance().getDocument(newTestCasesFile.getVirtualFile());

            Executors.newSingleThreadExecutor().execute(() -> {

                AtomicBoolean shouldWait = new AtomicBoolean(false);
                classMembers.forEach(classMember -> {

                    int i = 0;
                    while (shouldWait.get()) {
                        try {
                            Thread.sleep(100);
                            if (i++ % 50 == 0) {
                                System.out.println("waiting...");
                            }
                        } catch (InterruptedException ex) {
                            // ignore
                        }
                    }

                    shouldWait.set(true);
                    aiTestCaseGenerator.generateTestCasesFor(classContents, classMember, new StreamingResponseHandler() {
                        @Override
                        public void onPartialResponse(String partialResponse) {
                            WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
                                appendTo(newTestCasesDocument, partialResponse);
                            });
                        }

                        @Override
                        public void onComplete() {
                            WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
                                appendTo(newTestCasesDocument, "\n\n");
                                FileDocumentManager.getInstance().saveDocument(newTestCasesDocument);
                                shouldWait.set(false);
                            });
                        }

                        @Override
                        public void onError(Throwable error) {
                            Errors.showNotification(error, e.getProject());
                        }
                    });
                });
            });
        });
    }
}