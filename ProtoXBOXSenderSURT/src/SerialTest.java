import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;

//Step 1:  Plug in arduino and Controller
//Setp 2:  Check that there are no currnet instances of java running via task manager.(CLOSE IF NEED BE)
//Setp 3:  Run program, (AND PRAY).`

//NOTE TO FUTURE SURT PROGRAMMERS:
//Hi. I'm Matt. This is the Java code that sends data from the Xbox controller to the Arduino Mega.
//I got this from somewhere on the Internet. If I can find a link it will be on the next line.
//Sorry for now.
//I'm not entirely sure how this works. I will comment it to the best of my ability. Note that not all of these comments are mine.
//From what I can tell, this gets joystick inputs, creates a GUI, and sends the inputs out through a COM port.
//It will print anything that it gets back from that port.
//Closing the GUI or pressing the red square stop button will stop the program.

public class SerialTest implements SerialPortEventListener {
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
//			"/dev/tty.usbserial-A9007UX1", // Mac OS X
//                        "/dev/ttyACM0", // Raspberry Pi
//			"/dev/ttyUSB0", // Linux
			
			"COM9",// Windows. These are the ports that worked for us. They might be different on your system.
			"COM3",// Check device manager or something to find yours.
			"COM4",
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private static OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				System.out.println(inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	public void sendByte(int[] sender) {
		for(int i = 0; i < sender.length; i++) {
			try {
				output.write(sender[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	 public static void main(String[] args) throws Exception {
			SerialTest main = new SerialTest();
			main.initialize();
			Thread t=new Thread() {
				public void run() {
					//the following line will keep this app alive for 10000 seconds,
					//waiting for events to occur and responding to them (printing incoming messages to console).
					try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
				}
			};
			t.start();
			System.out.println("Started");
			
			
			//JInputJoystickTest jinputJoystickTest = new JInputJoystickTest();
	        // Writes (into console) informations of all controllers that are found.
	        //jinputJoystickTest.getAllControllersInfo();
	        // In loop writes (into console) all joystick components and its current values.
	        //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.STICK);
	        //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.GAMEPAD);
	        
			//test to see if we can output
			//output.write(0);
			
			Thread.sleep(1500); //wait for serial communication to begin
			
	        Test sean = new Test();//I forgot why we called it sean and seanny. Too late to change it now.
	        while(true) {
	        	if(!sean.foundControllers.isEmpty()) {
	        		int[] seanny = sean.startShowingControllerData();
	        		output.write(250);
	        		for(int i = 0; i < seanny.length; i++) {
	        			// outputLables(i); //Gives lables to the outputs.
	        			// System.out.print(seanny[i] + "\t");
	        			output.write(seanny[i]); //output won't work unless there's an open com port
	        		}
	        		// System.out.println();
	        	}
		        else
		            sean.window.addControllerName("No controller found!");
	        }
		}

	private static void outputLables(int i) {
		if (i == 0) {
			System.out.print("X-a:");
		}
		else if (i == 1) {
			System.out.print("Y-a:");
		}
		else if (i == 2) {
			System.out.print("X-r:");
		}
		else if (i == 3) {
			System.out.print("Y-r:");
		}
		else if (i == 4) {
			System.out.print("Trigger:");
		}
		else if (i == 5) {
			System.out.print("A:");
		}
		else if (i == 6) {
			System.out.print("B:");
		}
		else if (i == 7) {
			System.out.print("X:");
		}
		else if (i == 8) {
			System.out.print("Y:");
		}
		else if (i == 9) {
			System.out.print("LB:");
		}
		else if (i == 10) {
			System.out.print("RB:");
		}
		else if (i == 11) {
			System.out.print("Select:");
		}
		else if (i == 12) {
			System.out.print("Start:");
		}
		else if (i == 13) {
			System.out.print("L-Hat:");
		}
		else if (i == 14) {
			System.out.print("R-Hat:");
		}
		else if (i == 15) {
			System.out.print("N/A:");
		}
		else if (i == 16) {
			System.out.print("D-Pad:");
		}
	}
}