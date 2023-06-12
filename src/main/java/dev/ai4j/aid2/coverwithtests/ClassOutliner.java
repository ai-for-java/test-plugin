package dev.ai4j.aid2.coverwithtests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.ast.Modifier.Keyword.PRIVATE;

public class ClassOutliner {

    public List<ClassMember> getNonPrivateClassMembers(String classContents) {

        List<ClassMember> members = new ArrayList<>();

        CompilationUnit cu = StaticJavaParser.parse(classContents);

        cu.findAll(ConstructorDeclaration.class).stream()
                .filter(ctor -> !ctor.hasModifier(PRIVATE))
                .forEach(ctor -> members.add(new ClassMember(ClassMember.ClassMemberType.CONSTRUCTOR, ctor.toString())));

        cu.findAll(MethodDeclaration.class).stream()
                .filter(method -> !method.hasModifier(PRIVATE))
                .forEach(method -> members.add(new ClassMember(ClassMember.ClassMemberType.METHOD, method.toString())));

        return members;
    }
}
