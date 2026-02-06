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
                break;
            }
        }
        if (ans) {
            return "ERROR COLOR_NOT_SUPPORTED";
        }

        // Overlap check
        ans = false;
        for (Note a: notes) {
            if(a.getX() == x && a.getY() == y) {
                ans = true;
                break;
            }
        }
        if (ans) {
            return "ERROR COMPLETE_OVERLAP";
        }

        // Pinned or not
        boolean pinned = false;
        for (Pin pin: pins) {
            if (pin.getX() >= x && pin.getX() <= x + nWid && pin.getY() >= y && pin.getY() <= nHei) {
                pinned = true;
                break;
            }
        }

        notes.add(new Note(x, y, colour, message, pinned));

        return "OK NOTE_POSTED";
    }

    public List<Note> get(String colour, int x, int y, String refer) {
        return Null;
    }
    
    public String pin(int x, int y) {
        boolean legit = true;
        if (! pins.contains(new Pin(x, y))) {
            legit = false;
            for (Note a : notes) {
                if (a.getX() <= x && a.getX() + nWid >= x && a.getY() <= y && a.getY() + nHei >= y) {
                    a.pinned();
                    legit = true;
                }
            }
        }
        
        if (legit) {
            return "OK PIN_ADDED";
        }
        else {
            return "ERROR NO_NOTE_AT_COORDINATE";
        }
    }

    public String unpin(int x, int y) {
        int pos = -1;
        for (int i = 0; i < pins.size(); ++ i) {
            if (pins.get(i).getX() == x && pins.get(i).getY() == y) {
                pos = i;
                break;
            }
        }

        if (pos == -1) {
            return "ERROR PIN_NOT_FOUND";
        }

        pins.remove(pos);
        return "OK UNPIN_COMPLETE";
    }

    public String shake() {
        notes.removeIf(note -> !note.getPinned());
        return "OK SHAKE_COMPLETE";
    }

    public String clear() {
        notes.clear();
        pins.clear();
        return "OK CLEAR_COMPLETE"
    }
}
