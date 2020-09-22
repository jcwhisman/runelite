package net.runelite.client.plugins.infernosplittimer;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.account.SessionManager;
import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class InfernoSplitTimerPanel extends PluginPanel {

    private static final String RUNELITE_LOGIN = "https://runelite_login/";

    private final JLabel loggedLabel = new JLabel();
    private final JRichTextPane emailLabel = new JRichTextPane();
    private JPanel actionsContainer;

    @Inject
    private Client client;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ScheduledExecutorService executor;

    void init()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel versionPanel = new JPanel();
        versionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        versionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        versionPanel.setLayout(new GridLayout(0, 1));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        JLabel version = new JLabel(htmlLabel("RuneLite version: ", RuneLiteProperties.getVersion()));
        version.setFont(smallFont);

        JLabel revision = new JLabel();
        revision.setFont(smallFont);

        String engineVer = "Unknown";
        if (client != null)
        {
            engineVer = String.format("Rev %d", client.getRevision());
        }

        revision.setText(htmlLabel("Oldschool revision: ", engineVer));

        JLabel launcher = new JLabel(htmlLabel("Launcher version: ", MoreObjects
                .firstNonNull(RuneLiteProperties.getLauncherVersion(), "Unknown")));
        launcher.setFont(smallFont);

        loggedLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        loggedLabel.setFont(smallFont);

        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(smallFont);
        emailLabel.enableAutoLinkHandler(false);
        emailLabel.addHyperlinkListener(e ->
        {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()) && e.getURL() != null)
            {
                if (e.getURL().toString().equals(RUNELITE_LOGIN))
                {
                    executor.execute(sessionManager::login);
                }
            }
        });

        versionPanel.add(version);
        versionPanel.add(revision);
        versionPanel.add(launcher);
        versionPanel.add(Box.createGlue());
        versionPanel.add(loggedLabel);
        versionPanel.add(emailLabel);

        actionsContainer = new JPanel();
        actionsContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        actionsContainer.setLayout(new GridLayout(0, 1, 0, 10));
    }

    private static String htmlLabel(String key, String value)
    {
        return "<html><body style = 'color:#a5a5a5'>" + key + "<span style = 'color:white'>" + value + "</span></body></html>";
    }
}
