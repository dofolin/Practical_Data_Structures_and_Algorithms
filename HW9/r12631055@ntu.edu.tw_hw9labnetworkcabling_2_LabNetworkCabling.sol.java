import java.util.*;

import edu.princeton.cs.algs4.UF;

import java.io.FileNotFoundException;
import java.io.FileReader;
import com.google.gson.*;

class OutputFormat{
    LabNetworkCabling l;
    Map<Integer, String> deviceTypes;
    List<int[]> links;

    int cablingCost;
    int serverToRouter;
    int mostPopularPrinter;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}



class LabNetworkCabling {
    private Map<Integer, String> deviceTypes;
    private List<int[]> links;
    private Map<Integer, List<int[]>> mst;
    private int totalCost;

    public LabNetworkCabling(Map<Integer, String> deviceTypes, List<int[]> links) {
        this.deviceTypes = deviceTypes;
        this.links = links;
        this.mst = new HashMap<>();
        this.totalCost = 0;
        createMST();
    }

    private void createMST() {
        int n = deviceTypes.size();
        boolean[] inMST = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            if (a[2] != b[2]) return a[2] - b[2];
            return Integer.compare(Math.min(a[0], a[1]), Math.min(b[0], b[1]));
        });

        // Ensure all nodes are included in the MST
        for (int start = 0; start < n; start++) {
            if (!inMST[start]) {
                inMST[start] = true;
                for (int[] link : links) {
                    if (link[0] == start || link[1] == start) {
                        pq.add(link);
                    }
                }

                while (!pq.isEmpty()) {
                    int[] edge = pq.poll();
                    int u = edge[0], v = edge[1], weight = edge[2];

                    if (inMST[u] && inMST[v]) continue;

                    int next = inMST[u] ? v : u;
                    inMST[next] = true;
                    totalCost += weight;

                    mst.putIfAbsent(u, new ArrayList<>());
                    mst.putIfAbsent(v, new ArrayList<>());
                    mst.get(u).add(new int[]{v, weight});
                    mst.get(v).add(new int[]{u, weight});

                    for (int[] link : links) {
                        if (link[0] == next || link[1] == next) {
                            pq.add(link);
                        }
                    }
                }
            }
        }
    }

    public int cablingCost() {
        return totalCost;
    }

    public int serverToRouter() {
        int serverIndex = -1, routerIndex = -1;
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            if (entry.getValue().equals("Server")) serverIndex = entry.getKey();
            if (entry.getValue().equals("Router")) routerIndex = entry.getKey();
        }
        return dfs(serverIndex, routerIndex, new boolean[deviceTypes.size()]);
    }

    private int dfs(int current, int target, boolean[] visited) {
        if (current == target) return 0;
        visited[current] = true;
        for (int[] neighbor : mst.get(current)) {
            int next = neighbor[0], weight = neighbor[1];
            if (!visited[next]) {
                int distance = dfs(next, target, visited);
                if (distance != -1) {
                    return distance + weight;
                }
            }
        }
        return -1;
    }

    public int mostPopularPrinter() {
        Map<Integer, Integer> printerUsage = new HashMap<>();
        for (int i : deviceTypes.keySet()) {
            if (deviceTypes.get(i).equals("Computer")) {
                int nearestPrinter = findNearestPrinter(i);
                //System.out.println("Computer " + i + " -> Nearest Printer: " + nearestPrinter);
                printerUsage.put(nearestPrinter, printerUsage.getOrDefault(nearestPrinter, 0) + 1);
            }
        }

        int maxUsage = -1, mostPopularPrinter = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : printerUsage.entrySet()) {
            //System.out.println("Printer " + entry.getKey() + " Usage: " + entry.getValue());
            if (entry.getValue() > maxUsage || (entry.getValue() == maxUsage && entry.getKey() < mostPopularPrinter)) {
                maxUsage = entry.getValue();
                mostPopularPrinter = entry.getKey();
            }
        }

        return mostPopularPrinter;
    }

    private int findNearestPrinter(int computer) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            if (a[1] != b[1]) return a[1] - b[1];
            return Integer.compare(a[0], b[0]);
        });
        boolean[] visited = new boolean[deviceTypes.size()];
        pq.add(new int[]{computer, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];
            int distance = current[1];

            if (visited[node]) continue;
            visited[node] = true;

            if (deviceTypes.get(node).equals("Printer")) {
                return node;
            }

            for (int[] neighbor : mst.get(node)) {
                if (!visited[neighbor[0]]) {
                    pq.add(new int[]{neighbor[0], distance + neighbor[1]});
                }
            }
        }
        return -1; // should never reach here if the input is valid
    }

    public static void main(String[] args) {
        Map<Integer, String> deviceTypes = Map.of(
            4, "Router",
            5, "Computer",
            0, "Printer",
            1, "Printer",
            2, "Printer",
            3, "Server"
        );

        List<int[]> links = List.of(
            new int[]{0, 5, 7},
            new int[]{1, 3, 5},
            new int[]{3, 5, 2},
            new int[]{4, 5, 3},
            new int[]{2, 4, 4}
        );

        LabNetworkCabling network = new LabNetworkCabling(deviceTypes, links);
        System.out.println("Total Cabling Cost: " + network.cablingCost());
        System.out.println("Distance between Server and Router: " + network.serverToRouter());
        System.out.println("Most Popular Printer: " + network.mostPopularPrinter());

        // Print MST structure
        // System.out.println("MST Structure:");
        // for (Map.Entry<Integer, List<int[]>> entry : network.mst.entrySet()) {
        //     System.out.print("Node " + entry.getKey() + " is connected to: ");
        //     for (int[] neighbor : entry.getValue()) {
        //         System.out.print(Arrays.toString(neighbor) + " ");
        //     }
        //     System.out.println();
        // }
    }
}



class test_LabNetworkCabling{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        int num_ac = 0;

        try {
            TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
            
            for(int i = 0; i<testCases.length;++i)
            {
                for (OutputFormat data : testCases[i].data) {

                    LabNetworkCabling LNC = new LabNetworkCabling(data.deviceTypes, data.links);
                    int ans_cc = data.cablingCost;
                    int ans_sr = data.serverToRouter;
                    int ans_mpp = data.mostPopularPrinter;
                    
                    int user_cc = LNC.cablingCost();
                    int user_sr = LNC.serverToRouter();
                    int user_mpp = LNC.mostPopularPrinter();
                    
                    if(user_cc == ans_cc && user_sr == ans_sr && user_mpp==ans_mpp)
                    {
                        System.out.println("AC");
                        num_ac++;
                    }
                    else
                    {
                        System.out.println("WA");
                        System.out.println("Input deviceTypes:\n" + data.deviceTypes);
                        System.out.println("Input links: ");
                        for (int[] link : data.links) {
                            System.out.print(Arrays.toString(link));
                        }

                        System.out.println("\nAns cablingCost: " + ans_cc );
                        System.out.println("Your cablingCost:  " + user_cc);
                        System.out.println("Ans serverToRouter:  " + ans_sr);
                        System.out.println("Your serverToRouter:  " + user_sr);
                        System.out.println("Ans mostPopularPrinter:  " + ans_mpp);
                        System.out.println("Your mostPopularPrinter:  " + user_mpp);
                        System.out.println("");
                    }
                }
            }
            System.out.println("Score: "+num_ac+"/10");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}