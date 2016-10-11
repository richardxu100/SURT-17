import net.java.games.input.Controller;

public class Runner {
//RUN IT IN SERIAL TEST YOU DIMWITS
//WHY HAS THIS TAKEN US SO LONG TO FIGURE OUT
//AAAAAAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGHHHHHHHHHHHHHHH
    public static void main(String[] args) throws Exception {
//		SerialTest main = new SerialTest();
//		main.initialize();
//		Thread t=new Thread() {
//			public void run() {
//				//the following line will keep this app alive for 10000 seconds,
//				//waiting for events to occur and responding to them (printing incoming messages to console).
//				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
//			}
//		};
//		t.start();
		System.out.println("Started");
		
		
		//JInputJoystickTest jinputJoystickTest = new JInputJoystickTest();
        // Writes (into console) informations of all controllers that are found.
        //jinputJoystickTest.getAllControllersInfo();
        // In loop writes (into console) all joystick components and its current values.
       //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.STICK);
        //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.GAMEPAD);
        
        new Test();
	}
}
