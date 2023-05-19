package com.example.testplugin.coverwithtests;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.PromptTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static com.example.testplugin.coverwithtests.ClassMember.ClassMemberType.CONSTRUCTOR;
import static com.example.testplugin.coverwithtests.ClassMember.ClassMemberType.METHOD;
import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static java.util.Arrays.stream;

public class AiClassOutliner {

    public List<ClassMember> getNonPrivateClassMembers(String classContents) {
        List<ClassMember> members = new ArrayList<>();
        String[] lines = classContents.split("\n");

        boolean processingMember = false;
        StringBuilder memberContents = new StringBuilder();

        for (String line : lines) {
            // line = line.trim();

            if (line.contains("constructor-start") || line.contains("method-start")) {
                processingMember = true;
            } else if (line.contains("constructor-end")) {
                processingMember = false;
                members.add(new ClassMember(CONSTRUCTOR, memberContents.toString()));
                memberContents = new StringBuilder();
            } else if (line.contains("method-end")) {
                processingMember = false;
                members.add(new ClassMember(METHOD, memberContents.toString()));
                memberContents = new StringBuilder();
            } else if (processingMember) {
                memberContents.append(line);
                memberContents.append("\n");
            }
        }

        return members;
    }
}
