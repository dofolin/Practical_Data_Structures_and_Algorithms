import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

class OutputFormat{
    int[][] map;
    int[] init_pos;
    int[] target_pos;
    int answer;
}



class RoadToCastle {
    private int[][] map;
    private int[] init_pos;
    private int[] target_pos;
    private int N;
    private int M;
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public RoadToCastle(int[][] map, int[] init_pos, int[] target_pos) {
        this.map = map;
        this.init_pos = init_pos;
        this.target_pos = target_pos;
        this.N = map.length;
        this.M = map[0].length;
    }

    public List<int[]> shortest_path() {
        List<int[]> result = new ArrayList<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
        boolean[][] visited = new boolean[N][M];
        
        Node start = new Node(init_pos[0], init_pos[1], 0, null);
        pq.offer(start);
        visited[init_pos[0]][init_pos[1]] = true;

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            if (current.x == target_pos[0] && current.y == target_pos[1]) {
                while (current != null) {
                    result.add(new int[]{current.x, current.y});
                    current = current.prev;
                }
                Collections.reverse(result);
                return result;
            }

            for (int[] dir : DIRECTIONS) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (isValid(newX, newY) && !visited[newX][newY]) {
                    int newCost = current.cost + moveCost(current.x, current.y, newX, newY);
                    Node neighbor = new Node(newX, newY, newCost, current);
                    pq.offer(neighbor);
                    visited[newX][newY] = true;
                }
            }
        }

        return result;
    }

    public int shortest_path_len() {
        List<int[]> path = shortest_path();
        if (path.isEmpty()) return 0;

        int totalCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalCost += moveCost(path.get(i)[0], path.get(i)[1], path.get(i + 1)[0], path.get(i + 1)[1]);
        }

        return totalCost;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < N && y >= 0 && y < M && map[x][y] != 0;
    }

    private int moveCost(int fromX, int fromY, int toX, int toY) {
        if (map[toX][toY] == 3) {
            return (map[fromX][fromY] == 2) ? 5 : 1;
        } else {
            return 1;
        }
    }

    private static class Node {
        int x, y, cost;
        Node prev;

        Node(int x, int y, int cost, Node prev) {
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.prev = prev;
        }
    }

    public static void main(String[] args) {
        RoadToCastle sol = new RoadToCastle(new int[][]{
                        {0, 0, 0, 0, 0},
                        {0, 2, 3, 2, 0},  //map[1][2]=3
                        {0, 2, 0, 2, 0},
                        {0, 2, 0, 2, 0},
                        {0, 2, 2, 2, 0},
                        {0, 0, 0, 0, 0}
                },
                new int[]{1, 1},
                new int[]{1, 3}
        );

        System.out.println(sol.shortest_path_len());
        List<int[]> path = sol.shortest_path();
        for (int[] coor : path)
            System.out.println("x: " + coor[0] + " y: " + coor[1]);

        //ans: best_path:{{1, 1}, {1, 2}, {1, 3}}
        //Path 1 (the best): [1, 1] [1, 2] [1, 3] -> 0+5+1 = 6, cost to reach init_pos is zero!
        //Path 2 (example of other paths): [1, 1] [2, 1] [3, 1] [4, 1] [4, 2] [4, 3] [3, 3] [2, 3] [1, 3] -> 8
    }
}



class test{
    static boolean are4Connected(int[] p1, int[] p2) {
        return (Math.abs(p1[0] - p2[0]) == 1 && p1[1] == p2[1]) || (Math.abs(p1[1] - p2[1]) == 1 && p1[0] == p2[0]);
    }
    static boolean isShortestPath(int[][] map, int path_len, List<int[]> path)
    {
        // check if the path is valid, (if the two node is actually neighbour, and if the path is not wall)
        int path_len2 = 0;
        for(int i = 1; i<path.size(); ++i){
            int[] pos_prev = path.get(i-1);
            int[] pos_now = path.get(i);
            int type = map[pos_now[0]][pos_now[1]];
            if(!are4Connected(pos_prev,pos_now) || type == 0) //type == 0 means that it is a wall.
                return false;
            path_len2 += (type == 3) ? 5 : 1;
        }
        return (path_len == path_len2);
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        OutputFormat data;
        int num_ac = 0;

        List<int[]> SHP;
        RoadToCastle sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new RoadToCastle(data.map, data.init_pos, data.target_pos);
                SHP = sol.shortest_path();
                
                System.out.print("Sample"+i+": ");
                if(sol.shortest_path_len() != data.answer)
                {
                    System.out.println("WA: incorrect path length");
                    System.out.println("Test_ans:  " + data.answer);
                    System.out.println("User_ans:  " + sol.shortest_path_len());
                    System.out.println("");
                }
                else if(!Arrays.equals(SHP.get(0),data.init_pos))
                {
                    System.out.println("WA: incorrect starting position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.init_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(0)));
                    System.out.println("");
                }
                else if(!Arrays.equals(SHP.get(SHP.size()-1),data.target_pos))
                {
                    System.out.println("WA: incorrect goal position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.target_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(SHP.size()-1)));
                    System.out.println("");
                }
                else if(!isShortestPath(data.map, data.answer,SHP))
                {
                    System.out.println("WA: Path Error, either not shortest Path or path not connected");
                    System.out.println("Map:      " + Arrays.deepToString(data.map));
                    System.out.println("User_Path:  " + Arrays.deepToString(SHP.toArray()));
                    System.out.println("Test_path_len:  " + data.answer);
                    System.out.println("User_path_len:  " + sol.shortest_path_len());
                    System.out.println("");
                    
                }
                else
                {
                    System.out.println("AC");
                    num_ac++;
                }
            }
            System.out.println("Score: "+num_ac+"/"+datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}