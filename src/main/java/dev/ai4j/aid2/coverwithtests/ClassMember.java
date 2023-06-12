package dev.ai4j.aid2.coverwithtests;

public record ClassMember(ClassMemberType type, String contents) {

    public enum ClassMemberType {
        CONSTRUCTOR,
        METHOD
    }
}
