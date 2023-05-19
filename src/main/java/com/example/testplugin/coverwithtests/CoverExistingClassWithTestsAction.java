package com.example.testplugin.coverwithtests;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.openai.OpenAiModelName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.testplugin.Utils.createFileAndShiftExistingFilesIfAny;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public abstract class CoverExistingClassWithTestsAction extends AnAction {

    private final AiClassOutliner aiClassOutliner = new AiClassOutliner(); // TODO memory leak
    private final AiTestCaseGenerator aiTestCaseGenerator = new AiTestCaseGenerator(getModelName()); // TODO memory leak
    private final AiTestGenerator aiTestGenerator = new AiTestGenerator(GPT_3_5_TURBO); // TODO memory leak

    private final AtomicInteger currentMemberId = new AtomicInteger(0);

    protected abstract OpenAiModelName getModelName();

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
        String taskTitle = "Generating tests for " + PSI_FILE.getData(e.getDataContext()).getName();
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

    private final ExecutorService withTwoThreads = Executors.newFixedThreadPool(2);
    private final ExecutorService unlimitedThreads = Executors.newCachedThreadPool();

    public void handle(AnActionEvent e) {
        PsiFile psiFile = PSI_FILE.getData(e.getDataContext());
        String classContents = psiFile.getText();

        List<ClassMember> classMembers = aiClassOutliner.getNonPrivateClassMembers(classContents);

        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            String testClassName = psiFile.getName().replace(".java", "") + "Test";
            String classHeader = String.format("""
                    import org.junit.jupiter.api.Test;
                    import static org.assertj.core.api.Assertions.assertThat;
                    import static org.assertj.core.api.Assertions.assertThatThrownBy;
                                        
                    public class %s {
                                        
                    """, testClassName);
            PsiFile newTestClassFile = createFileAndShiftExistingFilesIfAny(testClassName, "", ".java", classHeader, psiFile.getContainingDirectory(), e.getProject());
            FileEditorManager.getInstance(e.getProject()).openFile(newTestClassFile.getVirtualFile(), false); // TODO try true?

            Document newTestClassDocument = FileDocumentManager.getInstance().getDocument(newTestClassFile.getVirtualFile());

            AtomicInteger id = new AtomicInteger(0);
            classMembers.forEach(classMember -> {
                int memberId = id.getAndIncrement();

                withTwoThreads.execute(() -> {
                    String testCases = aiTestCaseGenerator.generateTestCasesFor(classContents, classMember);

                    unlimitedThreads.execute(() -> {
                        while (currentMemberId.get() != memberId) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                // ignore
                            }
                        }
                        generateTests(e, classContents, newTestClassDocument, classMember, testCases);
                    });
                });
            });
        });
    }

    private void generateTests(AnActionEvent e, String classContents, Document newTestClassDocument, ClassMember classMember, String testCases) {
        aiTestGenerator.generateTestsFor(classContents, testCases, classMember, new ModelResponseHandler() {
            @Override
            public void handleResponseFragment(String responseFragment) {
                WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
                    if (responseFragment != null) {
                        int documentLength = newTestClassDocument.getTextLength();
                        newTestClassDocument.insertString(documentLength, responseFragment);
                        //FileDocumentManager.getInstance().saveDocument(newTestClassDocument); // TODO ?
                    }
                });
            }

            @Override
            public void handleCompleteResponse(String completeResponse) {
                // TODO reformat code
//                JavaCodeStyleManager.getInstance(e.getProject()).optimizeImports(psiFile);

                // TODO or on each line/method?
                WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
                    int documentLength = newTestClassDocument.getTextLength();
                    newTestClassDocument.insertString(documentLength, "\n\n");
                    FileDocumentManager.getInstance().saveDocument(newTestClassDocument);

                    currentMemberId.incrementAndGet(); // TODO
                });
            }
        });
    }
}