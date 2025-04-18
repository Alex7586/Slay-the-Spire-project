import java.util.ArrayList;
import java.util.List;

public class AttackCard extends Card {
    private int damage;

    public AttackCard(){
        this("Damage Blank Card", "Deal 0 damage", 0, 0, false, false);
    }
    public AttackCard(String name, String description, int cost){
        this(name , description, cost, 0, false, false);
    }
    public AttackCard(String name, String description, int cost, int damage){
        this(name, description, cost, damage, false, false);
    }
    public AttackCard(String name, String description, int cost, int damage, Boolean playedOnSomeone){
        this(name, description, cost, damage, playedOnSomeone, false);
    }
    public AttackCard(String name, String description, int cost, int damage, Boolean playedOnSomeone, Boolean exhaust){
        super(name, description, cost, playedOnSomeone, exhaust);
        this.damage = damage;
    }
    public AttackCard(AttackCard other){
        super(other.name, other.description, other.cost, other.playedOnSomeone, other.exhaust);
        this.damage = other.damage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" _________________\n");
        builder.append("|")
                .append(cost);
        builder.repeat(' ', 10);
        /*for(int i = 0; i < 10; i++) builder.append(" ".repeat(10));*/
        builder.append("Attack|\n");
        builder.append("|                 |\n");
        builder.append('|').append(name);
        builder.repeat(' ', 17 - name.length());
        /*for(int i = name.length(); i < 17; i++) builder.append(' ');*/
        builder.append("|\n");
        builder.append("|                 |\n");
        StringBuilder descriptionEffects = new StringBuilder(description);
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
        return new AttackCard(this);
    }

    @Override
    public void play(Player player, Enemy enemy) {
        int dmg = damage + Utility.nvl(player.getEffects().get("Strength"), 0);
        if(player.getEffects().get("Weak") != null)
            dmg = (int)(0.75 * dmg);
        enemy.takeDamage(dmg);
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
