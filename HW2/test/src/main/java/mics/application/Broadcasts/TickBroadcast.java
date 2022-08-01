package mics.application.Broadcasts;

import mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int duration;
    public TickBroadcast(int dur){
        duration=dur;
    }
    @Override
    public String getSenderName() {
        return null;
    }
    public int getDuration(){return duration;}
}
