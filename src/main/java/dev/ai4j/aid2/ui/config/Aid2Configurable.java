package dev.ai4j.aid2.ui.config;

import com.intellij.openapi.options.Configurable;
import dev.ai4j.aid2.Config;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Aid2Configurable implements Configurable {

    private Aid2SettingsPanel settingsPanel;

    @Override
    public String getDisplayName() {
        return "AID2 Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsPanel = new Aid2SettingsPanel();
        return settingsPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        Config.model(settingsPanel.getModel());
        Config.temperature(settingsPanel.getTemperature());
        Config.openAiApiKey(settingsPanel.getOpenAiApiKey());
        Config.explainCodePromptTemplate(settingsPanel.getExplainCodePromptTemplate());
        Config.coverWithCommentsPromptTemplate(settingsPanel.getCoverWithCommentsPromptTemplate());
        Config.findBugsPromptTemplate(settingsPanel.getFindBugsPromptTemplate());
        Config.suggestImprovementsPromptTemplate(settingsPanel.getSuggestImprovementsPromptTemplate());
    }

    @Override
    public void reset() {
        settingsPanel.setModel(Config.model());
        settingsPanel.setTemperature(Config.temperature());
        settingsPanel.setOpenAiApiKey(Config.openAiApiKey());
        settingsPanel.setExplainCodePromptTemplate(Config.explainCodePromptTemplate());
        settingsPanel.setCoverWithCommentsPromptTemplate(Config.coverWithCommentsPromptTemplate());
        settingsPanel.setFindBugsPromptTemplate(Config.findBugsPromptTemplate());
        settingsPanel.setSuggestImprovementsPromptTemplate(Config.suggestImprovementsPromptTemplate());
    }
}
