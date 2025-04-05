import java.util.ArrayList;
import java.util.List;

public class Enemy extends Entity {
    private int damage;
    private double probability = 1;
    private int curlUpBlock;

    public Enemy(String name, int maxHp, int defense, int damage){
        super(name, maxHp, defense);
        this.damage = damage;
        if(name.equals("Louse"))
            curlUpBlock = (int)(Math.random() * 5 + 3);
    }

    public double getProbability() {return probability;}

    private void Cultist(Player p) {
        p.takeDamage(damage);
    }
    private void JawWorm(int turn, Player p) {
        if(turn == 1){
            p.takeDamage(damage);
            return;
        }
        //Thrash
        if(probability < 0.525){
            p.takeDamage(damage - 3);
            this.gainDefense(5);
            return;
        }
        //Chomp
        p.takeDamage(damage);
    }
    private void Louse(Player p) {
        p.takeDamage(damage);
    }
    private void AcidSlime(Player p) {
        if(name.equals("Acid Slime (L)") && hp < 0.5 * maxHP){
            hp = 0;
            return;
        }
        if(probability == 1)
            return;
        if(probability < 0.45){
            p.takeDamage(damage - (name.equals("Acid Slime (L)")?5:3));
            return;
        }
        p.takeDamage(damage);

    }

    public String intention(int turn){
        StringBuilder builder = new StringBuilder();
        builder.append(this).append("Intentions:\n");
        builder.append("Intentions:\n");
        switch(name){
            case "Cultist":{
                builder.append("Deal ")
                        .append(damage)
                        .append(" damage.\n");
                break;
            }
            case "Jaw Worm":{
                if(turn == 1){
                    //Chomp
                    builder.append("Deal ")
                            .append(damage)
                            .append(" damage.\n");
                    break;
                }
                probability = Math.random();
                //Thrash
                if(probability < 0.525){
                    builder.append("Deal ")
                            .append(damage-3)
                            .append(" damage.\n");
                    builder.append("Gain 5 defense.\n");
                    break;
                }
                //Chomp
                builder.append("Deal ")
                        .append(damage)
                        .append(" damage.\n");
                break;
            }
            case "Louse":{
                builder.append("Deal ")
                        .append(damage)
                        .append(" damage.\n");
                if(curlUpBlock != 0){
                    builder.append("Gain ")
                            .append(curlUpBlock)
                            .append(" block upon first\nreceiving attack damage.\n");
                }
                break;
            }
            case "Acid Slime (L)", "Acid Slime (M)":{
                if(name.equals("Acid Slime (L)") && hp < 0.5 * maxHP){
                    builder.append("Dies and spawn 2 Acid Slimes (M).");
                    break;
                }
                probability = Math.random();
                if(probability < 0.45){
                    builder.append("Deal ")
                            .append(damage - (name.equals("Acid Slime (L)")?5:3))
                            .append(" damage.\nShuffles ")
                            .append(name.equals("Acid Slime (L)")?2:1)
                            .append(" Slimed into the\ndiscard pile.\n");
                    break;
                }
                builder.append("Deal ")
                        .append(damage)
                        .append(" damage.\n");
                break;
            }
        }
        builder.append("============================\n");
        return builder.toString();
    }

    public List<String> getStatsBox() {
        List<String> lines = new ArrayList<>();
        String border = "============================";
        String centeredName = String.format("%-28s", String.format("%" + (14 + name.length()/2) + "s", name));
        lines.add(border);
        lines.add(centeredName);
        lines.add(border);
        lines.add(String.format("  ❤ HP    : %-14s", hp));
        if (defense > 0) {
            lines.add(String.format("  ⛊ Defense : %-14s", defense));
        } else {
            lines.add(String.format("  %-24s", "")); // blank line for alignment
        }
        lines.add(border);
        return lines;
    }

    public List<String> getIntensionsBox(int turn){
        List<String> lines = new ArrayList<>();
        String[] parts = intention(turn).split("\n");
        for(String part : parts){
            lines.add(String.format("%-28s", part));
        }
        return lines;
    }

    public void setDefense(int amount){
        this.defense = amount;
    }

    @Override
    public String toString() {
        String fancyName = "🛡️ " + name + " 🛡️";
        int padding = (28 - fancyName.length())/2;
        StringBuilder centeredName = new StringBuilder();
        centeredName.repeat(" ", padding);
        centeredName.append(fancyName).append('\n');

        return "============================\n" +
                centeredName +
                "============================\n" +
                "  ❤ HP    : " + hp + "\n" +
                ((defense > 0)?("  ⛊ Defense : " + defense + "\n"):"") +
                "============================\n";
    }

    public void play(int turn, Player p){
        switch(name){
            case "Cultist":
                Cultist(p);
                break;
            case "Jaw Worm":
                JawWorm(turn, p);
                break;
            case "Louse":
                Louse(p);
                break;
            case "Acid Slime (L)", "Acid Slime (M)":
                AcidSlime(p);
                break;
        }
    }

    @Override
    public void takeDamage(int damage) {
        if(name.equals("Louse") && curlUpBlock != 0){
            super.takeDamage(damage);
            this.defense += curlUpBlock;
            curlUpBlock = 0;
            return;
        }
        super.takeDamage(damage);
    }

}
