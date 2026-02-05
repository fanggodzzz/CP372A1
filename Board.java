import java.util.*;

public class Board {
    private final int bWid, bHei, nWid, nHei;
    private final String[] colours;
    private final List<Note> notes = new ArrayList<>();
    private final List<Pin> pins = new ArrayList<>();

    public Board(int bWid, int bHei, int nWid, int nHei, String[] colours) {
        this.bWid = bWid;
        this.bHei = bHei;
        this.nWid = nWid;
        this.nHei = nHei;
        this.colours = colours;
    }

    public synchronized String post(int x, int y, String colour, String message)  {
        // In bound check
        if (!(x >= 0 && x + nWid <= bWid && y >=0 && y + nHei <= bHei)) {
            return "ERROR OUT_OF_BOUNDS";
        }

        // Supported colour check
        boolean ans = true;
        for (String a : colours) {
            if (a.equals(colour)) {
                ans = false;
            }
        }
        if (ans) {
            return "ERROR COLOR_NOT_SUPPORTED";
        }

        // Overlap check
        ans = True;
        for (Note a : notes) {
            if (a.x == x && )
        }


        return "OK NOTE_POSTED";
    }
    
}
