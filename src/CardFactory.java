import java.util.Locale;

public class CardFactory {
    private Card card;

    public CardFactory(CardType type) {
        card = switch (type) {
            case STRIKE -> new AttackCard("Strike",
                        "Deal 6 damage.",
                        1,
                        6,
                        true);
            case BASH -> new AttackCard("Bash",
                        "Deal 8 damage.",
                        2,
                        8,
                        true);
            case ANGER -> new AttackCard("Anger",
                        "Deal 6 damage. Add a copy of this card into the discard pile.",
                        0,
                        6,
                        true);
            case BLUDGEON -> new AttackCard("Bludgeon",
                        "Deal 32 damage",
                        3,
                        32);
            case DEFEND -> new SkillCard("Defend",
                        "Gain 5 block",
                        1,
                        5,
                        false);
            case SIO -> new SkillCard("Shrug It Off",
                        "Gain 8 block. Draw 1 card",
                        1,
                        8,
                        false);
            case BAT_TRA -> new SkillCard("Battle Trance",
                        "Draw 3 cards. You cannot draw cards this turn.",
                        0,
                        0,
                        false);
            case DOUBLE_TAP -> new SkillCard("Double Tap",
                        "This turn, your next attack is played twice",
                        1,
                        0,
                        false);
            case SLIMED -> new StatusCard("Slimed",
                    "Exhaust",
                    1,
                    true);
            default -> null;
        };
    }

    public void cleanup(){
        if(card != null){
            card = null;
        }
    }

    public Card getCard() {
        return card;
    }
}
