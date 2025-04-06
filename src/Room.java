public class Room {
    int id = -1;
    RoomType type;
    String symbol = "";

    Room(int id) {
        this.id = id;
    }

    Room(RoomType type) {
        this.type = type;
        this.symbol = getSymbol();
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return switch (type) {
            case SHOP -> "S";
            case TREASURE -> "T";
            case REST -> "R";
            case ENEMY -> "E";
            default -> "";
        };
    }

    @Override
    public String toString() {
        return symbol;
    }

}
