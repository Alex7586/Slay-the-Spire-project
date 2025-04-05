public abstract class Entity {
    protected String name;
    protected int maxHP;
    protected int hp;
    protected int defense;

    Entity(){}

    Entity(String name, int hp, int defense) {
        this.name = name;
        this.hp = this.maxHP = hp;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }
    public int getHP() {return hp;}

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
        if(amount <= defense){
            defense -= amount;
            return;
        }
        this.hp = this.hp + this.defense - amount;
        defense = 0;
    }
}
