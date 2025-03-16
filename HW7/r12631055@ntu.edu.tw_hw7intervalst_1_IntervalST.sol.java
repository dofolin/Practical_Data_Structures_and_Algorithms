import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;

class OutputFormat{
    List<String> answer;
    String func;
    String[] args;
}

class IntervalST<Key extends Comparable<Key>, Value>{
    private Node root;

    private class Node {
        private Key lo, hi, max;
        private Value val;
        private int size;
        private Node left, right;
        
        public Node(Key lo, Key hi, Value val) {
            // initializes the node if required.
            this.lo = lo;
            this.hi = hi;
            this.max = hi;
            this.val = val;
            this.size = 1;
        }
    }

    public IntervalST()
    {
        // initializes the tree if required.
        this.root = null;
    }
    
    public void put(Key lo, Key hi, Value val)
    {
        // insert a new interval here.
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // val   : the value stored in the tree.
        root = put(root, lo, hi, val);
    }
    private Node put(Node x, Key lo, Key hi, Value val) {
        if (x == null) {
            return new Node(lo, hi, val);
        }
        int cmp = lo.compareTo(x.lo);
        if (cmp < 0) {
            x.left = put(x.left, lo, hi, val);
        } else if (cmp > 0) {
            x.right = put(x.right, lo, hi, val);
        } else {
            cmp = hi.compareTo(x.hi);
            if (cmp < 0) {
                x.left = put(x.left, lo, hi, val);
            } else if (cmp > 0) {
                x.right = put(x.right, lo, hi, val);
            } else {
                x.val = val;
            }
        }
        x.max = max(x.max, hi, max(max(x.left), max(x.right)));
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void delete(Key lo, Key hi)
    {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        root = delete(root, lo, hi);
    }
    private Node delete(Node x, Key lo, Key hi) {
        if (x == null) return null;
        int cmp = lo.compareTo(x.lo);
        if (cmp < 0) {
            x.left = delete(x.left, lo, hi);
        } else if (cmp > 0) {
            x.right = delete(x.right, lo, hi);
        } else {
            cmp = hi.compareTo(x.hi);
            if (cmp < 0) {
                x.left = delete(x.left, lo, hi);
            } else if (cmp > 0) {
                x.right = delete(x.right, lo, hi);
            } else {
                if (x.left == null) return x.right;
                if (x.right == null) return x.left;
                Node t = x;
                x = min(t.right);
                x.right = deleteMin(t.right);
                x.left = t.left;
            }
        }
        x.max = max(x.lo, x.hi, max(max(x.left), max(x.right)));
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }
    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        x.max = max(x.lo, x.hi, max(max(x.left), max(x.right)));
        return x;
    }

    public List<Value> intersects(Key lo, Key hi)
    {
        // return the values of all intervals within the tree which intersect with [lo,hi].
        List<Value> result = new ArrayList<>();
        intersects(root, lo, hi, result);
        return result;

    }
    private void intersects(Node x, Key lo, Key hi, List<Value> result) {
        if (x == null) return;
        if (intersects(lo, hi, x.lo, x.hi)) result.add(x.val);
        if (x.left != null && x.left.max.compareTo(lo) >= 0) {
            intersects(x.left, lo, hi, result);
        }
        intersects(x.right, lo, hi, result);
    }

    private boolean intersects(Key lo1, Key hi1, Key lo2, Key hi2) {
        return lo1.compareTo(hi2) <= 0 && lo2.compareTo(hi1) <= 0;
    }

    private int size(Node x) {
        return (x == null) ? 0 : x.size;
    }
    private Key max(Key a, Key b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) > 0 ? a : b;
    }
    private Key max(Key a, Key b, Key c) {
        Key max = a;
        if (b != null && b.compareTo(max) > 0) max = b;
        if (c != null && c.compareTo(max) > 0) max = c;
        return max;
    }

    private Key max(Node x) {
        return (x == null) ? null : x.max;
    }
    public static void main(String[]args)
    {
        // Example
        IntervalST<Integer, String> IST = new IntervalST<>();
        IST.put(2,5,"badminton");
        IST.put(1,5,"PDSA HW7");
        IST.put(3,5,"Lunch");
        IST.put(3,6,"Workout");
        IST.put(3,7,"Do nothing");
        IST.delete(2,5); // delete "badminton"
        System.out.println(IST.intersects(1,2));
        
        IST.put(8,8,"Dinner");
        System.out.println(IST.intersects(6,10));
        
        IST.put(3,7,"Do something"); // If an interval is identical to an existing node, then the value of that node is updated accordingly
        System.out.println(IST.intersects(7,7));
        
        IST.delete(3,7); // delete "Do something"
        System.out.println(IST.intersects(7,7));
    }
}

class test{
    static boolean deepEquals(List<String> a, List<String> b)
    {
        return Arrays.deepEquals(a.toArray(), b.toArray());
    }
    static boolean run_and_check(OutputFormat[] data, IntervalST <Integer,String> IST)
    {
        for(OutputFormat cmd : data)
        {
            if(cmd.func.equals("intersects"))
            {
                int lo = Integer.parseInt(cmd.args[0]);
                int hi = Integer.parseInt(cmd.args[1]);
                
                List<String> student_answer = IST.intersects(lo, hi);
                Collections.sort(cmd.answer);
                Collections.sort(student_answer);
                if(!deepEquals(student_answer, cmd.answer))
                {
                    return false;
                }
            }
            else if(cmd.func.equals("put"))
            {
                IST.put(Integer.parseInt(cmd.args[0]), Integer.parseInt(cmd.args[1]), cmd.args[2]);
            }
            else if(cmd.func.equals("delete"))
            {
                IST.delete(Integer.parseInt(cmd.args[0]), Integer.parseInt(cmd.args[1]));
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[][] datas;
        OutputFormat[] data;
        int num_ac = 0;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[][].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                
                System.out.print("Sample"+i+": ");
                if(run_and_check(data, new IntervalST<>()))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("");
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