import java.util.ArrayList;

public class Map {
    private ArrayList<ArrayList<Room>> map;
    private ArrayList<ArrayList<Integer>> adjacentRooms;
    private final int MAP_WIDTH = 7;
    private final int MAP_HEIGHT = 15;

    public Map(){
        map = new ArrayList<>();
        for(int i = 0; i < MAP_HEIGHT; i++) {
            map.add(new ArrayList<>());
        }
        int id = 0;
        for(ArrayList<Room> floor : map){
            for(int i = 0; i < MAP_WIDTH; i++)
                floor.add(new Room(id++));

            System.out.println(floor);
        }

        adjacentRooms = new ArrayList<>();
        for(int i = 0; i < MAP_HEIGHT * MAP_WIDTH; i++) {
            adjacentRooms.add(new ArrayList<>());
        }


        int room1 = (int)(Math.random() * 7);
        int room2 = (int)(Math.random() * 7);
        while(room1 == room2) {
            room2 = (int) (Math.random() * 7);
        }
        System.out.println(room1 + " " + room2);
        makePath(map.getFirst().get(room1), 0);
        makePath(map.getFirst().get(room2), 0);
        for(int i = 0; i < 4; i++){
            int room = (int)(Math.random() * 7);
            makePath(map.getFirst().get(room), 0);
        }

        for(int i = 0; i < adjacentRooms.size(); i++){
            /*if(adjacentRooms.get(i).isEmpty())
                continue;*/
            //System.out.print("[" + i + "] : ");
            System.out.print(adjacentRooms.get(i).size() + " ");
            for(var room : adjacentRooms.get(i)){
                System.out.print(room + " ");
            }
            System.out.print('\n');
        }
    }

    private void makePath(Room room, int floor) {
        if(floor == MAP_HEIGHT - 1)
            return;
        int min = (col(room.getId()) > 0)?-1:0;
        int max = (col(room.getId()) < MAP_WIDTH - 1)?1:0;
        int idNextRoom = -1;

        int direction;
        while(idNextRoom == -1 || wouldCrossExistingPath(room.getId(), idNextRoom)){
            direction = (int)Math.floor(Math.random() * (max - min + 1) + min);
            idNextRoom = room.getId() + MAP_WIDTH + direction;
        }

        if(!adjacentRooms.get(room.getId()).contains(idNextRoom))
            adjacentRooms.get(room.getId()).add(idNextRoom);
        makePath(map.get(row(idNextRoom)).get(col(idNextRoom)), floor + 1);
    }

    private boolean wouldCrossExistingPath(int id, int idNextRoom) {
        int i = row(id);

        int j = col(id);
        int jleftNeighbour = -1;
        int jrightNeighbour = -1;
        int jNextRoom = col(idNextRoom);

        if(j > 0) jleftNeighbour = j - 1;
        if(j < MAP_WIDTH - 1) jrightNeighbour = j + 1;

        if(jleftNeighbour != -1 && jNextRoom < j){
            for(var neighbour : adjacentRooms.get(MAP_WIDTH * i + jleftNeighbour)) {
                if(col(neighbour) > jNextRoom)
                    return true;
            }
        }

        if(jrightNeighbour != -1 && jNextRoom > j){
            for(var neighbour : adjacentRooms.get(MAP_WIDTH * i + jrightNeighbour)) {
                if(col(neighbour) < jNextRoom)
                    return true;
            }
        }

        return false;
    }

    private int row(int id){
        return id / MAP_WIDTH;
    }

    private int col(int id) {
        return id % MAP_WIDTH;
    }
}
