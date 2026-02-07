// BoardPanel.java
// Draws a simple board + notes as rectangles + pinned dot

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {

    private int boardW = 200;
    private int boardH = 100;
    private int noteW = 20;
    private int noteH = 10;

    public static class NoteDraw {
        public int x, y;
        public Color color;
        public boolean pinned;
        public String msg;

        public NoteDraw(int x, int y, Color color, boolean pinned, String msg) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.pinned = pinned;
            this.msg = msg;
        }
    }

    private final List<NoteDraw> notes = new ArrayList<>();

    public BoardPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 350));
    }

    public void setBoardConfig(int boardW, int boardH, int noteW, int noteH) {
        this.boardW = boardW;
        this.boardH = boardH;
        this.noteW = noteW;
        this.noteH = noteH;
        repaint();
    }

    public void setNotes(List<NoteDraw> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        repaint();
    }

    private int pad() { return 40; }

    private int mapX(int x) {
        int p = pad();
        int usable = getWidth() - 2 * p;
        if (usable <= 1) usable = 1;
        return p + (int) Math.round((x / (double) boardW) * usable);
    }

    private int mapY(int y) {
        int p = pad();
        int usable = getHeight() - 2 * p;
        if (usable <= 1) usable = 1;
        return p + (int) Math.round((y / (double) boardH) * usable);
    }

    private int mapW(int w) {
        int p = pad();
        int usable = getWidth() - 2 * p;
        if (usable <= 1) usable = 1;
        return (int) Math.round((w / (double) boardW) * usable);
    }

    private int mapH(int h) {
        int p = pad();
        int usable = getHeight() - 2 * p;
        if (usable <= 1) usable = 1;
        return (int) Math.round((h / (double) boardH) * usable);
    }

    public static Color parseColor(String name) {
        if (name == null) return Color.LIGHT_GRAY;
        String c = name.trim().toLowerCase();

        if (c.equals("red")) return Color.RED;
        if (c.equals("green")) return Color.GREEN;
        if (c.equals("blue")) return Color.BLUE;
        if (c.equals("yellow")) return Color.YELLOW;
        if (c.equals("white")) return Color.WHITE;
        if (c.equals("black")) return Color.BLACK;

        return Color.LIGHT_GRAY;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int p = pad();
        int bw = getWidth() - 2 * p;
        int bh = getHeight() - 2 * p;

        // Board border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(p, p, bw, bh);

        // Draw notes
        for (NoteDraw n : notes) {
            int px = mapX(n.x);
            int py = mapY(n.y);
            int pw = Math.max(8, mapW(noteW));
            int ph = Math.max(8, mapH(noteH));

            g.setColor(n.color);
            g.fillRect(px, py, pw, ph);

            g.setColor(Color.BLACK);
            g.drawRect(px, py, pw, ph);

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawString(n.msg, px + 3, py + 14);

            if (n.pinned) {
                int cx = px + pw / 2;
                int cy = py + ph / 2;
                g.setColor(Color.BLACK);
                g.fillOval(cx - 4, cy - 4, 8, 8);
            }
        }
    }
}


