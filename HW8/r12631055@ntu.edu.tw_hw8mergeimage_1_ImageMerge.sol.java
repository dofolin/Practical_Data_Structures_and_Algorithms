import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

class OutputFormat2{
    double[][] box;
    double iou;
    double[][] answer;
}


class ImageMerge {
    private double[][] boundingBoxes;
    private double iouThreshold;
    private UnionFind uf;

    public ImageMerge(double[][] bbs, double iou_thresh) {
        this.boundingBoxes = bbs;
        this.iouThreshold = iou_thresh;
        this.uf = new UnionFind(bbs.length);
        findAndUnionOverlaps();
    }

    private void findAndUnionOverlaps() {
        // for (int i = 0; i < boundingBoxes.length; i++) {
        //     for (int j = i + 1; j < boundingBoxes.length; j++) {
        //         if (calculateIoU(boundingBoxes[i], boundingBoxes[j]) >= iouThreshold) {
        //             uf.union(i, j);
        //         }
        //     }
        // }
        RecursiveAction task = new RecursiveAction() {
        @Override
        protected void compute() {
            // 使用Java 8 并行流进行并行计算
            ForkJoinPool.commonPool().submit(() -> 
                java.util.stream.IntStream.range(0, boundingBoxes.length).parallel().forEach(i -> {
                    for (int j = i + 1; j < boundingBoxes.length; j++) {
                        if (calculateIoU(boundingBoxes[i], boundingBoxes[j]) >= iouThreshold) {
                            uf.union(i, j);
                        }
                    }
                })
            ).join();
        }
    };
    task.invoke();
    }

    private double calculateIoU(double[] box1, double[] box2) {
        double x1 = Math.max(box1[0], box2[0]);
        double y1 = Math.max(box1[1], box2[1]);
        double x2 = Math.min(box1[0] + box1[2], box2[0] + box2[2]);
        double y2 = Math.min(box1[1] + box1[3], box2[1] + box2[3]);

        double interArea = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        double box1Area = box1[2] * box1[3];
        double box2Area = box2[2] * box2[3];
        double unionArea = box1Area + box2Area - interArea;

        return interArea / unionArea;
    }

    public double[][] mergeBox() {
        List<List<double[]>> groups = new ArrayList<>();
        for (int i = 0; i < boundingBoxes.length; i++) {
            int root = uf.find(i);
            while (groups.size() <= root) {
                groups.add(new ArrayList<>());
            }
            groups.get(root).add(boundingBoxes[i]);
        }

        List<double[]> mergedBoxes = new ArrayList<>();
        for (List<double[]> group : groups) {
            if (!group.isEmpty()) {
                mergedBoxes.add(mergeGroup(group));
            }
        }

        // Sort the merged boxes by the given criteria
        Collections.sort(mergedBoxes, new Comparator<double[]>() {
            public int compare(double[] b1, double[] b2) {
                if (b1[0] != b2[0]) return Double.compare(b1[0], b2[0]);
                if (b1[1] != b2[1]) return Double.compare(b1[1], b2[1]);
                if (b1[2] != b2[2]) return Double.compare(b1[2], b2[2]);
                return Double.compare(b1[3], b2[3]);
            }
        });

        return mergedBoxes.toArray(new double[0][]);
    }

    private double[] mergeGroup(List<double[]> group) {
        double minX = Double.MAX_VALUE, maxX = 0, minY = Double.MAX_VALUE, maxY = 0;
        for (double[] rect : group) {
            minX = Math.min(minX, rect[0]);
            minY = Math.min(minY, rect[1]);
            maxX = Math.max(maxX, rect[0] + rect[2]);
            maxY = Math.max(maxY, rect[1] + rect[3]);
        }
        return new double[]{minX, minY, maxX - minX, maxY - minY};
    }

    static class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int p) {
            if (parent[p] != p) {
                parent[p] = find(parent[p]);
            }
            return parent[p];
        }

        public void union(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            if (rootP != rootQ) {
                if (rank[rootP] > rank[rootQ]) {
                    parent[rootQ] = rootP;
                } else if (rank[rootP] < rank[rootQ]) {
                    parent[rootP] = rootQ;
                } else {
                    parent[rootQ] = rootP;
                    rank[rootP]++;
                }
            }
        }
    }

    public static void main(String[] args) {
        ImageMerge sol = new ImageMerge(
                new double[][]{
                        {0.02,0.01,0.1,0.05},{0.0,0.0,0.1,0.05},{0.04,0.02,0.1,0.05},{0.06,0.03,0.1,0.05},{0.08,0.04,0.1,0.05},
                        {0.24,0.01,0.1,0.05},{0.20,0.0,0.1,0.05},{0.28,0.02,0.1,0.05},{0.32,0.03,0.1,0.05},{0.36,0.04,0.1,0.05},
                },
                0.5
        );
        double[][] temp = sol.mergeBox();
        System.out.println("Merged Boxes:");
        for (double[] box : temp) {
            System.out.println("Box: [" + box[0] + ", " + box[1] + ", " + box[2] + ", " + box[3] + "]");
        }
    }
}



class test{
    private static boolean deepEquals(double[][] test_ans, double[][] user_ans)
    {
        if(test_ans.length != user_ans.length)
            return false;
        for(int i = 0; i < user_ans.length; ++i)
        {
            if(user_ans[i].length != test_ans[i].length)
                return false;
            for(int j = 0; j < user_ans[i].length; ++j)
            {
                if(Math.abs(test_ans[i][j]-user_ans[i][j]) > 0.00000000001)
                    return false;
            }
        }
        return true;
    }
    public static void draw(double[][] user, double[][] test)
    {
        StdDraw.setCanvasSize(960,540);
        for(double[] box : user)
        {
            StdDraw.setPenColor(StdDraw.BLACK);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
        for(double[] box : test)
        {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }
    public static void main(String[] args) throws InterruptedException
    {
        Gson gson = new Gson();
        OutputFormat2[] datas;
        OutputFormat2 data;
        int num_ac = 0;

        double[][] user_ans;
        ImageMerge sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat2[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new ImageMerge(data.box, data.iou);
                user_ans = sol.mergeBox();
                System.out.print("Sample"+i+": ");
                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + "\n    iou: "+data.iou + "\n" +
                            "    box: "+Arrays.deepToString(data.box));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans));
                    System.out.println("");
                    draw(user_ans,data.answer);
                    Thread.sleep(5000);
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