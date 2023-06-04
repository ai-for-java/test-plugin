package com.example.testplugin.coverwithtests;

public record ClassMember(ClassMemberType type, String contents) {

    public enum ClassMemberType {
        CONSTRUCTOR,
        METHOD
    }
}
