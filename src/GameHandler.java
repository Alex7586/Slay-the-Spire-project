import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

public class GameHandler {
    private final Player player;
    private CardFactory cardFactory;
    private EnemyFactory enemyFactory;
    private ArrayList<Card> drawDeck;
    private ArrayList<Card> discardPile;
    private ArrayList<Enemy> enemies;
    private Boolean canDraw = true;
    private Boolean attackTwice = false;

    private boolean enemiesDead(){
        return enemies.stream()
                .map(Entity::alive)
                .filter(item -> item.equals(false))
                .count() == enemies.size();
    }

    public GameHandler() {
        player = Player.getInstance();
        drawDeck = player.getCards();
    }

    public void showGameStatus() {
        System.out.println(player);
        System.out.println("\nDraw deck:\n");
        for(Card card : drawDeck) {
            System.out.println(card);
        }
    }

    private void makeEnemy(EnemyType type){
        enemyFactory = new EnemyFactory(type);
        enemies.add(enemyFactory.getEnemy());
        enemyFactory.cleanup();
    }

    public void combat(){
        int turn = 1;
        drawDeck = player.getCards();
        int numOfEnemies = (int)(Math.random() * 3 + 1);
        for(int i = 0; i < numOfEnemies; i++){
            double prob = Math.random();
            if(prob < 0.2) makeEnemy(EnemyType.CULTIST);
            else if(prob < 0.4) makeEnemy(EnemyType.JAW_WORM);
            else if(prob < 0.6) makeEnemy(EnemyType.LOUSE);
            else if(prob < 0.8) makeEnemy(EnemyType.ACID_SLIME_L);
            else makeEnemy(EnemyType.ACID_SLIME_M);
        }
        while(player.alive() && !enemiesDead()){
            playerTurn(turn);
            enemyTurn(turn);
            turn++;
        }
    }

    private void playerTurn(int turn){
        boolean endTurn = false;
        for(int i = 0; i < 5; i++)
            drawCard();
        while(!endTurn){
            for(Enemy enemy : enemies)
                enemy.intention(turn);
            for(Card card : player.getHand())
                System.out.println(card);

            System.out.println(player);
            System.out.println("Choose a card or end your turn [#no_of_card / end]");
            Scanner in = new Scanner(System.in);
            String input = in.next();
            try{
                int carte = Integer.parseInt(input);
                Card chosenCardToPlay = player.getHand().get(carte - 1);
                discardPile.add(chosenCardToPlay);
                int enemyToAttack = 1;
                if(chosenCardToPlay instanceof AttackCard){
                    System.out.println("Which enemy to attack? [1..no_of_enemies]\n");
                    enemyToAttack = in.nextInt();
                }
                playCard(chosenCardToPlay, enemies.get(enemyToAttack - 1));
                player.chooseCardToPlay(chosenCardToPlay);
            }
            catch(NumberFormatException e){
                if(input.equals("end")) {
                    endTurn = true;
                    canDraw = true;
                    attackTwice = false;
                }
            }
        }
    }

    private void enemyTurn(int turn){
        ArrayList<Enemy> aux = new ArrayList<>();
        for(Enemy enemy : enemies){
            if(Objects.equals(enemy.getName(), "Acid Slime L") && enemy.getHP()==0){
                makeEnemy(EnemyType.ACID_SLIME_M);
                makeEnemy(EnemyType.ACID_SLIME_M);
                continue;
            }
            if(enemy.getHP() <= 0)
                continue;
            aux.add(enemy);
            enemy.play(turn, player);
            if((enemy.getName().equals("Acid Slime L") ||
                    enemy.getName().equals("Acid Slime M")) &&
                    enemy.getProbability() < 0.45){
                cardFactory = new CardFactory(CardType.SLIMED);
            }
        }
        enemies = aux;
    }

    private void playCard(Card card, Enemy enemy){
        card.play(player, enemy);
        if(card.isExhausting()){
            discardPile.remove(card);
        }
        if(card instanceof AttackCard && attackTwice){
            card.play(player, enemy);
            attackTwice = false;
        }
        switch(card.getName()){
            case "Anger":
                cardFactory = new CardFactory(CardType.ANGER);
                discardPile.add(cardFactory.getCard());
                cardFactory.cleanup();
                break;
            case "Shrug It Off":
                if(!canDraw)
                    break;
                drawCard();
                break;
            case "Battle Trance":
                if(!canDraw)
                    break;
                for(int i = 0; i < 3; i++)
                    drawCard();
                break;
            case "Double Tap":
                attackTwice = true;
                break;
        }
    }

    private void drawCard(){
        if(drawDeck.isEmpty()){
            drawDeck = discardPile;
            discardPile = new ArrayList<>();
        }
        int draw = (int)(Math.random() * drawDeck.size());
        player.drawCard(drawDeck.get(draw));
        drawDeck.remove(draw);
    }
}
