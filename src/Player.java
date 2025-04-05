import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Player extends Entity {
    private static Player instance;
    private int mana;
    private final int maxMana = 3;
    private ArrayList<Card> currHand;
    private ArrayList<Card> cards;
    private CardFactory cardFactory;

    private Player() {
        super("John", 50, 0);
        this.mana = 3;
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
                ((defense > 0)?("  ⛊ Defense : " + defense + "\n"):"") +
                "  ★ Mana  : " + mana + " / " + maxMana + "\n" +
                "============================\n";
    }

    public ArrayList<Card> getHand() {
        ArrayList<Card> returnHand = new ArrayList<>();
        for (Card card : currHand) {
            returnHand.add(card.clone());
        }
        return returnHand;
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

    public void chooseCardToPlay(Card card) {
        currHand.remove(card);
    }

    private void addCard(CardType type) {
        cardFactory = new CardFactory(type);
        cards.add(cardFactory.getCard());
        cardFactory.cleanup();
    }
}
