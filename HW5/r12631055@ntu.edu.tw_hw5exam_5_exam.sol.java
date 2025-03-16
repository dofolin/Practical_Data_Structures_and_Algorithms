//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;


//import com.google.gson.*;

// class OutputFormat{
//     Integer[][] scores;
//     List<int[]> answer;
// }

class Exam {
    static class Student {
        int id;
        int[] scores;

        int totalScore;

        public Student(int id, int[] scores) {

            this.id = id;
            this.scores = scores;

            this.totalScore = calculateTotalScore(scores);
        }

        private int calculateTotalScore(int[] scores) {
            int total = 0;
            for (int score : scores) {
                total += score;
            }
            return total;
        }
    }

    public static List<int[]> getPassedList(Integer[][] scores)
    {
        //input:
        //    scores: int[subject][id] 
        //    eg. scores[0][0] -> subject: 0, ID: 0
        //        scores[1][5] -> subject: 1, ID: 5

        int numSubjects = scores.length;
        int numStudents = scores[0].length;
        int twenty = (int) Math.ceil(numStudents * 0.2);



        List<Student> students = new ArrayList<>(numStudents);
        for (int id = 0; id < numStudents; id++) {
            int[] studentScores = new int[numSubjects];
            
            for (int subject = 0; subject < numSubjects; subject++) {
                studentScores[subject] = scores[subject][id];
            
            }
            
            students.add(new Student(id, studentScores));
        }


        List<Student> passedStudents = new ArrayList<>();
        

        quickSelect(students, 0, numStudents - 1, twenty, 0);


        for (int i = twenty; i < students.size(); i++) {
            if (students.get(i).scores[0] == students.get(twenty - 1).scores[0]) {
                twenty++;
            } else {
                break;
            }
        }

        for (Student student : students.subList(0, twenty)) {
            passedStudents.add(student);
        }
        twenty = (int) Math.ceil(numStudents * 0.2);

        

        
        List<Student> tempStudents = new ArrayList<>();
        

        for (int subject = 1; subject < numSubjects; subject++) {
            quickSelect(students, 0, numStudents - 1, twenty, subject);
            for (int i = twenty; i < students.size(); i++) {
                if (students.get(i).scores[subject] == students.get(twenty - 1).scores[subject]) {
                    twenty++;
                } else {
                    break;
                }
            }


            for (Student student : students.subList(0, twenty)) {    
                tempStudents.add(student);
            }



            passedStudents.retainAll(tempStudents);
            tempStudents.clear();
            twenty = (int) Math.ceil(numStudents * 0.2);
        }

        List<int[]> answer = new ArrayList<>(passedStudents.size());
        for (Student student : passedStudents) { 

            answer.add(new int[]{student.id, student.totalScore});
        }

        answer.sort(
            Comparator.<int[]>comparingInt(score -> score[1])
              .reversed()
              .thenComparingInt(score -> score[0])
        );

        int lenans = Math.min(twenty, passedStudents.size());



        return answer.subList(0, lenans);
    }

    public static void quickSelect(List<Student> students, int left, int right, int k, int subject) {
        if (left < right) {
            int pivotIndex = partition(students, left, right, subject);
            if (k < pivotIndex) {
                quickSelect(students, left, pivotIndex - 1, k, subject);
            } else if (k > pivotIndex) {
                quickSelect(students, pivotIndex + 1, right, k, subject);
            } else {
                return;
            }
        }
    }

    public static int partition(List<Student> students, int left, int right, int subject) {
        Student pivotStudent = students.get(right);
        int pivotScore = pivotStudent.scores[subject];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            int currentScore = students.get(j).scores[subject];
            if (currentScore >= pivotScore){ //|| (currentScore == pivotScore && currentID < pivotID)) {
                i++;
                Student temp = students.get(i);
                students.set(i, students.get(j));
                students.set(j, temp);
            }
        }
        Student temp = students.get(i + 1);
        students.set(i + 1, students.get(right));
        students.set(right, temp);
        return i + 1;

    }

    public static void main(String[] args) {
        List<int[]> ans = getPassedList(new Integer[][]
            {
                // ID:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                {67,82,98,32,65,76,87,12,43,75,25},
                {42,90,80,12,76,58,95,30,67,78,10}
            }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
            // 11 students * 0.2 = 2.2 -> Top 3 students 
            // Output -> [6, 182][2, 178][1, 172]
        
        System.out.println(); // For typesetting
        
        ans = getPassedList(new Integer[][]
            {
                // ID:[0, 1, 2, 3, 4, 5]
                {67,82,64,32,65,76},
                {42,90,80,12,76,58}
            }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
            // 6 students * 0.2 = 1.2 -> Top 2 students 
            // Output -> [1, 172]
    } 
}



// class test_Exam{
//     static boolean deepEquals(List<int[]> answer,List<int[]> answer2)
//     {
//         if(answer.size() != answer2.size())
//             return false;
//         for(int i = 0; i< answer.size(); ++i)
//         {
//             int[] a = answer.get(i);
//             int[] b = answer2.get(i);
//             if(!Arrays.equals(a, b))
//             {
//                 return false;
//             }
//         }
//         return true;
//     }
//     public static void main(String[] args)
//     {
//         Gson gson = new Gson();
//         OutputFormat[] datas;
//         int num_ac = 0;
//         List<int[]> user_ans;
//         OutputFormat data;

//         try {
//             datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
//             for(int i = 0; i<datas.length;++i)
//             {
//                 data = datas[i];
//                 user_ans = Exam.getPassedList(data.scores);
//                 System.out.print("Sample"+i+": ");

//                 if(deepEquals(user_ans, data.answer))
//                 {
//                     System.out.println("AC");
//                     num_ac++;
//                 }
//                 else
//                 {
//                     System.out.println("WA");
//                     System.out.println("Data:      " + Arrays.deepToString(data.scores));
//                     System.out.println("Test_ans:  " + Arrays.deepToString(data.answer.toArray()));
//                     System.out.println("User_ans:  " + Arrays.deepToString(user_ans.toArray()));
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