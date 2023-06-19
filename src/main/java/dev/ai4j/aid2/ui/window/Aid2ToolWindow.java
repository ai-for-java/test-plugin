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
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.ai4j.chat.UserMessage.userMessage;

public class Aid2ToolWindow implements ToolWindowFactory {

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
            handle(textField.getText());
            textField.setText("");
        });

        JButton verifyButton = new JButton("Self-Verify");
        verifyButton.addActionListener(e -> handle("Verify your answer for correctness."));

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            Conversation.stopIfStreaming();
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            reset("");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(verifyButton);
        buttonPanel.add(stopButton);
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

    public static void appendText(String text) {
        if (textArea != null) {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    public static void reset(String text) {
        Conversation.reset();
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

    private void handle(String userMessage) {

        Conversation.stopIfStreaming();

        if (!textArea.getText().isEmpty()) {
            appendText("\n\n\n");
        }

        appendText("[ User ]\n" + userMessage);

        Conversation.fromUser(userMessage(userMessage), new StreamingResponseHandler() {

            private final AtomicBoolean streamingStarted = new AtomicBoolean(false);

            @Override
            public void onPartialResponse(String partialResponse) {
                if (!streamingStarted.get()) {
                    streamingStarted.set(true);
                    appendText("\n\n\n[ AID2 ]\n");
                }

                appendText(partialResponse);
            }

            @Override
            public void onError(Throwable error) {
                Errors.showNotification(error, project);
            }
        });
    }
}
