package ru.adrezoff.cli.application.ui;

/**
 * @author Andrey Selin
 */
public class ProgressBar {

    private static final int DEFAULT_WIDTH = 40;
    private final int width;
    private long current;
    private long total;
    private String message;
    private boolean active;

    public ProgressBar() {
        this(DEFAULT_WIDTH);
    }

    public ProgressBar(int width) {
        this.width = width;
        this.current = 0;
        this.total = 100;
        this.message = "";
        this.active = false;
    }

    /**
     * Start progress bar
     */
    public void start(long total) {
        this.total = total;
        this.current = 0;
        this.active = true;
        render();
    }

    /**
     * Start progress bar with message
     */
    public void start(long total, String message) {
        this.total = total;
        this.current = 0;
        this.message = message;
        this.active = true;
        render();
    }

    /**
     * Update progress
     */
    public void update(long current) {
        if (!active) return;
        this.current = Math.min(current, total);
        render();
    }

    /**
     * Update progress with message
     */
    public void update(long current, String message) {
        if (!active) return;
        this.current = Math.min(current, total);
        this.message = message;
        render();
    }

    /**
     * Increment progress by 1
     */
    public void increment() {
        if (!active) return;
        this.current = Math.min(current + 1, total);
        render();
    }

    /**
     * Increment progress by delta
     */
    public void increment(long delta) {
        if (!active) return;
        this.current = Math.min(current + delta, total);
        render();
    }

    /**
     * Render progress bar
     */
    private void render() {
        int percent = (int) (current * 100 / total);
        int filledWidth = (int) (current * width / total);

        StringBuilder bar = new StringBuilder("\r[");
        for (int i = 0; i < width; i++) {
            if (i < filledWidth) {
                bar.append("=");
            } else if (i == filledWidth && current < total) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("] ");
        bar.append(String.format("%3d%%", percent));

        if (!message.isEmpty()) {
            bar.append(" ").append(message);
        }

        System.out.print(bar.toString());

        if (current >= total) {
            System.out.println();
            active = false;
        }
    }

    /**
     * Complete progress bar
     */
    public void complete() {
        if (!active) return;
        update(total);
        active = false;
    }

    /**
     * Complete with message
     */
    public void complete(String message) {
        if (!active) return;
        this.message = message;
        update(total);
        active = false;
    }

    /**
     * Stop progress bar
     */
    public void stop() {
        if (active) {
            System.out.println();
            active = false;
        }
    }

    /**
     * Reset progress bar
     */
    public void reset() {
        this.current = 0;
        this.message = "";
        this.active = false;
    }

    /**
     * Check if progress bar is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Get current progress
     */
    public long getCurrent() {
        return current;
    }

    /**
     * Get total
     */
    public long getTotal() {
        return total;
    }
}