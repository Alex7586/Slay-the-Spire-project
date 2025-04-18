import java.util.ArrayList;
import java.util.List;

public class Enemy extends Entity {
    private final int damage;
    private double probability = 1;
    private int curlUpBlock;

    public Enemy(String name, int maxHp, int defense, int damage){
        super(name, maxHp, defense);
        this.damage = damage;
        if(name.equals("Louse"))
            curlUpBlock = (int)(Math.random() * 5 + 3);
    }

    public double getProbability() {return probability;}

    private void Cultist(int turn, Player p) {
        if(turn == 1){
            applyEffect(3, "Ritual");
            return;
        }
        p.takeDamage(damage + Utility.nvl(effects.get("Strength"),0));
        applyEffect(3, "Strength");
    }
    private void JawWorm(int turn, Player p) {
        if(turn == 1){
            p.takeDamage(damage);
            return;
        }
        //Bellow
        if(probability < 0.45){
            applyEffect(3,"Strength");
            this.gainDefense(6);
            return;
        }
        //Thrash
        if(probability < 0.75){
            p.takeDamage(damage - 3);
            this.gainDefense(5);
            return;
        }
        //Chomp
        p.takeDamage(damage);
    }
    private void Louse(Player p) {
        if(probability < 0.25){
            applyEffect(3, "Strength");
            return;
        }
        p.takeDamage(damage);
    }
    private void AcidSlime(Player p) {
        if(name.equals("Acid Slime (L)") && hp < 0.5 * maxHP){
            hp = 0;
            return;
        }
        if(probability == 1)
            return;
        if(probability < 0.3){
            p.takeDamage(damage - (name.equals("Acid Slime (L)")?5:3));
            return;
        }
        if(probability < 0.6){
            p.applyEffect((name.equals("Acid Slime (L)"))?2:1, "Weak");
            return;
        }
        p.takeDamage(damage);

    }

    public String intention(int turn){
        StringBuilder builder = new StringBuilder();
        builder.append("Intentions:\n");
        switch(name){
            case "Cultist":{
                if(turn == 1){
                    builder.append("Gains 3 Ritual\n");
                    break;
                }
                builder.append("Deal ")
                        .append((damage + Utility.nvl(effects.get("Strength"), 0)) *
                                (effects.get("Weak") == null ? 1 : 0.75))
                        .append(" damage.\n");
                break;
            }
            case "Jaw Worm":{
                if(turn == 1){
                    //Chomp
                    builder.append("Deal ")
                            .append((damage + Utility.nvl(effects.get("Strength"), 0)) *
                                    (effects.get("Weak") == null ? 1 : 0.75))
                            .append(" damage.\n");
                    break;
                }
                probability = Math.random();
                //Bellow
                if(probability < 0.45){
                    builder.append("Gain 3 Strength\nand 6 defense.\n");
                    break;
                }
                //Thrash
                if(probability < 0.75){
                    builder.append("Deal ")
                            .append((damage - 3 + Utility.nvl(effects.get("Strength"), 0)) *
                                    (effects.get("Weak") == null ? 1 : 0.75))
                            .append(" damage.\n");
                    builder.append("Gain 5 defense.\n");
                    break;
                }

                //Chomp
                builder.append("Deal ")
                        .append((damage + Utility.nvl(effects.get("Strength"), 0)) *
                                (effects.get("Weak") == null ? 1 : 0.75))
                        .append(" damage.\n");
                break;
            }
            case "Louse":{
                if(curlUpBlock != 0){
                    builder.append("Gain ")
                            .append(curlUpBlock)
                            .append(" block upon first\nreceiving attack damage.\n");
                }
                probability = Math.random();
                if(probability < 0.25){
                    builder.append("Gain 3 Strength.\n");
                    break;
                }
                builder.append("Deal ")
                        .append((damage + Utility.nvl(effects.get("Strength"), 0)) *
                                (effects.get("Weak") == null ? 1 : 0.75))
                        .append(" damage.\n");
                break;
            }
            case "Acid Slime (L)", "Acid Slime (M)":{
                if(name.equals("Acid Slime (L)")){
                    builder.append("At 50% or less, it dies and\nspawn 2 Acid Slimes (M).\n");
                }
                probability = Math.random();
                //Corrosive Spit
                if(probability < 0.3){
                    builder.append("Deal ")
                            .append((damage - (name.equals("Acid Slime (L)")?5:3)
                                    + Utility.nvl(effects.get("Strength"), 0)) *
                                    (effects.get("Weak") == null ? 1 : 0.75))
                            .append(" damage.\nShuffles ")
                            .append(name.equals("Acid Slime (L)")?2:1)
                            .append(" Slimed into the\ndiscard pile.\n");
                    break;
                }
                if(probability < 0.6){
                    builder.append("Inflicts ")
                            .append((name.equals("Acid Slime (L)"))?2:1)
                            .append(" weak.");
                    break;
                }
                builder.append("Deal ")
                        .append((damage + Utility.nvl(effects.get("Strength"), 0)) *
                                (effects.get("Weak") == null ? 1 : 0.75))
                        .append(" damage.\n");
                break;
            }
        }
        //builder.append("============================\n");
        return builder.toString();
    }

    public List<String> getStatsBox() {
        List<String> lines = new ArrayList<>();
        String border = "============================";
        String centeredName = String.format("%-28s", String.format("%" + (14 + name.length()/2) + "s", name));
        lines.add(border);
        lines.add(centeredName);
        lines.add(border);
        StringBuilder hpLine = new StringBuilder(String.format("  ❤ HP    : %s / %s", hp, maxHP));
        lines.add(hpLine.append(String.format("%-"+(border.length() - hpLine.length())+"s", "")).toString());
        if (defense > 0) {
            lines.add(String.format("  ⛊ Defense : %-14s", defense));
        } else {
            lines.add(String.format("%-28s", "")); // blank line for alignment
        }
        if(!effects.isEmpty()){
            lines.add(String.format("%-28s","Effects:"));
            for(String i : effects.keySet()){
                StringBuilder effect = new StringBuilder(i + " : " + effects.get(i));
                lines.add(String.format("%-28s", effect));
                /*effect = switch(i){
                    case "Vulnerable" -> new StringBuilder("(take 50% more damage)");
                    case "Weak" -> new StringBuilder("(deal 25% less damage)");
                    case "Ritual" -> {
                        lines.add(String.format("%-28s", "(at the end of the turn gain"));
                        yield new StringBuilder(effects.get(i) + " strength)");
                    }
                    case "Strength" -> new StringBuilder("(deal +" + effects.get(i) + " damage)");
                    default -> new StringBuilder("(no effect)");
                };
                lines.add(String.format("%-28s", effect));*/
            }
        }

        //lines.add(border);
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
                Cultist(turn, p);
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
