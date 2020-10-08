package net.runelite.client.plugins.infernosplittimertest;

import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class WaveSplit
{
    public int wave;
    public String time;
    public String diff;

    public WaveSplit(int wave, String time, String diff)
    {
        this.wave = wave;
        this.time = time;
        this.diff = diff;
    }
}

@Slf4j
public class InfernoRun {

    public static final List<Integer> SPLIT_WAVES = new ArrayList<Integer>() {{
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

    public void addSplit(int wave, String time)
    {
        //Only add if its a wave split
        int index = SPLIT_WAVES.indexOf(wave);

        if(index > -1)
        {
            String prevTime = index == 0 ? time : splits.get(index - 1).time;
            String diff = calcTimeDifference(time, prevTime);

            splits.set(index, new WaveSplit(wave, time, diff));
        }
    }

    private String calcTimeDifference(String currentTime, String previousTime)
    {
        Duration dPrevTime = Duration.between(LocalTime.MIN, LocalTime.parse("00:" + previousTime));
        Duration dCurrentTime = Duration.between(LocalTime.MIN, LocalTime.parse("00:" + currentTime));
        Duration dTimeDiff = dCurrentTime.minus(dPrevTime);

        long seconds = dTimeDiff.getSeconds();
        String timeDiff = String.format("%02d:%02d", seconds / 60, seconds % 60);

        return timeDiff;
    }

    private void reset()
    {
        for(int wave : SPLIT_WAVES)
        {
            WaveSplit split = new WaveSplit(wave, "", "");
            splits.add(split);
        }
    }

}

