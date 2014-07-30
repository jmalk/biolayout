package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class LayoutProgressBarDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutProgressBar class.
    */
    public static final long serialVersionUID = 111222333444555693L;

    private JProgressBar progressBar = null;
    private JLabel label = null;
    private JLabel statusLabel = null;
    private JButton cancelButton = null;
    private LayoutFrame layoutFrame = null;
    private Timer timer = null;
    private int progressValue;
    private int maxValue;
    private long lastUpdateTime;

    private volatile boolean reset = false;

    public LayoutProgressBarDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, false);

        this.layoutFrame = layoutFrame;

        progressBar  = new JProgressBar(0, 1000);
        label = new JLabel();
        statusLabel  = new JLabel();
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(this);

        label.setText(" " + VERSION);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString(" Ready");

        timer = new Timer( 50, new TimerListener() );

        initProgressDialog();
    }

    private void initProgressDialog()
    {
        progressBar.setPreferredSize( new Dimension(700, 50) );

        this.getContentPane().setLayout( new BorderLayout(10, 0) );
        this.getContentPane().add(label, BorderLayout.NORTH);
        this.getContentPane().add(progressBar, BorderLayout.CENTER);
        this.getContentPane().add(cancelButton, BorderLayout.EAST);
        this.getContentPane().add(statusLabel, BorderLayout.SOUTH);
        this.getContentPane().setSize( new Dimension(700, 500) );
        this.setUndecorated(true);

        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }

    public void prepareProgressBar(int max, String title, boolean isCancellable)
    {
        reset = false;
        statusLabel.setText(" " + title);

        maxValue = max;
        if (maxValue <= 0)
        {
            maxValue = 1;
        }

        progressBar.setMaximum(maxValue);
        cancelled = false;
        cancelButton.setEnabled(true);
        cancelButton.setVisible(isCancellable);
    }

    public void prepareProgressBar(int max, String title)
    {
        prepareProgressBar(max, title, false);
    }

    public void startProgressBar()
    {
        timer.start();
        layoutFrame.block();
        progressValue = 0;
        lastUpdateTime = System.currentTimeMillis();
        progressBar.setValue(0);
        progressBar.setString("0%");
        this.pack();
    }

    public void endProgressBar()
    {
        reset = false;
        statusLabel.setText(" Ready");
        progressBar.setString(" Done");
        progressBar.setValue( progressBar.getMaximum() );
        layoutFrame.unblock();
    }

    public void stopProgressBar()
    {
        reset = true;
        this.setVisible(false);
    }

    public synchronized void incrementProgress(int value)
    {
        final int UPDATE_PERIOD_MS = 50;
        long currentTime = System.currentTimeMillis();
        progressValue = value;

        if (currentTime < lastUpdateTime + UPDATE_PERIOD_MS)
        {
            return;
        }

        progressBar.setValue(progressValue);
        int percentage = (progressValue * 100) / progressBar.getMaximum();
        progressBar.setString(percentage + "%");
        lastUpdateTime = currentTime;
    }

    public synchronized void incrementProgress()
    {
        incrementProgress(progressValue + 1);
    }

    public synchronized void setText(String text)
    {
        statusLabel.setText(" " + text);
    }

    public synchronized void appendText(String text)
    {
        statusLabel.setText(statusLabel.getText() + text);
    }

    public synchronized void setIndeterminate(Boolean value)
    {
        progressBar.setIndeterminate(value);
    }

    public synchronized String getText()
    {
        return statusLabel.getText();
    }

    boolean cancelled = false;
    public synchronized boolean userHasCancelled()
    {
        return cancelled;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(cancelButton))
        {
            cancelled = true;
            cancelButton.setEnabled(false);
        }
    }

    /**
     * The actionPerformed method in this class is called each time the Timer "goes off".
     */
    private class TimerListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
            if (!reset)
            {
                setVisible(true);
                timer.stop();
            }
        }
    }


}