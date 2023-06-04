package com.example.testplugin.impl;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

public class InspectionHelper {

    public static List<ProblemDescriptor> runInspectionOnFile(Project project, PsiFile psiFile, Class<? extends LocalInspectionTool> inspectionToolClass) {
        InspectionManager inspectionManager = InspectionManager.getInstance(project);
        LocalInspectionTool inspectionTool;

        try {
            inspectionTool = inspectionToolClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create an instance of the inspection tool.", e);
        }

        ProblemsHolder problemsHolder = new ProblemsHolder(inspectionManager, psiFile, true);
        PsiElementVisitor visitor = inspectionTool.buildVisitor(problemsHolder, true);
        psiFile.accept(visitor);
        return new ArrayList<>(problemsHolder.getResults());
    }
}
