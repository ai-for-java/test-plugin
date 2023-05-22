package com.example.testplugin.coverwithtests;

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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import dev.ai4j.model.openai.OpenAiModelName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.testplugin.Utils.createFileAndShiftExistingFilesIfAny;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public abstract class GenerateTestsFromTestCasesAction extends AnAction {

    private final AiTestGenerator aiTestGenerator = new AiTestGenerator(getModelName()); // TODO memory leak

    protected abstract OpenAiModelName getModelName();

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(virtualFile != null && "testcases".equals(virtualFile.getExtension()));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String taskTitle = "Generating tests from " + PSI_FILE.getData(e.getDataContext()).getName();
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
        String testCases = psiFile.getText();


        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            String testClassName = psiFile.getName().replace(".java", "") + "Test";
            String classHeader = String.format("""
                    import org.junit.jupiter.api.Test;
                    import static org.assertj.core.api.Assertions.assertThat;
                    import static org.assertj.core.api.Assertions.assertThatThrownBy;
                                        
                    public class %s {
                        
                    }
                    """, testClassName);
            PsiFile newTestClassFile = createFileAndShiftExistingFilesIfAny(testClassName, "", ".java", classHeader, psiFile.getContainingDirectory(), e.getProject());
            FileEditorManager.getInstance(e.getProject()).openFile(newTestClassFile.getVirtualFile(), false); // TODO try true?

            Document newTestClassDocument = FileDocumentManager.getInstance().getDocument(newTestClassFile.getVirtualFile());


        });
    }

    //    private void generateTests(AnActionEvent e, String classContents, Document newTestClassDocument, ClassMember classMember, String testCases) {
//        aiTestGenerator.generateTestsFor(classContents, testCases, classMember, new ModelResponseHandler() {
//            @Override
//            public void handleResponseFragment(String responseFragment) {
//                WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
//                    if (responseFragment != null) {
//                        int documentLength = newTestClassDocument.getTextLength();
//                        newTestClassDocument.insertString(documentLength - 3, responseFragment);
//                        FileDocumentManager.getInstance().saveDocument(newTestClassDocument); // TODO ?
//                    }
//                });
//            }
//
//            @Override
//            public void handleCompleteResponse(String completeResponse) {
//                 TODO reformat code
//                JavaCodeStyleManager.getInstance(e.getProject()).optimizeImports(psiFile);
//
//                 TODO or on each line/method?
//                WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
//                    int documentLength = newTestClassDocument.getTextLength();
//                    newTestClassDocument.insertString(documentLength, "\n\n"); // TODO?
//
//                    currentMemberId.incrementAndGet(); // TODO
//                    if (currentMemberId.get() == lastMemberId.get()) {
//                        FileDocumentManager.getInstance().saveDocument(newTestClassDocument);
//                    }
//                });
//            }
//        });
//    }
}