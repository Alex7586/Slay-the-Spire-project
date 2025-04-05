import java.util.ArrayList;
import java.util.List;

public class StatusCard extends Card{

    public StatusCard(){}
    public StatusCard(String name, String description, int cost, Boolean exhaust){
        super(name,description,cost,exhaust);
    }
    public StatusCard(StatusCard other){
        super(other);
    }

    @Override
    public Card clone() {
        return new StatusCard(this);
    }

    @Override
    public void play(Player p, Enemy e) {
    }

    @Override
    public List<String> getCardBox(){
        List<String> lines = new ArrayList<>();
        String[] parts = toString().split("\n");
        for(String part : parts){
            lines.add(String.format("%-19s", part));
        }
        return lines;
    }
}
