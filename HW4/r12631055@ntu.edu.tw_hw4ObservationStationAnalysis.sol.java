import java.util.ArrayList;
import java.util.Arrays;
//import java.util.List;
import edu.princeton.cs.algs4.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.*;

class ObservationStationAnalysis {

    private ArrayList<Point2D> stations;

    public ObservationStationAnalysis(ArrayList<Point2D> stations) {
        // you can do something in Constructor or not
        this.stations = stations;
    }

        // Function to calculate orientation of three points (p, q, r)
    private int orientation(Point2D p, Point2D q, Point2D r) {
            double val = (q.y() - p.y()) * (r.x() - q.x()) - (q.x() - p.x()) * (r.y() - q.y());
            if (val == 0)
                return 0; // Collinear
            return (val > 0) ? 1 : 2; // Clockwise or Counterclockwise
    }
    
        // Function to sort points by polar angle with respect to p
    // private void sortByPolarAngle(Point2D[] points, Point2D p) {
    //     Arrays.sort(points, new Comparator<Point2D>() {
    //         @Override
    //         public int compare(Point2D q1, Point2D q2) {
    //             int orientationVal = orientation(p, q1, q2);
    //             if (orientationVal == 0) {
    //                 double dist1 = p.distanceSquaredTo(q1);
    //                 double dist2 = p.distanceSquaredTo(q2);
    //                 return Double.compare(dist1, dist2);
    //                 }
    //             return (orientationVal == 2) ? -1 : 1;
    //             }
    //         });
    // }
    
        // Function to find convex hull of a set of n points using Graham Scan
    private Point2D[] convexHull(Point2D[] points) {
            int n = points.length;
            if (n < 3)
                return null;
    
            ArrayList<Point2D> convexHull = new ArrayList<>();
            int startIndex = 0;
    
            // Find the point with lowest y-coordinate
            for (int i = 1; i < n; i++) {
                if (points[i].y() < points[startIndex].y()) {
                    startIndex = i;
                }
            }
    
            // Sort points by polar angle with respect to the lowest point
            //sortByPolarAngle(points, points[startIndex]);
            int p = startIndex, q;

            //convexHull.add(points[0]);
            //convexHull.add(points[1]);
            do
            { 
            // Add current point to result 
            convexHull.add(points[p]); 
   
            q = (p + 1) % n; 
               
            for (int i = 0; i < n; i++) 
            { 
               // If i is more counterclockwise than  
               // current q, then update q 
               if (orientation(points[p], points[i], points[q]) 
                                                   == 2) 
                   q = i; 
            } 
   
            p = q;   
            } while (p != startIndex);

            // Process remaining points
            // for (int i = 2; i < n; i++) {
            //     while (convexHull.size() >= 2 &&
            //             orientation(convexHull.get(convexHull.size() - 2), convexHull.get(convexHull.size() - 1), points[i]) != 2) {
            //         convexHull.remove(convexHull.size() - 1);
            //     }
            //     convexHull.add(points[i]);
            // }
    
        return convexHull.toArray(new Point2D[0]);
    }

    public Point2D[] findFarthestStations() {
        //Point2D[] farthest = new Point2D[]{new Point2D(0,0), new Point2D(1,1)}; // Example
        Point2D[] points = stations.toArray(new Point2D[0]);
        Point2D[] convexHull = convexHull(points);
        int n = convexHull.length;
        double maxvalue = 0.0;
        //int lowp = 0;
        int temp;
        int left = 0;
        int right = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((convexHull[i].x() - convexHull[j].x()) * (convexHull[i].x() - convexHull[j].x()) + (convexHull[i].y() - convexHull[j].y()) * (convexHull[i].y() - convexHull[j].y()) > maxvalue){
                    maxvalue = (convexHull[i].x() - convexHull[j].x()) * (convexHull[i].x() - convexHull[j].x()) + (convexHull[i].y() - convexHull[j].y()) * (convexHull[i].y() - convexHull[j].y());
                    left = i;
                    right = j;
                }
            }
        }
        // for (int i = 1; i < n; i++) {
        //     if (convexHull[i].x() > convexHull[lowp].x()) {
        //         lowp = i;
        //     }
        // }
        // double angle1 = Math.atan2(convexHull[left].y() - convexHull[lowp].y(), convexHull[left].x() - convexHull[lowp].x());
        // double angle2 = Math.atan2(convexHull[right].y() - convexHull[lowp].y(), convexHull[right].x() - convexHull[lowp].x());
        if (convexHull[left].x()*convexHull[left].x()+convexHull[left].y()*convexHull[left].y()  > convexHull[right].x()*convexHull[right].x()+convexHull[right].y()*convexHull[right].y()){
            temp = left;
            left = right;
            right = temp;
        }
        else if (convexHull[left].x()*convexHull[left].x()+convexHull[left].y()*convexHull[left].y() == convexHull[right].x()*convexHull[right].x()+convexHull[right].y()*convexHull[right].y()) {
            if(convexHull[left].y() > convexHull[right].y()){
                temp = left;
                left = right;
                right = temp;
            }
            
        }
            
        Point2D[] farthest = new Point2D[]{convexHull[left], convexHull[right]};
        // find the farthest two stations
        return farthest; // it should be sorted (ascendingly) by polar radius; please sort (ascendingly) by y coordinate if there are ties in polar radius.
    }

    public double coverageArea() {
        Point2D[] points = stations.toArray(new Point2D[0]);
        Point2D[] convexHull = convexHull(points);
        double area = 0.0;
        // calculate the area surrounded by the existing stations
        int n = convexHull.length;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            area += convexHull[i].x() * convexHull[j].y() - convexHull[i].y() * convexHull[j].x();
        }
        return Math.abs(area) / 2.0;
    }

    public void addNewStation(Point2D newStation) {
        stations.add(newStation);
    }
    
    public static void main(String[] args) throws Exception {

        ArrayList<Point2D> stationCoordinates = new ArrayList<>();
        stationCoordinates.add(new Point2D(0, 0));
        stationCoordinates.add(new Point2D(2, 0));
        stationCoordinates.add(new Point2D(3, 2));
        stationCoordinates.add(new Point2D(2, 6));
        stationCoordinates.add(new Point2D(0, 4));
        stationCoordinates.add(new Point2D(1, 1));
        stationCoordinates.add(new Point2D(2, 2));

        ObservationStationAnalysis Analysis = new ObservationStationAnalysis(stationCoordinates);
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
        
        System.out.println("Add Station (10, 3): ");
        Analysis.addNewStation(new Point2D(10, 3));
        
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
    }
}


class OutputFormat{
    ArrayList<Point2D> stations;
    ObservationStationAnalysis OSA;
    Point2D[] farthest;
    double area;
    Point2D[] farthestNew;
    double areaNew;
    ArrayList<Point2D> newStations;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}


// class test_ObservationStationAnalysis{
//     public static void main(String[] args)
//     {
//         Gson gson = new Gson();
//         int num_ac = 0;
//         int i = 1;

//         try {
//             // TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
//             TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
//             for (TestCase testCase : testCases) {
//                 System.out.println("Sample"+i+": ");
//                 i++;
//                 for (OutputFormat data : testCase.data) {
//                     ObservationStationAnalysis OSA = new ObservationStationAnalysis(data.stations);
//                     Point2D[] farthest;
//                     double area;
//                     Point2D[] farthestNew;
//                     double areaNew;

//                     farthest = OSA.findFarthestStations();
//                     area = OSA.coverageArea();


//                     if(data.newStations!=null){
//                         for(Point2D newStation: data.newStations){
//                             OSA.addNewStation(newStation);
//                         }
//                         farthestNew = OSA.findFarthestStations();
//                         areaNew = OSA.coverageArea();
//                     }else{
//                         farthestNew = farthest;
//                         areaNew = area;
//                     }

                    
//                     if(farthest[0].equals(data.farthest[0]) && farthest[1].equals(data.farthest[1]) &&  Math.abs(area-data.area) < 0.0001 
//                     && farthestNew[0].equals(data.farthestNew[0]) && farthestNew[1].equals(data.farthestNew[1]) && Math.abs(areaNew-data.areaNew) < 0.0001)
//                     {
//                         System.out.println("AC");
//                         num_ac++;
//                     }
//                     else
//                     {
//                         System.out.println("WA");
//                         System.out.println("Ans-farthest: " + Arrays.toString(data.farthest));
//                         System.out.println("Your-farthest:  " + Arrays.toString(farthest));
//                         System.out.println("Ans-area:  " + data.area);
//                         System.out.println("Your-area:  " + area);

//                         System.out.println("Ans-farthestNew: " + Arrays.toString(data.farthestNew));
//                         System.out.println("Your-farthestNew:  " + Arrays.toString(farthestNew));
//                         System.out.println("Ans-areaNew:  " + data.areaNew);
//                         System.out.println("Your-areaNew:  " + areaNew);
//                         System.out.println("");
//                     }
//                 }
//                 System.out.println("Score: "+num_ac+"/ 8");
//                 }
            
//         } catch (JsonSyntaxException e) {
//             e.printStackTrace();
//         } catch (JsonIOException e) {
//             e.printStackTrace();
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         }
        
//     }
// }
