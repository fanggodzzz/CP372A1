package BulletinBoard;
import java.util.*;

public class Board {
    private final Object lock = new Object();
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

    public String post(int x, int y, String colour, String message)  {
    synchronized(lock) {
        Note note = new Note(x, y, colour, message, false);

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
            if(a.equals(note)) {
                ans = true;
                break;
            }
        }
        if (ans) {
            return "ERROR COMPLETE_OVERLAP";
        }

        // Pinned or not
        for (Pin pin: pins) {
            if (note.contains(pin, nWid, nHei)) {
                note.pinned();
                break;
            }
        }

        notes.add(note);
        return "OK NOTE_POSTED";
    }
    }

    public String get(String colour, int x, int y, String refer) {
    synchronized(lock) {
        List<Note> ansList = new ArrayList<>();
        String ans = null;
        for (Note note: notes) {
            if ((colour == null || note.getColour() == colour) 
                    && ((x == -1 && y == -1) || (note.contains(new Pin(x, y), nWid, nHei))) 
                    && (refer == null || note.getMes().contains(refer))) {
                ansList.add(note);
            }
        }
        return ans;
    }
    }
    
    public String pin(int x, int y) {
    synchronized(lock) {
        boolean legit = true;
        Pin pin = new Pin(x, y);
        if (! pins.contains(pin)) {
            legit = false;
            for (Note note : notes) {
                if (note.contains(pin, nWid, nHei)) {
                    note.pinned();
                    legit = true;
                }
            }
            if (legit) {
                pins.add(pin);
            }
        }
        
        if (! legit) {
            return "ERROR NO_NOTE_AT_COORDINATE";
        }
        return "OK PIN_ADDED";
    }
    }

    private void resetPinnedStatus() {
        for (Note note: notes) {
            note.unpinned();
            for (Pin pin: pins) {
                if (note.contains(pin, nWid, nHei)) {
                    note.pinned();
                    break;
                }
            }
        }
    }

    public String unpin(int x, int y) {
    synchronized(lock) {
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
        resetPinnedStatus();
        return "OK UNPIN_COMPLETE";
    }
    }

    public String shake() {
    synchronized(lock) {
        notes.removeIf(note -> !note.getPinned());
        return "OK SHAKE_COMPLETE";
    }
    }

    public String clear() {
    synchronized(lock) {
        notes.clear();
        pins.clear();
        return "OK CLEAR_COMPLETE";
    }
    }
}
