import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;

class OutputFormat{
    int[] answer;
    String func;
    int[] args;
}

class RoadStatus
{
    private int[] carsOnRoad;
    private int greenLightRoad;
    private int greenLightTimer;
    private int lastTimeProcessed;
    private boolean firstAddCarHandled;

    public RoadStatus() {
        this.carsOnRoad = new int[3]; 
        this.greenLightRoad = -1; 
        this.greenLightTimer = 0; 
        this.firstAddCarHandled = false;
        this.lastTimeProcessed = 0;

    }

    public int[] roadStatus(int time)
    {
        while (lastTimeProcessed < time) {
            if (greenLightTimer > 0 && greenLightRoad != -1) {
                carsOnRoad[greenLightRoad]--;
                greenLightTimer--;
            }
            if (greenLightTimer == 0) {
                chooseGreenLight();
            }
            lastTimeProcessed++;
        }
        return carsOnRoad.clone();
    }
    
    public void addCar(int time, int id, int num_of_cars)
    {
        roadStatus(time);
        carsOnRoad[id] += num_of_cars;
        // if (lastTimeProcessed < time) {
        //     roadStatus(time);
        // }
        if (greenLightRoad == -1 || greenLightTimer == 0) {
            chooseGreenLight();
        }
    }
    private void chooseGreenLight() {
        if (carsOnRoad[0]+carsOnRoad[1]+carsOnRoad[2]==0) {
            greenLightRoad = -1;
            greenLightTimer = 0;
        }
        int maxCars = -1;
        int chosenRoad = -1;
        for (int i = 0; i < carsOnRoad.length; i++) {
            if (carsOnRoad[i] > maxCars) {
                maxCars = carsOnRoad[i];
                chosenRoad = i;
            }
        }
        if (maxCars > 0) {
            greenLightRoad = chosenRoad;
            greenLightTimer = carsOnRoad[chosenRoad];
        } else {
            
            greenLightRoad = -1;
            greenLightTimer = 0;
        }
    }

    public static void main(String[] args)
    {
        // Example 1
        System.out.println("Example 1: ");
        RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        sol1.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        sol1.addCar(0, 1, 3);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
        sol1.addCar(2, 0, 4);
        for (int i = 2; i < 12; ++i)
            System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
        //______________________________________________________________________
        // Example 2
        RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 2: ");
        sol2.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        sol2.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
        sol2.addCar(2, 1, 2);
        for (int i = 2; i < 7; ++i)
            System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));
        //______________________________________________________________________
        // Example 3
        RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 3: ");
        sol3.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
        System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3))); 
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(4, 0, 2);
        for (int i = 4; i < 10; i++) {
            System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
        }
        // check below for full output explaination
        System.out.println("Example 4: ");
        RoadStatus sol4 = new RoadStatus();
        sol4.addCar(0, 0, 2);
        sol4.addCar(0, 0, 1);
        sol4.addCar(2, 1, 2);
        System.out.println("1: " + Arrays.toString(sol4.roadStatus(3)));
        // Example 5
        RoadStatus sol5 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 5: ");
        sol5.addCar(0, 0, 1);
        System.out.println("1: " + Arrays.toString(sol5.roadStatus(1)));
        sol5.addCar(3, 1, 1);
        sol5.addCar(3, 1, 1);
        sol5.addCar(4, 0, 2);
        System.out.println("1: " + Arrays.toString(sol5.roadStatus(4)));
    }
}

// class test{
//     static boolean run_and_check(OutputFormat[] data, RoadStatus roadStat)
//     {
//         for(OutputFormat cmd : data)
//         {
//             if(cmd.func.equals("addCar"))
//             {
//                 roadStat.addCar(cmd.args[0], cmd.args[1], cmd.args[2]);
//             }
//             else if(cmd.func.equals("roadStatus"))
//             {
//                 int[] arr = roadStat.roadStatus(cmd.args[0]);
//                 if(!Arrays.equals(arr,cmd.answer))
//                     return false;
//             }
//         }
//         return true;
//     }
//     public static void main(String[] args)
//     {
//         Gson gson = new Gson();
//         OutputFormat[][] datas;
//         OutputFormat[] data;
//         int num_ac = 0;

//         try {
//             datas = gson.fromJson(new FileReader(args[0]), OutputFormat[][].class);
//             for(int i = 0; i<datas.length;++i)
//             {
//                 data = datas[i];
                
//                 System.out.print("Sample"+i+": ");
//                 if(run_and_check(data, new RoadStatus()))
//                 {
//                     System.out.println("AC");
//                     num_ac++;
//                 }
//                 else
//                 {
//                     System.out.println("WA");
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