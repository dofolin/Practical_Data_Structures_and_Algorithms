import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays; // Used to print the arrays


import com.google.gson.*;

// class test{
//     public static void main(String[] args){
//         Mafia sol = new Mafia();
//         Gson gson = new Gson();
//         try (FileReader reader = new FileReader(args[0])){
//             JsonArray all = gson.fromJson(reader, JsonArray.class);
//             for(JsonElement caseInList : all){
//                 JsonArray a = caseInList.getAsJsonArray();
//                 int q_cnt = 0, wa = 0,ac = 0;
//                 for (JsonElement o : a) {
//                     q_cnt++;
//                     JsonObject person = o.getAsJsonObject();
//                     JsonArray arg_lvl = person.getAsJsonArray("level");
//                     JsonArray arg_rng = person.getAsJsonArray("range");
//                     JsonArray arg_ans = person.getAsJsonArray("answer");
//                     int LVL[] = new int[arg_lvl.size()];
//                     int RNG[] = new int[arg_lvl.size()];
//                     int Answer[] = new int[arg_ans.size()];
//                     int Answer_W[] = new int[arg_ans.size()];
//                     for(int i=0;i<arg_ans.size();i++){
//                         Answer[i]=(arg_ans.get(i).getAsInt());
//                         if(i<arg_lvl.size()){
//                             LVL[i]=(arg_lvl.get(i).getAsInt());
//                             RNG[i]=(arg_rng.get(i).getAsInt());
//                         }
//                     }
//                     Answer_W = sol.result(LVL,RNG);
//                     for(int i=0;i<arg_ans.size();i++){
//                         if(Answer_W[i]==Answer[i]){
//                             if(i==arg_ans.size()-1){
//                                 System.out.println(q_cnt+": AC");
//                             }
//                         }else {
//                             wa++;
//                             System.out.println(q_cnt+": WA");
//                             break;
//                         }
//                     }

//                 }
//                 System.out.println("Score: "+(q_cnt-wa)+"/"+q_cnt);

//             }
//         }catch (FileNotFoundException e) {
//             e.printStackTrace();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }

class member{
    int Level;
    int Range;
    int Index;
    //int[] ab;
    member(int _level,int _range, int i){
        Level=_level;
        Range=_range;
        Index=i;
    }
}

class Mafia {
    public int[] ab;
    public int[] result(int[] levels, int[] ranges) {
          // Given the traits of each member and output 
          // the leftmost and rightmost index of member
          // can be attacked by each member.
          //int c = levels.length;
          int[] ab = new int[2*levels.length];
          ab[0] = 0;
          for (int i = 0; i < levels.length; i++) {
            int a = Math.max(0, i - ranges[i]);
            int b = Math.min(levels.length-1, i + ranges[i]);
            
            if(ranges[i] == 0){
                ab[(i*2)] = i;
                ab[(i*2)+1] = i;
            }

            for(int j = i-1; j >= a; j--){
                if(levels[j] >= levels[i]){
                    ab[(i*2)] = j+1;
                    break;
                } else if (levels[j] < levels[i] && j==a) {
                    ab[(i*2)] = j;
                }
            }

            for(int k = i+1; k <= b; k++){
                if(levels[k] >= levels[i]){
                    ab[(i*2)+1] = k-1;
                    break;
                } else if (levels[k] < levels[i]) {
                    ab[(i*2)+1] = k;
                }
            }

        }
        ab[(2*levels.length)-1] = levels.length-1;
        return ab; 
        // complete the code by returning an int[]
        // flatten the results since we only need an 1-dimentional array.
    }

    public static void main(String[] args) {
        Mafia sol = new Mafia();
        System.out.println(Arrays.toString(
            sol.result(new int[] {11, 13, 11, 7, 15},
                         new int[] { 1,  8,  1, 7,  2})));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}