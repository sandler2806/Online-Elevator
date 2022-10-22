# **Smart Elevator Algorithm** 
**Literature Review:**
1)	https://paradigm.suss.edu.sg/the-smart-elevator-scheduling-algorithm-an-illustration-ofcomputational-intelligence/  
2)	https://github.com/jhlenes/ElevatorProject 
3)	https://www.geeksforgeeks.org/smart-elevator-pro-geek-cup 
4)	https://www.quora.com/What-algorithm-is-used-in-modern-day-elevators 
  
**Offline Algorithm:**

The algorithm read the calls by their chronological order. 
1. We look for an elevator who’s direction is the same as the call and it’s route contain the 
call’s 
Source to save time and not hurt the Elevators spread around the building. 
 
We will look for such elevator in the following methods, we will go trough the active methods 
And look for elevator that befits the following criterions  
- The elevator going in the same direction as the call 
-	It’s route contains the call source 
-	The caller will wait for the elevator and not the other way around 
2.	If we failed to find an elevator answering to the criterions of 1 we will look for the first free or soon to be free elevator that can be there on time with the least amount of “work”  
3.	If both 1 and 2 failed we will  send the first elevator that can reach the source floor after finishing it’s work 

In all the calculation we take into account that stopping a busy elevator delays it’s other passengers, so if the delay is to big we will prioritize other elevators. 
We will not choose option 1 over 2 or 3 if it will delay us by more than 20 seconds. 
The delay is calculated by the **number of elevator users times elevator floorTime** (usually 10 sec) 
 
 
  
 **Online Algorithm:**
If there is an idle elevator on the same floor it will take care of it, otherwise we will assign the call for each elevator in building while choosing the optimal location for it among it’s stops array, and calculate the delay caused by choosing this elevator to handle the call.  
The delay is calculated by adding two parameters 
1. the time it takes for the elevator to complete the call  
2. the delay caused from stopping ,to the current off and on board calls of the elevator 
 
After calculating this delay for every elevator in the building, we pick the elevator that handle the call with the lowest possible delay time. 
 
**This method proved to be quite useful as the algorithm managed to complete complicated cases such as 9,7,8 in the lowest time on class(example: 44.1 secs average per call in case 9)** 
 
 
 **Testing**
 
-	Making several calls, make sure that each one of them is assigned an elevator 
-	Make sure the elevator is assigned by the logic we composed 
-	Don’t let calls to be assigned to an elevator with error 
-	Check that new calls are inserted at the right location on a given elevator 
-	Make sure that Destination floor is always inserted after it’s source floor 
-	Check that after the assignment the elevator will execute the command 
-	Check that calls to the same floor are ignored 
 
 
**Best results for the 10 elementary cases:** 
 
 
 ![image](https://user-images.githubusercontent.com/91602396/158385559-c09ad22b-af2c-4adb-801b-bf9b5f44083f.png)

 
 
 **UML**
 
 ![image](https://user-images.githubusercontent.com/91602396/158385713-162daf85-07f8-4369-9f68-50f0f33d7b05.png)
