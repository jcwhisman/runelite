package net.runelite.client.plugins.infernosplittimertest;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Slf4j
public class InfernoSplitTimerTestPanel extends PluginPanel {

    private final JPanel panel = new JPanel();

    @Inject
    private Client client;

    void init()
    {
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel, BorderLayout.NORTH);
    }

    void updateView(InfernoRun infernoRun)
    {
        panel.removeAll();

        final JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        final JPanel title = new JPanel();
        title.setLayout(new GridLayout(0,3));
        title.setBorder(new EmptyBorder(3,3,3,3));
        title.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());

        JLabel waveLabel = textPanel("Split");
        JLabel timeLabel = textPanel("Time");
        JLabel diffTime = textPanel("Diff");

        title.add(waveLabel);
        title.add(timeLabel);
        title.add(diffTime);

        JPanel splitsPanel = new JPanel();
        splitsPanel.setLayout(new GridLayout(0,3));
        splitsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        splitsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        for(WaveSplit split: infernoRun.getSplits())
        {
            final JLabel splitWave = new JLabel("Wave: " + split.wave, SwingConstants.RIGHT);
            splitWave.setForeground(Color.WHITE);
            splitWave.setFont(FontManager.getRunescapeSmallFont());

            final JLabel splitTime = new JLabel(split.time, SwingConstants.CENTER);
            splitTime.setForeground(Color.WHITE);
            splitTime.setFont(FontManager.getRunescapeSmallFont());

            final JLabel splitTimeDiff = new JLabel(split.diff, SwingConstants.CENTER);
            splitTimeDiff.setForeground(Color.WHITE);
            splitTimeDiff.setFont(FontManager.getRunescapeSmallFont());

            splitsPanel.add(splitWave);
            splitsPanel.add(splitTime);
            splitsPanel.add(splitTimeDiff);
        }

        wrapper.add(title);
        wrapper.add(splitsPanel);

        panel.add(wrapper);
        panel.revalidate();
        panel.repaint();
    }


    public JLabel textPanel(String text) {
        JLabel label = new JLabel();
        label.setText(text);
        label.setForeground(Color.WHITE);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(FontManager.getRunescapeBoldFont());

        return label;
    }
}
