package dev.ai4j.aid2.codeexplanation;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.ex.EditorPopupHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.util.PsiTreeUtil;

public class MyHandler implements EditorPopupHandler {

    public MyHandler() {
        super();
    }

    @Override
    public boolean handlePopup(EditorMouseEvent event) {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return false;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return false;
        }

        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if (element == null) {
            return false;
        }

        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        if (modifierList == null) {
            return false;
        }

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiElement commentElement = elementFactory.createCommentFromText("// Your comment here", null);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            modifierList.addBefore(commentElement, modifierList.getFirstChild());
        });

        return true;
    }
}
