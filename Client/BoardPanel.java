import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {

    // Board size in "coordinate units" (match your server board size)
    private int boardWidth = 200;
    private int boardHeight = 100;

    // Things to draw
    private final List<NoteRect> notes = new ArrayList<>();
    private final List<Point> pins = new ArrayList<>();

    // Simple note model
    public static class NoteRect {
        public int x, y, w, h;
        public Color color;
        public NoteRect(int x, int y, int w, int h, Color color) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.color = color;
        }
    }

    public BoardPanel() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
    }

    public void setBoardSize(int w, int h) {
        this.boardWidth = w;
        this.boardHeight = h;
        repaint();
    }

    public void setNotes(List<NoteRect> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        repaint();
    }

    public void setPins(List<Point> newPins) {
        pins.clear();
        pins.addAll(newPins);
        repaint();
    }

    private int mapX(int x) {
        int padding = 40;
        int usableW = getWidth() - 2 * padding;
        if (usableW <= 1) usableW = 1;
        return padding + (int)Math.round((x / (double)boardWidth) * usableW);
    }

    private int mapY(int y) {
        int padding = 40;
        int usableH = getHeight() - 2 * padding;
        if (usableH <= 1) usableH = 1;
        return padding + (int)Math.round((y / (double)boardHeight) * usableH);
    }

    private int mapW(int w) {
        int padding = 40;
        int usableW = getWidth() - 2 * padding;
        if (usableW <= 1) usableW = 1;
        return (int)Math.round((w / (double)boardWidth) * usableW);
    }

    private int mapH(int h) {
        int padding = 40;
        int usableH = getHeight() - 2 * padding;
        if (usableH <= 1) usableH = 1;
        return (int)Math.round((h / (double)boardHeight) * usableH);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int padding = 40;
        g.setColor(new Color(30, 60, 160));
        g.setStroke(new BasicStroke(3));

        // x-axis
        g.drawLine(padding, padding, getWidth() - padding, padding);
        g.drawString("0", padding - 10, padding - 10);
        g.drawString("x", getWidth() - padding + 10, padding + 5);

        // y-axis
        g.drawLine(padding, padding, padding, getHeight() - padding);
        g.drawString("y", padding - 15, getHeight() - padding + 15);

        // Board rectangle
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(padding, padding, getWidth() - 2 * padding, getHeight() - 2 * padding);

        // Draw notes 
        for (NoteRect n : notes) {
            int px = mapX(n.x);
            int py = mapY(n.y);
            int pw = Math.max(6, mapW(n.w));
            int ph = Math.max(6, mapH(n.h));

            g.setColor(n.color);
            g.fillRect(px, py, pw, ph);

            g.setColor(Color.BLACK);
            g.drawRect(px, py, pw, ph);
        }

        // Draw pins
        for (Point p : pins) {
            int cx = mapX(p.x);
            int cy = mapY(p.y);

            g.setColor(Color.BLACK);
            g.fillOval(cx - 4, cy - 4, 8, 8);
        }
    }

    // Basic color helper
    public static Color parseColor(String name) {
        if (name == null) return Color.LIGHT_GRAY;
        String c = name.trim().toLowerCase();
        if (c.equals("red")) return new Color(230, 90, 90);
        if (c.equals("green")) return new Color(120, 210, 120);
        if (c.equals("blue")) return new Color(100, 140, 240);
        if (c.equals("yellow")) return new Color(240, 230, 120);
        if (c.equals("white")) return Color.WHITE;
        if (c.equals("black")) return Color.BLACK;
        return Color.LIGHT_GRAY;
    }
}
