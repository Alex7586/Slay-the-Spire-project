import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class GameHandler {
    private final Player player;
    private CardFactory cardFactory;
    private EnemyFactory enemyFactory;
    private ArrayList<Card> drawDeck;
    private ArrayList<Card> discardPile;
    private ArrayList<Enemy> enemies;
    private ArrayList<List<String>> intentions;
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
        enemies = new ArrayList<>();
        discardPile = new ArrayList<>();
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

    public void combat() throws InterruptedException {
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
            resetStats();
            turn++;
        }
    }

    private void showEnemies(int turn, int maxIntention){
        ArrayList<List<String>> enemyBoxes = new ArrayList<>();
        for(Enemy enemy : enemies){
            enemyBoxes.add(enemy.getStatsBox());
        }
        for(int i = 0; i < enemyBoxes.getFirst().size(); i++){
            StringBuilder line = new StringBuilder();
            for (List<String> enemyBox : enemyBoxes)
                line.append(enemyBox.get(i)).append(' ');
            System.out.println(line);
        }
        for(int i = 0; i < maxIntention; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < intentions.size(); j++){
                if (i < intentions.get(j).size())
                    line.append(intentions.get(j).get(i)).append(' ');
                else
                    line.append(String.format("%-28s", ""));
            }
            System.out.println(line);
        }
    }

    private void playerTurn(int turn) throws InterruptedException{
        boolean endTurn = false;
        for(int i = 0; i < 5; i++)
            drawCard();
        intentions = new ArrayList<>();
        int maxIntention = -1;
        for (Enemy enemy : enemies) {
            List<String> intention = enemy.getIntensionsBox(turn);
            maxIntention = Math.max(maxIntention, intention.size());
            intentions.add(intention);
        }

        while(!endTurn){
            showEnemies(turn, maxIntention);

            ArrayList<List<String>> cardBoxes = new ArrayList<>();
            int maxCardHeight = -1;
            for(Card card : player.getHand()){
                List<String> cardBox = card.getCardBox();
                maxCardHeight = Math.max(maxCardHeight, cardBox.size());
                cardBoxes.add(cardBox);
            }
            for(int i = 0; i < maxCardHeight; i++){
                StringBuilder line = new StringBuilder();
                for (List<String> cardBox : cardBoxes)
                    if (i < cardBox.size())
                        line.append(cardBox.get(i)).append(' ');
                    else
                        line.append(String.format("%-19s", ""));
                System.out.println(line);
            }


            /*for(Card card : player.getHand())
                System.out.println(card);*/

            System.out.println(player);
            System.out.println("Choose an option:");
            System.out.println(" -> Play a card\t\t\t[#no_of_card]");
            System.out.println(" -> Show draw deck\t\t[draw]");
            System.out.println(" -> Show discard pile\t[discard]");
            System.out.println(" -> End your turn\t\t[end]");
            Scanner in = new Scanner(System.in);
            String input = in.next();
            try{
                int carte = Integer.parseInt(input);
                if(carte < 1 || carte > player.getHand().size()){
                    System.out.println("Not a valid imput!");
                    Thread.sleep(1000);
                }
                Card chosenCardToPlay = player.getHand().get(carte - 1);
                if(chosenCardToPlay.getCost() > player.getMana()){
                    System.out.println("Not enough mana!");
                    Thread.sleep(1000);
                    continue;
                }
                discardPile.add(chosenCardToPlay);
                int enemyToAttack = 1;
                if(chosenCardToPlay instanceof AttackCard){
                    System.out.println("Which enemy to attack? [1..no_of_enemies]");
                    enemyToAttack = in.nextInt();
                }
                playCard(chosenCardToPlay, enemyToAttack - 1);
                player.consumeCard(chosenCardToPlay);
                player.setMana(player.getMana() - chosenCardToPlay.getCost());
            }
            catch(NumberFormatException e){
                switch(input) {
                    case "end":
                        endTurn = true;
                        canDraw = true;
                        attackTwice = false;
                        break;
                    case "draw":
                        for(Card card : drawDeck)
                            System.out.println(card);
                        System.out.println("Go back [exit]");
                        while(!in.next().equals("exit"));
                        break;
                    case "discard":
                        for(Card card : discardPile)
                            System.out.println(card);
                        System.out.println("Go back [exit]");
                        while(!in.next().equals("exit"));
                        break;
                    default:
                        System.out.println("Not a valid input!");
                        break;
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

    private void resetStats(){
        player.setMana(player.getMaxMana());
        for(Card card:player.getHand()){
            discardPile.add(card);
            player.consumeCard(card);
        }
        player.setDefense(0);
        for(Enemy enemy:enemies){
            enemy.setDefense(0);
        }
    }

    private void playCard(Card card, int enemyPosition){
        card.play(player, enemies.get(enemyPosition));
        if(card.isExhausting()){
            discardPile.remove(card);
        }
        if(card instanceof AttackCard && attackTwice){
            card.play(player, enemies.get(enemyPosition));
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
        if(enemies.get(enemyPosition).getHP() < 0){
            enemies.remove(enemyPosition);
            intentions.remove(enemyPosition);
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
