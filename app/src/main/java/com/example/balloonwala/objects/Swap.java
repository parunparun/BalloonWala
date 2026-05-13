package com.example.balloonwala.objects;

import android.widget.Button;

/* JADX INFO: loaded from: classes2.dex */
public class Swap {
    private Button fromButton;
    private Button toButton;

    public void setFromButton(Button fromButton) {
        this.fromButton = fromButton;
    }

    public void setToButton(Button toButton) {
        this.toButton = toButton;
    }

    public Swap(Button fromButton, Button toButton) {
        this.fromButton = fromButton;
        this.toButton = toButton;
    }

    public Button getFromButton() {
        return this.fromButton;
    }

    public Button getToButton() {
        return this.toButton;
    }
}
