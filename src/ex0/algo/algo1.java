package ex0.algo;
import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import java.util.*;

public class algo1 implements ElevatorAlgo{

    private int elevatorNum;
    private final Building _building;
    //Store the future stops of each elevator
    ArrayList<Double>[] calls;
    //constructor
    public algo1(Building b){
        _building = b;
        elevatorNum = _building.numberOfElevetors();
        calls =  (ArrayList<Double>[]) new ArrayList[elevatorNum];
        for (int i = 0; i <elevatorNum ; i++) {
            calls[i] = new ArrayList<Double>();
        }
    }
    @Override
    public Building getBuilding() {
        return _building;
    }
    @Override
    public String algoName() {
        return "algo1";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        //here we store the total waiting time added for all users if particular
        //elevator handle the given call.in the end of the function we will allocate
        //the elevator with the least delay time to take the call
        double delayFromCall[] = new double[elevatorNum];
        int dest = c.getDest();
        int src = c.getSrc();
        //we denote a destination floor (where people come off) with a 0.5 appended
        double destF = (dest > 0 ? (dest + 0.5) : (dest - 0.5));

        //we calculate the delay for each elevator
        for(int i=0; i <elevatorNum;i++){
            delayFromCall[i]=time(dest,src,i)[0];
        }
        //we find the elevator with the least delay time
        int min = 0;
        for (int i = 1; i < elevatorNum;i++) {
            if(delayFromCall[i] < delayFromCall[min]) {
                min = i;
            }
        }
        //insert the stops in the end for two cases if the array is empty or if we doesn't find a better place
        double[] arr = time(dest,src,min);
        int srcIndex = (int)arr[1];
        int destIndex = (int)arr[2];
        if(calls[min].isEmpty()||arr.length == 4){
            calls[min].add(src * 1.0);
            calls[min].add(destF);
            return min;
        }
        //if want to insert the dest and source floor in the middle of the stops log we do it here
        if (dest == (int)calls[min].get(destIndex).doubleValue())
            calls[min].remove(destIndex);
        calls[min].add(destIndex, destF);
        if( src != ((int)calls[min].get(srcIndex).doubleValue()))
            calls[min].add(srcIndex+1,src*1.0);

        return min;
    }
    @Override
    public void cmdElevator(int elev) {
        //remove multiple occurrence of stopping on the same source floor
        ArrayList<Double> temp = new ArrayList<>();
        for (int i = 0; i < calls[elev].size(); i++) {
            if(calls[elev].get(i) % 1 ==0) {
                if (temp.contains(calls[elev].get(i)))
                    calls[elev].remove(i);
                else
                    temp.add(calls[elev].get(i));
            }
        }
        //remove duplicate values - shouldn't happen, but left as a safeguard
        while(calls[elev].size() >= 2 )
            if((int)calls[elev].get(0).doubleValue() == (int)(calls[elev]).get(1).doubleValue())
                calls[elev].remove(0);
            else{
                break;
            }
        //delete a stop when we reached it, and command the elevator to go to the next stop
        Elevator e = _building.getElevetor(elev);
        if(e.getState() == Elevator.LEVEL &&  !calls[elev].isEmpty()){
            if (e.getPos() == (int)calls[elev].get(0).doubleValue()){
                calls[elev].remove(0);
                if(!calls[elev].isEmpty())
                    e.goTo((int)calls[elev].get(0).doubleValue());
            }
            else {
                e.goTo((int)calls[elev].get(0).doubleValue());
            }
        }
    }

    /** this function get source,destination and elevator index and find the ideal
     *position to insert those stops for the given elevator
     * @return double[] containing the best delay for the elevator along with the ideal indexes
     */
    double[] time(int dest,int src, int elev) {
        Elevator thisElev =_building.getElevetor(elev);
        double floorTime =thisElev.getStopTime()+thisElev.getStartTime()+thisElev.getTimeForOpen()+thisElev.getTimeForClose();
        int destIndex;
        int srcIndex ;
        ArrayList<Double> stops = calls[elev];
        ArrayList<Double> tempStops = new ArrayList<>();

        for (Double d: calls[elev])
            tempStops.add(d);
        //if the elevator is not busy the distance is simple to calculate
        if (calls[elev].isEmpty()) return new double[]{(Math.abs(thisElev.getPos()-src)+Math.abs(src -dest))/thisElev.getSpeed()+floorTime,-1,-1};
        //Elevators with ERROR will get the lowest priority possible
        if (thisElev.getState()==Elevator.ERROR) return new double[]{Double.MAX_VALUE,0,0};

        //we try to insert the dest and the source between the current stops of the elevator
        //in the outer loop we insert the dest, and if we succeed we try to insert the source before it
        for (int i = 0; i <stops.size() -1 ; i++) {
            //we pass the destination between stops i and i+1
            if ((dest > stops.get(i) && dest <= stops.get(i+1))
                    ||(dest < stops.get(i) && dest >= stops.get(i+1))){
                destIndex = i+1;
                for (int j = i+1; j >0 ; j--) {
                    if(j == i+1){
                        if ((src > dest && src <= stops.get(j-1))
                                || (src < dest && src >= stops.get(j-1))){
                            srcIndex = j-1;
                            return new double[]{delayCalculator(tempStops,elev,floorTime,src,dest,srcIndex,destIndex),srcIndex,destIndex};
                        }
                    }
                    else if ((src > stops.get(j) && src <= stops.get(j-1))
                            || (src < stops.get(j) && src >= stops.get(j-1))){
                        srcIndex = j-1;
                        return new double[]{delayCalculator(tempStops,elev,floorTime,src,dest,srcIndex,destIndex),srcIndex,destIndex};
                    }
                }
            }
        }
        //if the call isn't contained in the current route of the elevator we put it last
        ArrayList<Double> arr = new ArrayList<>();
        arr.addAll(stops);
        arr.add((double)src);
        arr.add(dest > 0 ? (dest + 0.5) : (dest - 0.5));
        //we return array sized 4 to note that the call was appended to the stops list
        return new double[] {timeCalculator(elev,arr,floorTime),0,0,0};
    }
    /**we get an elevator and it's stops, destination, source and indexes to insert them into
     * we calculate the delay caused to the elevator by inserting the source and dest on the given indexes
     * we call timeCalculator to help us with some of the calculations
     * @return the delay in seconds
     * */
    double delayCalculator(ArrayList<Double> stops,int elev,double floorTime,int src,int dest,int srcIndex,int destIndex){
        //for calculation purposes, we denote a destination floor with -+ 0.5 (- for negative floor)
        double destF = (dest > 0 ? (dest + 0.5) : (dest - 0.5));
        boolean addDest=false,addSrc=false;
        //first, we add the new destination and source the stops list
        //case 1: the new destination is the last stop
        if(destIndex == stops.size()) {
            //we check if the dest is the same as the last stop
            if ((int) stops.get(destIndex - 1).doubleValue() != dest)
                stops.add(destF);
            if ((int)stops.get(srcIndex).doubleValue() != src) {
                stops.add(++srcIndex, src * 1.0);
                addSrc=true;
                destIndex++;
            }
        }
        //case 2: the new dest is in the middle of the call log
        else {
            if((int)stops.get(destIndex).doubleValue() != dest)
                stops.add(destIndex,destF);
            addDest=true;

            if ((int)stops.get(srcIndex).doubleValue() != src){
                stops.add(++srcIndex,src * 1.0);
                destIndex++;
                addSrc=true;
            }
        }
        ArrayList<Double> clean = new ArrayList<>();
        for (int i = 0; i < stops.size(); i++) {
            if(stops.get(i) % 1 ==0) {
                if (clean.contains(stops.get(i)))
                    stops.remove(i);
                else
                    clean.add(stops.get(i));
            }
        }
        //second, we calculate how much time it take the the passenger to reach it's destination
        //by passing the list of stops up to his destination to timeCalculator
        List<Double> temp = new ArrayList<>();
        if(destIndex>=stops.size()-1){
            for (Double d: stops)
                temp.add(d);
        }
        else
            temp = stops.subList(0,destIndex);
        ArrayList<Double> temp2 = new ArrayList<>(temp);
        double callHandleTime = timeCalculator(elev,temp2,floorTime);
        //third, we check the delay caused for other users of the elevator
        int delayedStops = 0;
//        if(addDest&&addSrc&&stops.get(destIndex)%1!=0)delayedStops--;
        //count the number of passengers that didn't got off by the time
        //the elevator got to secIndex
        if(addDest&&addSrc)delayedStops--;
        for(int i=srcIndex+1 ; i< stops.size()&&addSrc; i++)
            if(stops.get(i) % 1 != 0)
                delayedStops++;

        for(int i=destIndex+1 ; i<stops.size()&&addDest; i++)
            if(stops.get(i) % 1 != 0)
                delayedStops++;
        //we return the total delay time
        return callHandleTime + delayedStops * floorTime ;
    }
    /** this function get an array of stops and calculate the time required for the elevator
     * to go from it's current position to it's last stop*/
    double timeCalculator(int elev, ArrayList<Double> stops,double floorTime){
        Elevator thisElev =_building.getElevetor(elev);
        double runTime = 0 ;
        //we add the elevator current position to the array
        stops.add(0,(double)thisElev.getPos());
        //we count the number of floors traveled, and add the delay from stopping at floors
        for (int i = 0; i < stops.size() - 1; i++) {
            if((int)stops.get(i).doubleValue() == (int)stops.get((i+1)).doubleValue())
                continue;
            double floors = Math.abs((int)stops.get(i+1).doubleValue() - (int)stops.get(i).doubleValue());
            runTime += floors/thisElev.getSpeed() + floorTime;
        }
        return runTime;
    }
}