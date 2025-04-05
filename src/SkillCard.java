import java.util.ArrayList;
import java.util.List;

public class SkillCard extends Card {
    private int block;

    public SkillCard(){
        this("","",0,0, false,false);
    }
    public SkillCard(String name, String description, int cost){
        this(name, description, cost, 0, false);
    }
    public SkillCard(String name, String description, int cost, int block){
        this(name, description, cost, block, false,false);
    }
    public SkillCard(String name, String description, int cost, int block, Boolean playedOnSomeone){
        this(name, description, cost, block, playedOnSomeone, false);
    }
    public SkillCard(String name, String description, int cost, int block, Boolean playedOnSomeone, Boolean exhaust){
        super(name, description, cost, playedOnSomeone, exhaust);
        this.block = block;
    }
    public SkillCard(SkillCard other){
        super(other.name, other.description, other.cost, other.playedOnSomeone, other.exhaust);
        this.block = other.block;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" _________________\n");
        builder.append("|")
                .append(cost);
        for(int i = 0; i < 11; i++) builder.append(' ');
        builder.append("Skill|\n");
        builder.append("|                 |\n");
        builder.append('|').append(name);
        for(int i = name.length(); i < 17; i++) builder.append(' ');
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

    @Override
    public Card clone() {
        return new SkillCard(this);
    }

    @Override
    public void play(Player player, Enemy enemy) {
        player.gainDefense(block);
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
