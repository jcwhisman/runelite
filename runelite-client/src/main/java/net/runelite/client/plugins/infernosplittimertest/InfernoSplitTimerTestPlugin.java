package net.runelite.client.plugins.infernosplittimertest;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.api.ItemID.INFERNAL_CAPE;

@Slf4j
@PluginDescriptor(
        name = "Test Plugin",
        description = "Displays inferno wave split times in chatbox",
        enabledByDefault = true,
        tags={"inferno","timer","split"}
)

public class InfernoSplitTimerTestPlugin extends Plugin
{
    private static final Pattern WAVE_MESSAGE = Pattern.compile("Wave: (\\d+)");
    private static final String TIMER_CLASS = "net.runelite.client.plugins.timers.ElapsedTimer";

    private InfernoRun infernoRun = new InfernoRun();

    @Inject
    private Client client;

    @Inject
    private InfoBoxManager infoboxManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Setter
    private NavigationButton navButton;
    private InfernoSplitTimerTestPanel panel;

    @Override
    protected void startUp() throws Exception
    {
        //Instantiate plugin
        panel = injector.getInstance(InfernoSplitTimerTestPanel.class);
        panel.init();

        //generate icon from the resources/plugins/infernosplittimer folder
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "nib.png");

        //create the button
        navButton = NavigationButton.builder()
                .tooltip("Inferno Wave Split")
                .icon(icon)
                .priority(8)
                .panel(panel)
                .build();


        //add button to the navbar
        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        if (checkInInferno())
        {
            //Check if message was a 'Wave' message
            String message = Text.removeTags(event.getMessage());
            Matcher matcher = WAVE_MESSAGE.matcher(message);

            if (matcher.matches()) {
                int wave = Integer.parseInt(matcher.group(1));

                if (wave == 1)
                {
                    log.info("New inferno run started! Updating panel.");
                    infernoRun = new InfernoRun();
                    SwingUtilities.invokeLater(() -> panel.updateView(infernoRun));
                }

                //Is the wave a split wave
                if (infernoRun.SPLIT_WAVES.contains(wave)) {
                    //elapsedTime is null if the inferno info box couldn't be found
                    String elapsedTime = getElapsedInfernoTime();

                    if (elapsedTime != null) {
                        String chatMessage = "Wave Split: " + elapsedTime;

                        log.info(chatMessage + " - Updating panel!" );
                        sendChatMessage(chatMessage);

                        infernoRun.addSplit(wave, elapsedTime);
                        SwingUtilities.invokeLater(() -> panel.updateView(infernoRun));
                    }
                }
            }
        }
    }

    private String getElapsedInfernoTime()
    {
        //Get all info boxes
        List<InfoBox> infoBoxList = infoboxManager.getInfoBoxes();

        //check all inferno boxes to find the elapsed timer one that matches the infernal cape icon
        for (InfoBox ib : infoBoxList)
        {
            //Check if info box is the inferno timer
            if(ib.getClass().getName() == TIMER_CLASS && ib.getImage() == itemManager.getImage(INFERNAL_CAPE))
            {
                return ib.getText();
            }
        }

        return null;
    }

    private void sendChatMessage(String chatMessage)
    {
        final String message = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(chatMessage)
                .build();

        chatMessageManager.queue(
                QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(message)
                        .build()
        );
    }

    private boolean checkInInferno()
    {
        return client.getMapRegions() != null && Arrays.stream(client.getMapRegions())
                .filter(x -> x == 9043)
                .toArray().length > 0;
    }

}
