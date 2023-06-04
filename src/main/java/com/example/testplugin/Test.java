package com.example.testplugin;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.github.javaparser.ast.Modifier.Keyword.PRIVATE;

public class Test {

    public static void main(String[] args) {
        String javaCode = "package com.example.testplugin;\n" +
                "\n" +
                "public class Calculator {\n" +
                "\n" +
                "    public Calculator() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    public Calculator(int a) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    private Calculator(String s) {\n" +
                "    }\n" +
                "\n" +
                "    protected Calculator(Double d) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    Calculator(Float f) {\n" +
                "        \n" +
                "    }\n" +
                "\n" +
                "    public void publicMethod() {\n" +
                "    }\n" +
                "\n" +
                "    void packagePrivateMethod() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    protected void protectedMethod() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    private void privateMethod() {\n" +
                "\n" +
                "    }\n" +
                "}\n";  // Your Java code here


        CompilationUnit cu = StaticJavaParser.parse(new ByteArrayInputStream(javaCode.getBytes()));

        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class).stream()
                .filter(method -> !method.hasModifier(PRIVATE))
                .toList();

        List<ConstructorDeclaration> ctors = cu.findAll(ConstructorDeclaration.class).stream()
                .filter(ctor -> !ctor.hasModifier(PRIVATE))
                .toList();

    }
}
