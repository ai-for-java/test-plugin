package dev.ai4j.aid2.ui.config;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Aid2SettingsPanel {

    private JPanel myPanel;
    private JTextField openAiApiKeyField;
    private JTextArea coverWithCommentsField;

    public Aid2SettingsPanel() {
        myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));

        Font font = new Font("JetBrains Mono", Font.PLAIN, 12);

        JPanel apiKeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel apiKeyLabel = new JLabel("OpenAI API Key: ");
        openAiApiKeyField = new JPasswordField(60);
        openAiApiKeyField.setFont(font);
        apiKeyPanel.add(apiKeyLabel);
        apiKeyPanel.add(openAiApiKeyField);
        myPanel.add(apiKeyPanel);


        JPanel coverWithCommentsPanel = new JPanel(new BorderLayout());
        coverWithCommentsPanel.setBorder(new TitledBorder("Cover with Comments Prompt Template"));
        coverWithCommentsField = new JTextArea(6, 40);
        coverWithCommentsField.setLineWrap(true);
        coverWithCommentsField.setWrapStyleWord(true);
        coverWithCommentsField.setFont(font);
        JScrollPane coverWithCommentsScrollPane = new JBScrollPane(coverWithCommentsField);

        int margin = 5;
        coverWithCommentsScrollPane.setBorder(JBUI.Borders.empty(margin, margin, margin, margin));

        coverWithCommentsPanel.add(coverWithCommentsScrollPane, BorderLayout.CENTER);
        myPanel.add(coverWithCommentsPanel);
    }

    public JPanel getPanel() {
        return myPanel;
    }

    public String getOpenAiApiKey() {
        return openAiApiKeyField.getText();
    }

    public void setOpenAiApiKey(String apiKey) {
        openAiApiKeyField.setText(apiKey);
    }

    public String getCoverWithCommentsPromptTemplate() {
        return coverWithCommentsField.getText();
    }

    public void setCoverWithCommentsPromptTemplate(String template) {
        coverWithCommentsField.setText(template);
    }
}
