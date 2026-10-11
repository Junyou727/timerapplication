import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple stopwatch-style timer with Start, Stop, Continue, and Reset.
 */
public class TimerApplication extends JFrame {

    private final JLabel timeLabel;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton continueButton;
    private final JButton resetButton;

    private javax.swing.Timer swingTimer;
    private long elapsedMillis;
    private long lastTickMillis;
    private boolean running;

    public TimerApplication() {
        super("Timer");

        timeLabel = new JLabel(formatTime(0), SwingConstants.CENTER);
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        continueButton = new JButton("Continue");
        resetButton = new JButton("Reset");

        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        continueButton.addActionListener(e -> resume());
        resetButton.addActionListener(e -> reset());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(continueButton);
        buttonPanel.add(resetButton);

        setLayout(new BorderLayout());
        add(timeLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        swingTimer = new javax.swing.Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long now = System.currentTimeMillis();
                elapsedMillis += now - lastTickMillis;
                lastTickMillis = now;
                timeLabel.setText(formatTime(elapsedMillis));
            }
        });

        updateButtons();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void start() {
        if (running) {
            return;
        }
        elapsedMillis = 0;
        lastTickMillis = System.currentTimeMillis();
        running = true;
        swingTimer.start();
        timeLabel.setText(formatTime(elapsedMillis));
        updateButtons();
    }

    private void stop() {
        if (!running) {
            return;
        }
        swingTimer.stop();
        running = false;
        updateButtons();
    }

    private void resume() {
        if (running || elapsedMillis == 0) {
            return;
        }
        lastTickMillis = System.currentTimeMillis();
        running = true;
        swingTimer.start();
        updateButtons();
    }

    private void reset() {
        swingTimer.stop();
        running = false;
        elapsedMillis = 0;
        timeLabel.setText(formatTime(0));
        updateButtons();
    }

    private void updateButtons() {
        boolean hasTime = elapsedMillis > 0;
        startButton.setEnabled(!running && !hasTime);
        stopButton.setEnabled(running);
        continueButton.setEnabled(!running && hasTime);
        resetButton.setEnabled(hasTime || running);
    }

    private static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long centiseconds = (millis % 1000) / 10;
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep default look and feel if system L&F is unavailable.
            }
            new TimerApplication().setVisible(true);
        });
    }
}
