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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" _________________\n");
        builder.append("|")
                .append(cost);
        builder.append(" ".repeat(10));
        builder.append("Status|\n");
        builder.append("|                 |\n");
        builder.append('|').append(name);
        builder.append(" ".repeat(17 - name.length()));
        builder.append("|\n");
        builder.append("|                 |\n");
        int left = 0;
        while(left < description.length()){
            builder.append('|')
                    .append(description, left, Math.min(left+17, description.length()));
            if(left + 17 > description.length())
                builder.append(" ".repeat(left+17 - description.length()));
            builder.append("|\n");
            left += 17;
        }
        builder.append("|                 |\n")
                .append(" \\_______________/\n");

        return builder.toString();
    }
}
