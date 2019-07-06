package com.historycraft.launcher;

import javax.swing.*;

public class ProgressionFrame extends JFrame {

    private JProgressBar progressBar = new JProgressBar();
    private JLabel progressName = new JLabel();
    private JButton cancelButton = new JButton();

    private boolean isCanceled = false;


    public void setProgress(int process) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(process);
        });
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setProcessName(final String processName) {
        SwingUtilities.invokeLater(() -> {
            progressName.setText(processName);
           // progressBar.setValue(progressBar.getMinimum());
        });
    }

    public void reset() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progressBar.getMinimum());
        });
    }


    public void setMaximum(final Integer maximum) {
        SwingUtilities.invokeLater(() -> progressBar.setMaximum(maximum));
    }


    public void incrementValue() {
        SwingUtilities.invokeLater(() -> {
            int val = progressBar.getValue();
            progressBar.setValue(val + 1);
        });
    }

    public void init() {
        setSize(500,180);
        setTitle("History craft downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        progressName.setBounds(10,20,460,25);
        progressName.setText("Downloading minecraft");

        progressBar.setBounds(10,45,460,25);
        progressBar.setMinimum(0);

        cancelButton.setText("Cancel");
        cancelButton.setBounds(200, 80,100,25);
        cancelButton.addActionListener(e -> {
            isCanceled = true;
            ProgressionFrame.this.dispose();
        });

        getContentPane().add(progressBar);
        getContentPane().add(progressName);
        getContentPane().add(cancelButton);

        setVisible(true);
    }
}
