package com.example.balloonwala.model;

import android.widget.Button;

/* JADX INFO: loaded from: classes2.dex */
public class Move {
    private final Button fromButton;
    private final Button toButton;

    public Move(Button fromButton, Button toButton) {
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
