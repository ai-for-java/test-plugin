package com.example.testplugin;

import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;

public class DoMagicAction extends AnAction {

    private final AiCoder aiCoder = new AiCoder(); // TODO memory leak

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

        ApplicationManager.getApplication().runWriteAction(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> { // Wrap the PSI modification code in WriteCommandAction
                try {
                    String specFileContents = VfsUtil.loadText(specFile);
                    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);

                    String testClassName = specFile.getName().replace(".spec", "") + "Test";
                    String testClassContent = aiCoder.generateTestClassContents(specFileContents, testClassName).replace("```", "");
                    PsiFile testClassFile = psiFileFactory.createFileFromText(testClassName + ".java", testClassContent);
                    PsiDirectory testClassDirectory = PsiManager.getInstance(project).findDirectory(specFile.getParent());
                    PsiFile testClassJavaFile = (PsiFile) testClassDirectory.add(testClassFile); // TODO?
                    OpenFileAction.openFile(testClassJavaFile.getVirtualFile(), project);

                    // impl
                    String implementationClassContents = aiCoder.generateImplementationClassContents(specFileContents, testClassContent).replace("```", "");
                    System.out.println(implementationClassContents);
                    String implClassName = specFile.getName().replace(".spec", "");
                    PsiFile implClassFile = psiFileFactory.createFileFromText(implClassName + ".java", implementationClassContents);
                    PsiDirectory implClassDirectory = PsiManager.getInstance(project).findDirectory(specFile.getParent());
                    PsiFile implClassJavaFile = (PsiFile) implClassDirectory.add(implClassFile); // TODO?
                    OpenFileAction.openFile(implClassJavaFile.getVirtualFile(), project); // TODO ?

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

    public static PsiDirectory convertMainToTest(PsiDirectory mainDirectory) {
        Project project = mainDirectory.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiDirectoryFactory psiDirectoryFactory = PsiDirectoryFactory.getInstance(project);

        String mainPath = mainDirectory.getVirtualFile().getPath();
        String testPath = mainPath.replaceFirst("/src/main/", "/src/test/");
        String packageName = psiDirectoryFactory.getQualifiedName(mainDirectory, false);

        VirtualFile testVirtualFile = project.getBaseDir().findFileByRelativePath(testPath);

        if (testVirtualFile == null) {
            System.out.println("Test directory not found for package: " + packageName);
            return null;
        }

        PsiDirectory testDirectory = psiManager.findDirectory(testVirtualFile);

        return testDirectory;
    }
}
