package com.shojabon.man10raid.Utils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class STimer {

    public static boolean pluginEnabled = true;

    public int remainingTime = 0;
    boolean timerMoving = false;

    ArrayList<Runnable> onEndEvents = new ArrayList<>();
    ArrayList<Consumer<Integer>> onIntervalEvents = new ArrayList<>();

    public STimer(){
        pluginEnabled = true;
    }

    Thread timerThread = new Thread(() -> {
        while(timerMoving && pluginEnabled){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            remainingTime -= 1;
            if(remainingTime <= 0) {
                //timer end
                for(Runnable event: onEndEvents){
                    event.run();
                }
                timerMoving = false;
                return;
            }

            //end of interval events
            for(Consumer<Integer> event: onIntervalEvents){
                event.accept(remainingTime);
            }

        }
    });

    public void setRemainingTime(int time){
        this.remainingTime = time;
    }

    public void addRemainingTime(int time){
        remainingTime += time;
    }

    public void removeRemainingTIme(int time){
        remainingTime -= time;
    }

    public void stop(){timerMoving = false;}

    public void start(){
        timerMoving = true;
        if(!timerThread.isAlive()) timerThread.start();
    }

    public void addOnEndEvent(Runnable callback){
        onEndEvents.add(callback);
    }

    public void addOnIntervalEvent(Consumer<Integer> callback){
        onIntervalEvents.add(callback);
    }



}
