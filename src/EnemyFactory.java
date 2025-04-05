public class EnemyFactory {
    private Enemy enemy;

    public EnemyFactory(EnemyType type){
        enemy = switch(type){
            case CULTIST -> new Enemy("Cultist",
                    (int)(Math.random() * 7 + 48),
                    0,
                    6);
            case JAW_WORM -> new Enemy("Jaw Worm",
                    (int)(Math.random() * 5 + 40),
                    0,
                    11);
            case LOUSE -> new Enemy("Louse",
                    (int)(Math.random() * 6 + 10),
                    0,
                    (int)(Math.random() * 3 + 5));
            case ACID_SLIME_L -> new Enemy("Acid Slime L",
                    (int)(Math.random() * 5 + 65),
                    0,
                    16);
            case ACID_SLIME_M -> new Enemy("Acid Slime M",
                    (int)(Math.random() * 5 + 28),
                    0,
                    10);
            default -> null;
        };
    }

    public void cleanup(){
        if(enemy != null)
            enemy = null;
    }

    public Enemy getEnemy(){
        return enemy;
    }
}
