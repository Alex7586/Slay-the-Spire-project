import java.util.ArrayList;

public class Player extends Entity {
    private static Player instance;
    private int mana;
    private final int maxMana = 3;
    private ArrayList<Card> currHand;
    private ArrayList<Card> cards;
    private CardFactory cardFactory;

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    private Player() {
        super("John", 100, 0);
        this.mana = 3;
        this.currHand = new ArrayList<>();
        cards = new ArrayList<>();
        for(int i = 0; i < 5; i++)
            addCard(CardType.STRIKE);
        for(int i = 0; i < 4; i++)
            addCard(CardType.DEFEND);
        addCard(CardType.BASH);
    }

    public static Player getInstance(){
        if(instance == null)
            instance = new Player();
        return instance;
    }

    @Override
    public String toString() {
        return "============================\n" +
                "        🛡️ " + name + " 🛡️\n" +
                "============================\n" +
                "  ❤ HP    : " + hp + "\n" +
                "  ⛊ Defense : " + defense + "\n" +
                "  ★ Mana  : " + mana + " / " + maxMana + "\n" +
                "============================\n";
    }

    public ArrayList<Card> getHand() {
        /*ArrayList<Card> returnHand = new ArrayList<>();
        for (Card card : currHand) {
            returnHand.add(card.clone());
        }*/
        return (ArrayList<Card>)currHand.clone();
    }

    public int getMaxMana(){
        return maxMana;
    }

    public ArrayList<Card> getCards() {
        ArrayList<Card> returnCards = new ArrayList<>();
        for (Card card : cards) {
            returnCards.add(card.clone());
        }
        return returnCards;
    }

    public void drawCard(Card card) {
        currHand.add(card);
    }

    public void consumeCard(Card card) {
        currHand.remove(card);
    }

    private void addCard(CardType type) {
        cardFactory = new CardFactory(type);
        cards.add(cardFactory.getCard());
        cardFactory.cleanup();
    }

    public void setDefense(int amount){
        this.defense = amount;
    }
}
