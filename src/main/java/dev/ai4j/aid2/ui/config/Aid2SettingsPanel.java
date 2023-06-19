package dev.ai4j.aid2.ui.config;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_4;

public class Aid2SettingsPanel {

    private static final String[] MODELS = {GPT_4, GPT_3_5_TURBO, "gpt-3.5-turbo-16k"};
    private static final Double[] TEMPERATURES = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    private final JPanel myPanel;
    private final JComboBox<String> modelDropdown;
    private final JComboBox<Double> temperatureDropdown;
    private final JTextField openAiApiKeyField;
    private final JTextArea coverWithCommentsField;
    private final JTextArea explainCodeField;
    private final JTextArea findBugsField;

    public Aid2SettingsPanel() {
        myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));

        Font font = new Font("JetBrains Mono", Font.PLAIN, 12);

        JPanel apiKeyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel modelLabel = new JLabel("Model: ");
        modelDropdown = new ComboBox<>(MODELS);
        apiKeyPanel.add(modelLabel);
        apiKeyPanel.add(modelDropdown);

        JLabel temperatureLabel = new JLabel("Temperature: ");
        temperatureDropdown = new ComboBox<>(TEMPERATURES);
        apiKeyPanel.add(temperatureLabel);
        apiKeyPanel.add(temperatureDropdown);

        JLabel apiKeyLabel = new JLabel("OpenAI API Key: ");
        openAiApiKeyField = new JPasswordField(40);
        openAiApiKeyField.setFont(font);
        apiKeyPanel.add(apiKeyLabel);
        apiKeyPanel.add(openAiApiKeyField);

        myPanel.add(apiKeyPanel);


        int margin = 5;


        JPanel explainCodePanel = new JPanel(new BorderLayout());
        explainCodePanel.setBorder(new TitledBorder("Explain Code Prompt Template"));
        explainCodeField = new JTextArea(6, 40);
        explainCodeField.setLineWrap(true);
        explainCodeField.setWrapStyleWord(true);
        explainCodeField.setFont(font);
        JScrollPane explainCodeScrollPane = new JBScrollPane(explainCodeField);
        explainCodeScrollPane.setBorder(JBUI.Borders.empty(margin, margin, margin, margin));
        explainCodePanel.add(explainCodeScrollPane, BorderLayout.CENTER);
        myPanel.add(explainCodePanel);


        JPanel coverWithCommentsPanel = new JPanel(new BorderLayout());
        coverWithCommentsPanel.setBorder(new TitledBorder("Cover with Comments Prompt Template"));
        coverWithCommentsField = new JTextArea(6, 40);
        coverWithCommentsField.setLineWrap(true);
        coverWithCommentsField.setWrapStyleWord(true);
        coverWithCommentsField.setFont(font);
        JScrollPane coverWithCommentsScrollPane = new JBScrollPane(coverWithCommentsField);
        coverWithCommentsScrollPane.setBorder(JBUI.Borders.empty(margin, margin, margin, margin));
        coverWithCommentsPanel.add(coverWithCommentsScrollPane, BorderLayout.CENTER);
        myPanel.add(coverWithCommentsPanel);


        JPanel findBugsPanel = new JPanel(new BorderLayout());
        findBugsPanel.setBorder(new TitledBorder("Find Bugs Prompt Template"));
        findBugsField = new JTextArea(6, 40);
        findBugsField.setLineWrap(true);
        findBugsField.setWrapStyleWord(true);
        findBugsField.setFont(font);
        JScrollPane findBugsScrollPane = new JBScrollPane(findBugsField);
        findBugsScrollPane.setBorder(JBUI.Borders.empty(margin, margin, margin, margin));
        findBugsPanel.add(findBugsScrollPane, BorderLayout.CENTER);
        myPanel.add(findBugsPanel);
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

    public String getExplainCodePromptTemplate() {
        return explainCodeField.getText();
    }

    public void setExplainCodePromptTemplate(String template) {
        explainCodeField.setText(template);
    }

    public String getFindBugsPromptTemplate() {
        return findBugsField.getText();
    }

    public void setFindBugsPromptTemplate(String template) {
        findBugsField.setText(template);
    }

    public String getModel() {
        return (String) modelDropdown.getSelectedItem();
    }

    public void setModel(String model) {
        modelDropdown.setSelectedItem(model);
    }

    public Double getTemperature() {
        return (Double) temperatureDropdown.getSelectedItem();
    }

    public void setTemperature(Double temperature) {
        temperatureDropdown.setSelectedItem(temperature);
    }
}
