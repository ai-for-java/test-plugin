package com.example.testplugin.coverwithtests;

import lombok.Value;

public record ClassMember(com.example.testplugin.coverwithtests.ClassMember.ClassMemberType type, String contents) {

    public enum ClassMemberType {
        CONSTRUCTOR,
        METHOD
    }
}
