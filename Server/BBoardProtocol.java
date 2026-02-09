package Server;
import BulletinBoard.*;

public class BBoardProtocol {

    // false: Not sending connection accepted; 
    // true: Starting to receive requests and response back
    private Board board;
    private boolean state = false; 
    private final String FORMAT_ERROR = "ERROR INVALID_FORMAT The";
    
    public BBoardProtocol(Board board) {
        this.board = board;
    }

    public String processInput(String clientInput) {
        String response = null;

        if (! state) {
            response = "OK CONNECTION_ACCEPTED ";
            response += board.getBWid() + " " + board.getBHei() + ' ';
            response += board.getNWid() + " " + board.getNHei() + ' ';
            for (String colour: board.getColours()) {
                response += colour + ' ';
            }
            state = true;
        }
        else {
            if (clientInput.startsWith("POST")) {
                String[] parts = clientInput.trim().split("\\s+");

                try {
                    if (parts.length < 5) {
                        throw new Exception("wrong format");
                    }

                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    String colour = parts[3];
                    String message = "";
                    for (int i = 4; i < parts.length; ++ i) {
                        message += parts[i] + ' ';
                    }

                    response = board.post(x, y, colour, message);

                } catch (Exception e) {
                    response = FORMAT_ERROR;
                }

            }
            else if (clientInput.startsWith("GET")) {
                String[] parts = clientInput.trim().split("\\s+");

                try {

                    int x = -1, y = -1;
                    String colour = null, refer = null;

                    for (int i = 1; i < parts.length; ++ i) {
                        if (parts[i].startsWith("color=")) {
                            colour = parts[i].replace("color=", "");
                        }
                        else if (parts[i].startsWith("refersTo=")) {
                            refer = parts[i].replace("refersTo=", "");
                            ++ i;
                            for(; i < parts.length; ++ i) {
                                if (parts[i].startsWith("color=") 
                                        || parts[i].startsWith("contains=")) {
                                    break;
                                }
                                refer += ' ' + parts[i];
                            }
                            -- i;
                        }
                        else if (parts[i].startsWith("contains=")) {
                            try {
                                x = Integer.parseInt(parts[i].replace("contains=", ""));
                                y = Integer.parseInt(parts[i + 1]);
                                i = i + 1;
                            } finally {}
                        }
                        else {
                            throw new Exception("wrong format");
                        }
                    }
                    response = board.get(colour, x, y, refer);

                } catch (Exception e) {
                    response = FORMAT_ERROR;
                }

            }
            else if (clientInput.startsWith("PIN")) {
                String[] parts = clientInput.trim().split("\\s+");

                try {
                    if (parts.length != 3) {
                        throw new Exception("wrong format");
                    }

                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    response = board.pin(x, y);

                } catch (Exception e) {
                    response = FORMAT_ERROR;
                }

            }
            else if (clientInput.startsWith("UNPIN")) {
                String[] parts = clientInput.trim().split("\\s+");

                try {
                    if (parts.length != 3) {
                        throw new Exception("wrong format");
                    }

                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    response = board.unpin(x, y);

                } catch (Exception e) {
                    response = FORMAT_ERROR;
                }

            }
            else if (clientInput.trim().equals("SHAKE")) {
                response = board.shake();
            }
            else if (clientInput.trim().equals("CLEAR")) {
                response = board.clear();
            }
            else if (clientInput.trim().equals("DISCONNECT")) {
                response = "CONNECTION_CLOSED";
            }
            else {
                response = FORMAT_ERROR;
            }
        }
        return response;
    }
}
