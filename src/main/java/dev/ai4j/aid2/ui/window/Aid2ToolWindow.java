package dev.ai4j.aid2.ui.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicLong;

public class Aid2ToolWindow implements ToolWindowFactory {

    private static final AtomicLong latestAppender = new AtomicLong();

    private static JTextArea textArea;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // Create the tool window content.
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        JScrollPane scrollPane = new JBScrollPane(textArea);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(scrollPane, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    // Method to append text to the JTextArea.
    public static void appendText(long appenderId, String text) {
        if (latestAppender.get() == appenderId && textArea != null) {
            textArea.append(text);
        }
    }

    public static void init(long appenderId) {
        latestAppender.set(appenderId);
        if (textArea != null) {
            textArea.setText("");
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
}
