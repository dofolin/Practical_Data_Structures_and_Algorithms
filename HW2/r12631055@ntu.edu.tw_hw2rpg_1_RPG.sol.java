// public class App {
//     public static void main(String[] args) throws Exception {
//         System.out.println("Hello, World!");
//     }
// }
//import edu.princeton.cs.algs4.Stack;
// public class App {
// public static void main(String[] args) throws Exception {
// Stack<Integer> stack = new Stack<Integer>();
// stack.push(1);
// System.out.println(stack.peek());
// }
// }
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import com.google.gson.*;

class OutputFormat{
    int[] defence;
    int[] attack;
    int k;
    int answer;
}

// class test_RPG{
//     public static void main(String[] args)
//     {
//         Gson gson = new Gson();
//         OutputFormat[] datas;
//         int num_ac = 0;
//         int user_ans;
//         OutputFormat data;

//         try {
//             datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
//             for(int i = 0; i<datas.length;++i)
//             {
//                 data = datas[i];
//                 user_ans = new RPG(data.defence, data.attack).maxDamage(data.k);
//                 System.out.print("Sample"+i+": ");
//                 if(data.answer == user_ans)
//                 {
//                     System.out.println("AC");
//                     num_ac++;
//                 }
//                 else
//                 {
//                     System.out.println("WA");
//                     System.out.println("Data_atk:  " + Arrays.toString(data.attack));
//                     System.out.println("Data_dfc:  " + Arrays.toString(data.defence));
//                     System.out.println("Test_ans:  " + data.answer);
//                     System.out.println("User_ans:  " + user_ans);
//                     System.out.println("");
//                 }
//             }
//             System.out.println("Score: "+num_ac+"/"+datas.length);
//         } catch (JsonSyntaxException e) {
//             e.printStackTrace();
//         } catch (JsonIOException e) {
//             e.printStackTrace();
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         }
//     }
// }
// paste your own RPG class here :)
class RPG {
    public int[] def;
    public int[] atk;
    public int[] maxSum;
    public int k;
    public RPG(int[] defence, int[] attack){
        // Initialize some variables
        def =  defence;
        atk =  attack;

        k = defence.length;
        maxSum = new int[k];
        
        //dp[0] = Math.max(0, attack[0] - defence[0]);
    }    
    public int maxDamage(int n){ 
        // return the highest total damage after n rounds.

        maxSum[0] = atk[0] - def[0];
        maxSum[1] = Math.max(maxSum[0] + atk[1] - def[1], (2*atk[1]) - def[1]);
        for (int i = 2; i < n; i++) {
            maxSum[i] = Math.max(maxSum[i-1] + atk[i] - def[i], maxSum[i-2] + (2*atk[i]) - def[i]);

        }
        return maxSum[n-1];
    }
    public static void main(String[] args) {
        RPG sol = new RPG(new int []{5,4,1,7,98,2},new int []{200,200,200,200,200,200});
        System.out.println(sol.maxDamage(6));
        //1: boost, 2: attack, 3: boost, 4: attack, 5: boost, 6: attack
        //maxDamage: 1187
    } 
}
