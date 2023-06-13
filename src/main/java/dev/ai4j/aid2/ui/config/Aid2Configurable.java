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
        Config.openAiApiKey(settingsPanel.getOpenAiApiKey());
        Config.coverWithCommentsPromptTemplate(settingsPanel.getCoverWithCommentsPromptTemplate());
    }

    @Override
    public void reset() {
        settingsPanel.setOpenAiApiKey(Config.openAiApiKey());
        settingsPanel.setCoverWithCommentsPromptTemplate(Config.coverWithCommentsPromptTemplate());
    }
}
