package net.runelite.client.plugins.infernosplittimer;

import lombok.Getter;
import org.w3c.dom.html.HTMLImageElement;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class WaveSplit
{
    public String wave;
    public String time;
    public String diff;

    public WaveSplit(String wave, String time, String diff)
    {
        this.wave = wave;
        this.time = time;
        this.diff = time;
    }
}

public class InfernoRun {

    private static final List<Integer> SPLIT_WAVES = new ArrayList<Integer>() {{
        add(2);
        add(3);
        add(4);
        add(18);
        add(25);
        add(35);
        add(42);
        add(50);
        add(57);
        add(60);
        add(63);
        add(66);
        add(67);
        add(68);
        add(69);
    }};

    @Getter
    private ArrayList<WaveSplit> splits = new ArrayList<WaveSplit>();

    public InfernoRun()
    {
        reset();
    }

    public void updateRun(String wave, String time)
    {
        for(WaveSplit split : splits)
        {
            if( split.wave == wave)
            {
                String diff = calcTimeDifference(time);
                split.time = time;
                split.diff = diff;
            }
        }
    }

    private String calcTimeDifference(String currentTime)
    {
        int splitsLength = this.splits.size();

        if (splitsLength > 0)
        {
            WaveSplit split = this.splits.get(splitsLength - 1);

            Duration dPrevTime = Duration.between(LocalTime.MIN, LocalTime.parse("00:" + split.time));
            Duration dCurrentTime = Duration.between(LocalTime.MIN, LocalTime.parse("00:" + currentTime));
            Duration dTimeDiff = dCurrentTime.minus(dPrevTime);

            long seconds = dTimeDiff.getSeconds();
            String timeDiff = String.format("%02d:%02d", seconds / 60, seconds % 60);

            return timeDiff;
        }
        return currentTime;
    }

    private void reset()
    {
        for(int wave : SPLIT_WAVES)
        {
            WaveSplit split = new WaveSplit(Integer.toString(wave), "", "");
            splits.add(split);
        }
    }

}

