import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Entity {
    protected String name;
    protected int maxHP;
    protected int hp;
    protected int defense;
    protected HashMap<String, Integer> effects;

    Entity(){}

    Entity(String name, int hp, int defense) {
        this.name = name;
        this.hp = this.maxHP = hp;
        this.defense = defense;
        this.effects = new HashMap<>();
    }

    public String getName() {
        return name;
    }
    public int getHP() {return hp;}
    public int getMaxHP() {return maxHP;}
    public HashMap<String, Integer> getEffects() {return effects;}

    public void updateEffects(){
        effects.entrySet().stream()
                .filter(a->a.getValue().equals(0))
                .map(Map.Entry::getKey).toList().forEach(
                        effects.keySet()::remove);
    }

    public void gainHp(int amount){
        this.hp += amount;
    }

    public Boolean alive(){
        return hp > 0;
    }

    public void gainDefense(int amount) {
        this.defense += amount;
    }

    public void takeDamage(int amount) {
        amount = (int)((effects.get("Vulnerable") != null) ? 1.5 * amount : amount);
        if(amount <= defense){
            defense -= amount;
            return;
        }
        this.hp = this.hp + this.defense - amount;
        defense = 0;
    }

    public void applyEffect(int amount, String effect){
        /*if(effects.get(effect) != null)
            effects.put(effect, amount);
        else
            effects.replace(effect, amount + effects.get(effect));*/
        effects.compute(effect, (_,v) -> Utility.nvl(v,0) + amount);
        /*effects.computeIfPresent(effect, (_, v) -> v + amount);
        effects.computeIfAbsent(effect, _ -> amount);*/
    }
}
