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
}
