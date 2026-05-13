package com.example.balloonwala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.balloonwala.objects.Swap;
import com.example.balloonwala.utils.GenericUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes2.dex */
public class PuzzleActivity extends AppCompatActivity {
    private List<Button> buttonList = new ArrayList();
    private int columns = 16;
    private int stepsCount = 0;
    private List<Swap> undoButtonList = new ArrayList();

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        this.columns = intent.getIntExtra(MainActivity.COLUMNS, 16);
        getAllButtons();
        GenericUtils.distributeData(this.columns + 1, 1, this.buttonList);
        TextView textView = (TextView) findViewById(R.id.outputTextView);
        textView.setText("");
        if (this.columns == 9) {
            setTitle(R.string.eight_puzzle);
        } else {
            setTitle(R.string.fifteen_puzzle);
        }
    }

    private void createAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alertDialogBuilder.setTitle(getString(R.string.play_again) + StringUtils.SPACE + getString(R.string.confirmation)).setMessage(R.string.quit).setIcon(R.mipmap.balloon_wala_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.example.balloonwala.PuzzleActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                GenericUtils.distributeData(PuzzleActivity.this.columns + 1, 1, PuzzleActivity.this.buttonList);
                TextView textView = (TextView) PuzzleActivity.this.findViewById(R.id.outputTextView);
                textView.setText("");
            }
        }).setNegativeButton(android.R.string.no, (DialogInterface.OnClickListener) null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.example.balloonwala.PuzzleActivity.2
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(-2).setTextColor(PuzzleActivity.this.getResources().getColor(android.R.color.holo_red_light));
                alertDialog.getButton(-1).setTextColor(PuzzleActivity.this.getResources().getColor(android.R.color.holo_green_light));
            }
        });
        alertDialog.show();
    }

    public void playAgain(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alertDialogBuilder.setTitle(getString(R.string.play_again_confirmation)).setMessage(R.string.quit).setIcon(R.mipmap.balloon_wala_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.example.balloonwala.PuzzleActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                GenericUtils.distributeData(PuzzleActivity.this.columns + 1, 1, PuzzleActivity.this.buttonList);
                TextView textView = (TextView) PuzzleActivity.this.findViewById(R.id.outputTextView);
                textView.setText("");
            }
        }).setNegativeButton(android.R.string.no, (DialogInterface.OnClickListener) null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.example.balloonwala.PuzzleActivity.4
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(-2).setTextColor(PuzzleActivity.this.getResources().getColor(android.R.color.holo_red_light));
                alertDialog.getButton(-1).setTextColor(PuzzleActivity.this.getResources().getColor(android.R.color.holo_green_light));
            }
        });
        alertDialog.show();
    }

    public void backToMainActivity(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alertDialogBuilder.setTitle(getString(R.string.play_again_confirmation)).setMessage(R.string.quit).setIcon(R.mipmap.balloon_wala_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.example.balloonwala.PuzzleActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(PuzzleActivity.this, (Class<?>) MainActivity.class);
                PuzzleActivity.this.startActivity(intent);
            }
        }).setNegativeButton(android.R.string.no, (DialogInterface.OnClickListener) null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.example.balloonwala.PuzzleActivity.6
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(-2).setTextColor(PuzzleActivity.this.getResources().getColor(android.R.color.holo_red_light));
                alertDialog.getButton(-1).setTextColor(PuzzleActivity.this.getResources().getColor(android.R.color.holo_green_light));
            }
        });
        alertDialog.show();
    }

    private void getAllButtons() {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() { // from class: com.example.balloonwala.PuzzleActivity.7
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println("Great Motion " + MotionEvent.actionToString(motionEvent.getAction()));
                if (motionEvent.getAction() == 1) {
                    System.out.println("Button Great " + view.getId());
                    PuzzleActivity.this.swap(view);
                    return true;
                }
                System.out.println("Not Button Great " + view.getId());
                return false;
            }
        };
        Button button1_1 = (Button) findViewById(R.id.buttonCoordinates1_1);
        button1_1.setOnTouchListener(onTouchListener);
        this.buttonList.add(button1_1);
        Button button1_2 = (Button) findViewById(R.id.buttonCoordinates1_2);
        this.buttonList.add(button1_2);
        Button button1_3 = (Button) findViewById(R.id.buttonCoordinates1_3);
        this.buttonList.add(button1_3);
        Button button1_4 = (Button) findViewById(R.id.buttonCoordinates1_4);
        if (this.columns == 16) {
            this.buttonList.add(button1_4);
        } else {
            button1_4.setVisibility(4);
        }
        Button button2_1 = (Button) findViewById(R.id.buttonCoordinates2_1);
        this.buttonList.add(button2_1);
        Button button2_2 = (Button) findViewById(R.id.buttonCoordinates2_2);
        this.buttonList.add(button2_2);
        Button button2_3 = (Button) findViewById(R.id.buttonCoordinates2_3);
        this.buttonList.add(button2_3);
        Button button2_4 = (Button) findViewById(R.id.buttonCoordinates2_4);
        if (this.columns == 16) {
            this.buttonList.add(button2_4);
        } else {
            button2_4.setVisibility(4);
        }
        Button button3_1 = (Button) findViewById(R.id.buttonCoordinates3_1);
        this.buttonList.add(button3_1);
        Button button3_2 = (Button) findViewById(R.id.buttonCoordinates3_2);
        this.buttonList.add(button3_2);
        Button button3_3 = (Button) findViewById(R.id.buttonCoordinates3_3);
        this.buttonList.add(button3_3);
        Button button3_4 = (Button) findViewById(R.id.buttonCoordinates3_4);
        if (this.columns == 16) {
            this.buttonList.add(button3_4);
        } else {
            button3_4.setVisibility(4);
        }
        Button button4_1 = (Button) findViewById(R.id.buttonCoordinates4_1);
        if (this.columns == 16) {
            this.buttonList.add(button4_1);
        } else {
            button4_1.setVisibility(4);
        }
        Button button4_2 = (Button) findViewById(R.id.buttonCoordinates4_2);
        if (this.columns == 16) {
            this.buttonList.add(button4_2);
        } else {
            button4_2.setVisibility(4);
        }
        Button button4_3 = (Button) findViewById(R.id.buttonCoordinates4_3);
        if (this.columns == 16) {
            this.buttonList.add(button4_3);
        } else {
            button4_3.setVisibility(4);
        }
        Button button4_4 = (Button) findViewById(R.id.buttonCoordinates4_4);
        if (this.columns == 16) {
            this.buttonList.add(button4_4);
        } else {
            button4_4.setVisibility(4);
        }
    }

    public void swap(View view) {
        System.out.println("Button Click Great " + view.getId());
        Button buttonPressed = null;
        switch (view.getId()) {
            case R.id.buttonCoordinates1_1 /* 2131230793 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates1_1);
                break;
            case R.id.buttonCoordinates1_2 /* 2131230794 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates1_2);
                break;
            case R.id.buttonCoordinates1_3 /* 2131230795 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates1_3);
                break;
            case R.id.buttonCoordinates1_4 /* 2131230796 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates1_4);
                break;
            case R.id.buttonCoordinates2_1 /* 2131230797 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates2_1);
                break;
            case R.id.buttonCoordinates2_2 /* 2131230798 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates2_2);
                break;
            case R.id.buttonCoordinates2_3 /* 2131230799 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates2_3);
                break;
            case R.id.buttonCoordinates2_4 /* 2131230800 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates2_4);
                break;
            case R.id.buttonCoordinates3_1 /* 2131230801 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates3_1);
                break;
            case R.id.buttonCoordinates3_2 /* 2131230802 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates3_2);
                break;
            case R.id.buttonCoordinates3_3 /* 2131230803 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates3_3);
                break;
            case R.id.buttonCoordinates3_4 /* 2131230804 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates3_4);
                break;
            case R.id.buttonCoordinates4_1 /* 2131230805 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates4_1);
                break;
            case R.id.buttonCoordinates4_2 /* 2131230806 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates4_2);
                break;
            case R.id.buttonCoordinates4_3 /* 2131230807 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates4_3);
                break;
            case R.id.buttonCoordinates4_4 /* 2131230808 */:
                buttonPressed = (Button) findViewById(R.id.buttonCoordinates4_4);
                break;
        }
        if (buttonPressed != null && StringUtils.isNotBlank(buttonPressed.getText())) {
            ConstraintLayout.LayoutParams buttonLayoutParams = (ConstraintLayout.LayoutParams) buttonPressed.getLayoutParams();
            View buttonAbove = findViewById(buttonLayoutParams.topToBottom);
            View buttonBelow = findViewById(buttonLayoutParams.bottomToTop);
            View buttonRight = findViewById(buttonLayoutParams.startToEnd);
            View buttonLeft = findViewById(buttonLayoutParams.endToStart);
            if (GenericUtils.checkButton(buttonAbove)) {
                GenericUtils.swapData(buttonPressed, (Button) buttonAbove);
                this.undoButtonList.add(0, new Swap(buttonPressed, (Button) buttonAbove));
                Button undoButton = (Button) findViewById(R.id.undo);
                undoButton.setEnabled(true);
                this.stepsCount++;
            } else if (GenericUtils.checkButton(buttonBelow)) {
                GenericUtils.swapData(buttonPressed, (Button) buttonBelow);
                this.undoButtonList.add(0, new Swap(buttonPressed, (Button) buttonBelow));
                Button undoButton2 = (Button) findViewById(R.id.undo);
                undoButton2.setEnabled(true);
                this.stepsCount++;
            } else if (GenericUtils.checkButton(buttonLeft)) {
                GenericUtils.swapData(buttonPressed, (Button) buttonLeft);
                this.undoButtonList.add(0, new Swap(buttonPressed, (Button) buttonLeft));
                Button undoButton3 = (Button) findViewById(R.id.undo);
                undoButton3.setEnabled(true);
                this.stepsCount++;
            } else if (GenericUtils.checkButton(buttonRight)) {
                GenericUtils.swapData(buttonPressed, (Button) buttonRight);
                this.undoButtonList.add(0, new Swap(buttonPressed, (Button) buttonRight));
                Button undoButton4 = (Button) findViewById(R.id.undo);
                undoButton4.setEnabled(true);
                this.stepsCount++;
            }
            if (GenericUtils.solved(this.columns, this.buttonList)) {
                GenericUtils.disableButtons(this.buttonList);
                TextView textView = (TextView) findViewById(R.id.outputTextView);
                textView.setText(getString(R.string.puzzle_solved) + StringUtils.SPACE + getString(R.string.in) + StringUtils.SPACE + this.stepsCount + StringUtils.SPACE + getString(R.string.steps));
            }
        }
    }

    public void undo(View view) {
        Swap swap = this.undoButtonList.get(0);
        GenericUtils.swapData(swap.getToButton(), swap.getFromButton());
        this.undoButtonList.remove(0);
        if (CollectionUtils.isEmpty(this.undoButtonList)) {
            Button undoButton = (Button) findViewById(R.id.undo);
            undoButton.setEnabled(false);
        }
    }
}
