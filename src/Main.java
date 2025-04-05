public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameHandler game = new GameHandler();
        game.combat();

        /*CardFactory cardFactory = new CardFactory(CardType.ANGER);
        AttackCard card = (AttackCard)cardFactory.getCard();
        System.out.println(card);*/
    }
}