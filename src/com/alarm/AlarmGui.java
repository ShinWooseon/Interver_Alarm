package com.alarm;
import javax.swing.*;

import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmGui {
    private static Timer alarmTimer;
    private static Timer restartTimer;
    private static javax.swing.Timer countdownTimer;
    private static javax.swing.Timer restartCountdownTimer;
    private static boolean isRunning = false;
    private static int remainingAlarmTime;
    private static int remainingRestartTime;

    public static void main(String[] args) {
        JFrame frame = new JFrame("⏰ 인터벌 알람");
        JTextField intervalField = new JTextField(5);
        JTextField restartField = new JTextField(5);
        JLabel intervalLabel = new JLabel("알람 주기 (초):");
        JLabel restartLabel = new JLabel("재시작 간격 (초):");
        JLabel alarmCountdown = new JLabel("알람까지 남은 시간: -- 초");
        JLabel restartCountdown = new JLabel("재시작까지 남은 시간: -- 초");
        JButton startButton = new JButton("알람 시작");
        JButton stopButton = new JButton("알람 정지");

        JPanel panel = new JPanel();
        panel.add(intervalLabel);
        panel.add(intervalField);
        panel.add(restartLabel);
        panel.add(restartField);
        panel.add(startButton);
        panel.add(stopButton);
        panel.add(alarmCountdown);
        panel.add(restartCountdown);
        frame.add(panel);

        frame.setSize(200, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        startButton.addActionListener(e -> {
            try {
                int interval = Integer.parseInt(intervalField.getText());
                int restart = Integer.parseInt(restartField.getText());
                isRunning = true;
                startButton.setEnabled(false);
                startAlarm(frame, interval, restart, alarmCountdown, restartCountdown, startButton);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "숫자를 정확히 입력해주세요.");
            }
        });

        stopButton.addActionListener(e -> {
            isRunning = false;
            if (alarmTimer != null) alarmTimer.cancel();
            if (restartTimer != null) restartTimer.cancel();
            if (countdownTimer != null) countdownTimer.stop();
            if (restartCountdownTimer != null) restartCountdownTimer.stop();
            alarmCountdown.setText("알람까지 남은 시간: -- 초");
            restartCountdown.setText("재시작까지 남은 시간: -- 초");
            startButton.setEnabled(true);
        });
    }

    private static void startAlarm(JFrame frame, int interval, int restart,
                                   JLabel alarmLabel, JLabel restartLabel, JButton startButton) {
        remainingAlarmTime = interval;
        countdownTimer = new javax.swing.Timer(1000, null);
        countdownTimer.addActionListener(e -> {
            alarmLabel.setText("알람까지 남은 시간: " + remainingAlarmTime + " 초");
            remainingAlarmTime--;
        });
        countdownTimer.start();

        alarmTimer = new Timer();
        alarmTimer.schedule(new TimerTask() {
            public void run() {
                countdownTimer.stop();
                alarmLabel.setText("✅ 알람 울림!");
                SwingUtilities.invokeLater(() -> {
                    JDialog dialog = new JDialog();
                    dialog.setAlwaysOnTop(true);
                    JOptionPane.showMessageDialog(dialog, "🔔 알람 울림! (6m 전방을 응시하세요.)\n 🔔 " + interval + "초 후 알람 재시작");
                    dialog.dispose();
                    
                });

                alarmTimer.cancel();

                if (isRunning) {
                    startRestartCountdown(frame, interval, restart, alarmLabel, restartLabel, startButton);
                } else {
                    startButton.setEnabled(true);
                }
            }
        }, interval * 1000);
    }

    private static void startRestartCountdown(JFrame frame, int interval, int restart,
                                              JLabel alarmLabel, JLabel restartLabel, JButton startButton) {
        remainingRestartTime = restart;
        restartCountdownTimer = new javax.swing.Timer(1000, null);
        restartCountdownTimer.addActionListener(e -> {
            restartLabel.setText("재시작까지 남은 시간: " + remainingRestartTime + " 초");
            remainingRestartTime--;
        });
        restartCountdownTimer.start();

        restartTimer = new Timer();
        restartTimer.schedule(new TimerTask() {
            public void run() {
                restartCountdownTimer.stop();
                restartLabel.setText("재시작 중...");
                startAlarm(frame, interval, restart, alarmLabel, restartLabel, startButton);
                JDialog dialog = new JDialog();
                dialog.setAlwaysOnTop(true);
                JOptionPane.showMessageDialog(dialog, "🔔 재시작");
                dialog.dispose();
            }
        }, restart * 1000);
    }
}
