class Note {
    private final int x, y;
    private final String colour;
    private final String message;
    private final boolean pinned;

    public Note(int x, int y, String colour, String message, boolean pinned) {
        this.x = x;
        this.y = y;
        this.colour = colour;
        this.message = message;
        this.pinned = pinned;
    }
}
