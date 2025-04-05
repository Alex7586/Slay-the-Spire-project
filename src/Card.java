public abstract class Card {
    protected String name;
    protected String description;
    protected int cost;
    protected Boolean exhaust;
    protected Boolean playedOnSomeone;

    public Card(){
        this.name = "Blank Card";
        this.description = "It does nothing :/";
        this.cost = 0;
    }

    public Card(String name, String description, int cost, Boolean exhaust){
        this(name, description, cost, false, exhaust);
    }
    public Card(String name, String description, int cost, Boolean playedOnSomeone, Boolean exhaust){
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.playedOnSomeone = playedOnSomeone;
        this.exhaust = exhaust;
    }

    public Card(Card other){
        this.name = other.name;
        this.description = other.description;
        this.cost = other.cost;
        this.exhaust = other.exhaust;
    }

    public String getName(){ return this.name; }
    public String getDescription(){ return this.description; }
    public int getCost(){ return this.cost; }

    public boolean isExhausting(){
        return exhaust;
    }

    @Override
    public String toString() {
        return "Card {\n" +
                "name = '" + name + '\'' +
                ",\ndescription = '" + description + '\'' +
                ",\ncost = " + cost +
                "\n}";
    }

    public abstract Card clone();
    public abstract void play(Player p, Enemy e);
}
