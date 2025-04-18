public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameHandler game = new GameHandler();
        int floor = 1;
        while(game.playerAlive())
            game.combat(floor++);


    }
}