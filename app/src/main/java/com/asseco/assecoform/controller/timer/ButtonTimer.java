package com.asseco.assecoform.controller.timer;

import android.widget.Button;

import java.util.TimerTask;

/**
 * Created by matej on 6/14/16.
 */
public class ButtonTimer extends TimerTask {

    int second = 5;
    private Button button;

    public ButtonTimer(Button button) {
        this.button = button;
    }

    @Override
    public void run() {
        button.setClickable(false);
        second--;
        if (second == 0) {
            button.setClickable(true);
            cancel();
        }
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}