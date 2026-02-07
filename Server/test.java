package Server;

import BulletinBoard.*;
import java.util.*;

public class test {
    public static void main (String[] args) {
        String[] colours = {"red", "white", "green"};
        Board board = new Board(200, 100, 20, 10, colours);
        BBoardProtocol protocol = new BBoardProtocol(board);
        String inp, out;
        Scanner keyboard = new Scanner(System.in);
        while (true) {
            inp = keyboard.nextLine();
            out = protocol.processInput(inp);
            System.out.println("--" + out);
        }
    }
    
}
