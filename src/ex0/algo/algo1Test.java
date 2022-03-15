package ex0.algo;
import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.simulator.Call_A;
import ex0.simulator.ElevetorCallList;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class algo1Test {


    Building _building1;
    Building _building9;
    Building _building1new;
    algo1 algoCase1;
    algo1 algoCase9;
    algo1 algoCase1new;
    //    CallForElevator Call_A;
    public algo1Test(){

        Simulator_A.initData(9,null);
        _building9 = Simulator_A.getBuilding();
        Simulator_A.initData(1,null);
        _building1 = Simulator_A.getBuilding();
        algoCase1 = new algo1(_building1);
        algoCase9 =new algo1(_building9);

    }

    @Test
    void allocateAnElevator() {
        //call 1 - expected e3
        CallForElevator c = new Call_A(0,-10 , 30);
        int elev = algoCase9.allocateAnElevator(c);
        //since all the elevators are idle we expecting it to be assigned to the fastest elevator - e3
        assertEquals(3,elev);
        //call 2 - expected e1
        CallForElevator c1  = new Call_A(0,-2 , 14);
        //even thought that elevator 3 is the fastest handling this call as well
        //will delay it's current passengers, so we expect e1 to handle it
        elev = algoCase9.allocateAnElevator(c1);
        assertEquals(1,elev);
        //call 3 - expected - e1
        CallForElevator c2  = new Call_A(0,5, 14);
        //e0 complete this call first because e1 and e3 already busy with other calls and if they stop
        //to take this call too they will delay the people that in the elevator
        elev = algoCase9.allocateAnElevator(c2);
        assertEquals(0,elev);
        CallForElevator c3  = new Call_A(0,90 , 0);
        //because this call distance is big e3(the fastest elevator)allocate to this call
        elev = algoCase9.allocateAnElevator(c3);
        assertEquals(3,elev);
    }
    @Test
    void cmdElevator() {

        // make sure that cmd delete a stop when the elevator reach it
        algoCase9.calls[3].add(-10.0);
        assertFalse(algoCase9.calls[3].isEmpty());
        algoCase9.cmdElevator(3);
        assertTrue(algoCase9.calls[3].isEmpty());

        algoCase1.calls[0].add(-2.0);
        assertFalse(algoCase1.calls[0].isEmpty());
        algoCase1.cmdElevator(0);
        assertTrue(algoCase1.calls[0].isEmpty());

        //make sure the elevator goes in the same direction as the next stop
        //case 1: elevator 1 static at -2 , gets gotTo(-2)
        algoCase1.getBuilding().getElevetor(0).goTo(-2);
        assertTrue(algoCase1.getBuilding().getElevetor(0).getState() == Elevator.LEVEL);
        //case 2: elevator 1 static at -2 goTo(10)
        algoCase1.getBuilding().getElevetor(0).goTo(10);
        assertTrue(algoCase1.getBuilding().getElevetor(0).getState() == Elevator.UP);
        //case 3: elevator 7 static at -10 goTo(97)
        algoCase9.getBuilding().getElevetor(7).goTo(97);
        assertTrue(algoCase9.getBuilding().getElevetor(7).getState() == Elevator.UP);

    }

    @Test
    void delayCalculator() {
        //delayCalc cumpute the total delay occurin
        //[3,8.5,5,10.5,14.5] 6->9 52.2+30=82.2
        double floorTimeE0 = 10;
        ArrayList<Double> stops0 = new ArrayList();
        stops0.add(3.0);stops0.add(8.5);stops0.add(5.0);stops0.add(10.5);stops0.add(14.5);
        double delay=algoCase9.delayCalculator(stops0,1,floorTimeE0,6,9,2,3);
        assertEquals(82.2,delay);

        //[4,10,15.5,8,20.5] 7->18=[4,7,10,15.5,8,18.5,20.5] 60.66+30=90.66
        stops0.clear();
        stops0.add(4.0);stops0.add(10.0);stops0.add(15.5);stops0.add(8.0);stops0.add(20.5);
        delay=algoCase9.delayCalculator(stops0,5,floorTimeE0,7,18,0,4);
        assertEquals(90.66666666666667,delay);

    }

    @Test
    void timeCalculator() {
        //this function should measure the time it takes for an elevator
        //to go from its current position to the end of it's route


        //e1 speed is 1 fps (floors per second)
        //the elev is in floor -2
        double floorTimeE0 = 10;
        ArrayList<Double> stops0 = new ArrayList();

        //case 1:  [1] 3 floors climb + 1 stop = 13
        stops0.add(1.0);
        double time0 = algoCase1.timeCalculator(0,stops0,floorTimeE0);
        assertEquals(13,time0);

        //case 2: [1,4,8] 10 floors climb + 3 stop = 40
        stops0.add(4.0);
        stops0.add(8.0);
        time0 = algoCase1.timeCalculator(0,stops0,floorTimeE0);
        assertEquals(40,time0);


        //case 3: [1,4,8,-2] 10 floor up + 10 down + 4 stops = 60
        stops0.add(-2.0);
        time0 = algoCase1.timeCalculator(0,stops0,floorTimeE0);
        assertEquals(60,time0);

        //switch building and elevator: building 9 ,elevator 1 (speed = 10), base floor = -10
        stops0.clear();
        stops0.add(12.0);
        stops0.add(15.0);
        stops0.add(-4.0);
        //case 1: [12,15,-4] 44 floors + 3 stops = 34.4
        time0 = algoCase9.timeCalculator(1,stops0,floorTimeE0);
        assertEquals(34.4,time0);

        //case 2: [12,15,-4,100] 148 floors + 4 stops =54.8
        stops0.add(100.0);
        time0 = algoCase9.timeCalculator(1,stops0,floorTimeE0);
        assertEquals(54.8,time0);

    }

    void time() {
        //[20,13.5,15,10.5,15]18->11==[20,18,13.5,15,11.5,10.5,15] 43.9+30=73.9
        double floorTimeE0 = 10;
        ArrayList<Double> stops0 = new ArrayList();
        stops0.add(20.0);stops0.add(13.5);stops0.add(15.0);stops0.add(10.5);stops0.add(15.0);
        algoCase9.calls[1]=stops0;
        double[] time=algoCase9.time(11,18,1);
        assertEquals(73.9,time[0]);
        assertEquals(0,time[1]);
        assertEquals(3,time[2]);

        //[14,-3.5,90,25,33.5,-9]45->-5==[14,-3.5,90,45,25,33.5,-5.5,-9] 51.75+60+30=141.75
        stops0.clear();
        stops0.add(14.0);stops0.add(-3.5);stops0.add(90.0);stops0.add(25.0);stops0.add(33.5);stops0.add(-9.5);
        algoCase9.calls[7]=stops0;
        time=algoCase9.time(-5,45,7);
        assertEquals(141.75,time[0]);
        assertEquals(2,time[1]);
        assertEquals(5,time[2]);
    }

}