package BulletinBoard;

class Note {
    private final int x, y;
    private final String colour;
    private final String message;
    private boolean pinned;

    public Note(int x, int y, String colour, String message, boolean pinned) {
        this.x = x;
        this.y = y;
        this.colour = colour;
        this.message = message;
        this.pinned = pinned;
    }

    public void pinned() {
        this.pinned = true;
    }

    public void unpinned() {
        this.pinned = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColour() {
        return colour;
    }

    public String getMes() {
        return message;
    }

    public boolean getPinned() {
        return pinned;
    }

    public boolean contains(Pin pin, int nWid, int nHei) {
        return x <= pin.getX() && x + nWid >= pin.getX() &&
                    y <= pin.getY() && y + nHei >= pin.getY();
    }
}
