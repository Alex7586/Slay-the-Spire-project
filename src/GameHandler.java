import java.util.*;

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
    private GameMap gameMap;
    private static final List<CardType> VALUES = List.of(CardType.values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private final Scanner in = new Scanner(System.in);


    private boolean enemiesDead(){
        return enemies.stream()
                .map(Entity::alive)
                .filter(item -> item.equals(false))
                .count() == enemies.size();
    }

    public boolean playerAlive(){
        return player.alive();
    }

    public GameHandler() {
        player = Player.getInstance();
        drawDeck = player.getCards();
        enemies = new ArrayList<>();
        discardPile = new ArrayList<>();
        gameMap = new GameMap();
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

    public void gainRewards(){
        discardPile = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        cardFactory = new CardFactory(VALUES.get(RANDOM.nextInt(SIZE)));
        cards.add(cardFactory.getCard());
        cardFactory.cleanup();

        cardFactory = new CardFactory(VALUES.get(RANDOM.nextInt(SIZE)));
        cards.add(cardFactory.getCard());
        cardFactory.cleanup();

        cardFactory = new CardFactory(VALUES.get(RANDOM.nextInt(SIZE)));
        cards.add(cardFactory.getCard());
        cardFactory.cleanup();

        System.out.println("Gain rewards");

        ArrayList<List<String>> cardBoxes = new ArrayList<>();
        int maxCardHeight = -1;
        for(Card card : cards){
            List<String> cardBox = card.getCardBox();
            maxCardHeight = Math.max(maxCardHeight, cardBox.size());
            cardBoxes.add(cardBox);
        }
        showCards(cardBoxes, maxCardHeight);
        System.out.println("Choose a card [1/2/3]: ");
        int option = in.nextInt();
        player.addCard(cards.get(option - 1));
    }

    private void generateEnemies(int floor){
        boolean bigEnemy = Math.random() < 0.5;
        int numOfEnemies = Math.min(floor, 3);
        if(floor > 3 && bigEnemy)
            numOfEnemies--;
        for(int i = 0; i < numOfEnemies; i++){
            Double prob = Math.random();
            //Big Enemies: [Cultist, Acid_Slime_L]
            if(bigEnemy && floor > 3){
                if(prob < 0.5) makeEnemy(EnemyType.CULTIST);
                else makeEnemy(EnemyType.ACID_SLIME_L);
                bigEnemy = false;
                continue;
            }
            switch(prob){
                case Double p when p < 0.33:
                    makeEnemy(EnemyType.JAW_WORM);
                    break;
                case Double p when p < 0.66:
                    makeEnemy(EnemyType.LOUSE);
                    break;
                default:
                    makeEnemy(EnemyType.ACID_SLIME_M);
                    break;
            }
        }
    }

    public void combat(int floor) throws InterruptedException {
        int turn = 1;
        drawDeck = player.getCards();
        generateEnemies(floor);

        while(playerAlive() && !enemiesDead()){
            playerTurn(turn);
            enemyTurn(turn);
            resetStats();
            turn++;
        }

        if(enemiesDead())
            gainRewards();
        else
            System.out.println("You died!!! :(");
    }

    private void showEnemies(int turn, int maxIntention){
        int maxSize = 0;
        ArrayList<List<String>> enemyBoxes = new ArrayList<>();
        for(Enemy enemy : enemies){
            enemyBoxes.add(enemy.getStatsBox());
            if(enemyBoxes.getLast().size() > maxSize)
                maxSize = enemyBoxes.getLast().size();
        }
        showBox(maxSize, enemyBoxes);
        showBox(maxIntention, intentions);
    }

    private void showBox(int maxSize, ArrayList<List<String>> boxes) {
        for(int i = 0; i < maxSize  ; i++) {
            StringBuilder line = new StringBuilder();
            for (List<String> box : boxes) {
                if(i == box.size()){
                    line.append(String.format("%-29s", ""));
                    continue;
                }
                if (i < box.size())
                    line.append(box.get(i)).append(' ');
                else
                    line.append(String.format("%-29s", ""));
            }
            System.out.println(line);
        }
        StringBuilder border = new StringBuilder("=".repeat(28));
        border.append(" ");
        border.repeat(border, boxes.size()-1);
        System.out.println(border);
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
            showCards(cardBoxes, maxCardHeight);

            System.out.println(player);
            System.out.println("Choose an option:");
            System.out.println(" -> Play a card\t\t\t[#no_of_card]");
            System.out.println(" -> Show draw deck\t\t[draw]");
            System.out.println(" -> Show discard pile\t[discard]");
            System.out.println(" -> End your turn\t\t[end]");

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
                if(!chosenCardToPlay.isExhausting())
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

            for(int i = 0; i < 50; i++) System.out.println();
            if(enemies.isEmpty())
                endTurn = true;
        }
    }

    private void showCards(ArrayList<List<String>> cardBoxes, int maxCardHeight) {
        for(int i = 0; i < maxCardHeight; i++){
            StringBuilder line = new StringBuilder();
            for (List<String> cardBox : cardBoxes)
                if (i < cardBox.size())
                    line.append(cardBox.get(i)).append(' ');
                else
                    line.append(String.format("%-20s", ""));
            System.out.println(line);
        }
    }

    private void enemyTurn(int turn){
        ArrayList<Enemy> aux = new ArrayList<>();
        for(Enemy enemy : enemies){
            if(Objects.equals(enemy.getName(), "Acid Slime (L)") && enemy.getHP() < 0.5 * enemy.getMaxHP()){
                enemyFactory = new EnemyFactory(EnemyType.ACID_SLIME_M, enemy.getHP());
                aux.add(enemyFactory.getEnemy());
                enemyFactory.cleanup();

                enemyFactory = new EnemyFactory(EnemyType.ACID_SLIME_M, enemy.getHP());
                aux.add(enemyFactory.getEnemy());
                enemyFactory.cleanup();
                continue;
            }
            if(enemy.getHP() <= 0)
                continue;
            aux.add(enemy);
            enemy.play(turn, player);
            if(enemy.getProbability() < 0.45){
                switch(enemy.getName()){
                    case "Acid Slime (L)":
                        cardFactory = new CardFactory(CardType.SLIMED);
                        discardPile.add(cardFactory.getCard());
                        cardFactory.cleanup();
                    case "Acid Slime (M)":
                        cardFactory = new CardFactory(CardType.SLIMED);
                        discardPile.add(cardFactory.getCard());
                        cardFactory.cleanup();
                        break;
                }
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
        resetBuffs(player);
        for(Enemy enemy : enemies)
            resetBuffs(enemy);
    }

    private void resetBuffs(Entity entity){
        HashMap<String, Integer> updatedEffects = new HashMap<>();
        for(String effect : entity.getEffects().keySet()){
            switch(effect){
                case "Vulnerable", "Weak":
                    entity.applyEffect(-1, effect);
                    break;
                default:
                    break;
            }
        }
        entity.updateEffects();
    }

    private void playCard(Card card, int enemyPosition){
        card.play(player, enemies.get(enemyPosition));
        if(card.isExhausting()){
            discardPile.remove(card);
        }
        switch(card.getName()){
            case "Anger":
                cardFactory = new CardFactory(CardType.ANGER);
                discardPile.add(cardFactory.getCard());
                cardFactory.cleanup();
                break;
            case "Bash":
                enemies.get(enemyPosition).applyEffect(2, "Vulnerable");
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
        if(card instanceof AttackCard && attackTwice){
            //card.play(player, enemies.get(enemyPosition));
            attackTwice = false;
            playCard(card, enemyPosition);
            return;
        }
        if(enemies.get(enemyPosition).getHP() <= 0){
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
