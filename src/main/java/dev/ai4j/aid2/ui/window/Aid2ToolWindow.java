package dev.ai4j.aid2.ui.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.aid2.Conversation;
import dev.ai4j.aid2.ui.error.Errors;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicLong;

import static dev.ai4j.chat.UserMessage.userMessage;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class Aid2ToolWindow implements ToolWindowFactory {

    private static final AtomicLong latestAppender = new AtomicLong();

    private Project project;

    private static JTextArea textArea;
    private static JTextField textField;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project;

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JBScrollPane(textArea);

        textField = new JTextField();
        textField.addActionListener(e -> {
            handleInput(textField.getText());
            textField.setText("");
        });

        JButton verifyButton = new JButton("Are you sure?");
        verifyButton.addActionListener(e -> handleInput("Are you sure?"));

        JButton resetButton = new JButton("Reset Conversation");
        resetButton.addActionListener(e -> {
            Conversation.reset(GPT_4); // TODO make configurable
            init(System.currentTimeMillis(), "");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(verifyButton);
        buttonPanel.add(resetButton);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public static void appendText(long appenderId, String text) {
        if (latestAppender.get() == appenderId && textArea != null) {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    public static void init(long appenderId, String text) {
        latestAppender.set(appenderId);
        if (textArea != null) {
            textArea.setText(text);
        }
    }

    public static void open(Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow myToolWindow = toolWindowManager.getToolWindow("AID2");
        if (myToolWindow != null) {
            myToolWindow.show(() -> {
            });
            myToolWindow.activate(null);
        }
    }

    private void handleInput(String input) {

        if (!textArea.getText().isEmpty()) {
            appendText(latestAppender.get(), "\n\n\n");
        }

        appendText(latestAppender.get(), "User:\n" + input + "\n\n\nAID2:\n");

        Conversation.fromUser(userMessage(input), new StreamingResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                appendText(latestAppender.get(), partialResponse);
            }

            @Override
            public void onError(Throwable error) {
                Errors.showNotification(error, project);
            }
        });
    }
}
