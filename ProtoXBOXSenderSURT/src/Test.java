import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 *
 * Joystick Test with JInput
 *
 *
 * @author TheUzo007 
 *         http://theuzo007.wordpress.com
 *
 * Created 22 Oct 2013
 *
 */
public class Test {  

    final JFrameWindow window;
    public ArrayList<Controller> foundControllers;

    public Test() {
        window = new JFrameWindow();
        
        foundControllers = new ArrayList<>();
        searchForControllers();
        
        // If at least one controller was found we start showing controller data on window.
        
    }

    /**
     * Search (and save) for controllers of type Controller.Type.STICK,
     * Controller.Type.GAMEPAD, Controller.Type.WHEEL and Controller.Type.FINGERSTICK.
     */
    private void searchForControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i = 0; i < controllers.length; i++){
            Controller controller = controllers[i];
            
            if (
                    controller.getType() == Controller.Type.STICK || 
                    controller.getType() == Controller.Type.GAMEPAD || 
                    controller.getType() == Controller.Type.WHEEL ||
                    controller.getType() == Controller.Type.FINGERSTICK
               )
            {
                // Add new controller to the list of all controllers.
                foundControllers.add(controller);
                
                // Add new controller to the list on the window.
                window.addControllerName(controller.getName() + " - " + controller.getType().toString() + " type");
            }
        }
    }
    
    /**
     * Starts showing controller data on the window.
     */
    public int[] startShowingControllerData(){
            // Currently selected controller.
            int selectedControllerIndex = window.getSelectedControllerName();
            Controller controller = foundControllers.get(selectedControllerIndex);

            // Pull controller for current data, and break while loop if controller is disconnected.
            if( !controller.poll() ){
                window.showControllerDisconnected();
                return null;
            }
            
            // X axis and Y axis
            int xAxisPercentage = 0;
            int yAxisPercentage = 0;
            // JPanel for other axes.
            JPanel axesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 2));
            axesPanel.setBounds(0, 0, 200, 190);
            
            // JPanel for controller buttons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
            buttonsPanel.setBounds(6, 19, 246, 110);
                    
            // Go through all components of the controller.
            Component[] components = controller.getComponents();
            
            //System.out.println(components.length); // There are 16 components
            
            // Create array that will be sent, fill with 0 for now
            int[] toSend = new int[components.length];
            for(int i = 0; i < toSend.length; i++) {
            	toSend[i] = 0;
            }
            
            for(int i=0; i < components.length; i++)
            {
                Component component = components[i];
                Identifier componentIdentifier = component.getIdentifier();
                
                // Buttons
                //if(component.getName().contains("Button")){ // If the language is not english, this won't work.
                if(componentIdentifier.getName().matches("^[0-9]*$")){ // If the component identifier name contains only numbers, then this is a button.
                    // Is button pressed?
                    boolean isItPressed = true;
                    if(component.getPollData() == 0.0f)
                        isItPressed = false;
                    
                    // Button index
                    String buttonIndex;
                    buttonIndex = component.getIdentifier().toString();
                    //"Button 0" A, i = 5
                    //"Button 1" B, i = 6
                    //then X, Y, LB, RB, Back, Start, LeftStick, RightStick
                    
                    toSend[i] = (int) component.getPollData();
                    
                    // Create and add new button to panel.
                    JToggleButton aToggleButton = new JToggleButton(buttonIndex, isItPressed);
                    aToggleButton.setPreferredSize(new Dimension(48, 25));
                    aToggleButton.setEnabled(false);
                    buttonsPanel.add(aToggleButton);
                    
                    // We know that this component was button so we can skip to next component.
                    continue;
                }
                
                // "Hat Switch" i = 15
                //the way this works
                //hatSwitchPosition = 0.0 if there's no input
                //hatSwitchPosition = 1.0 if you go directly left
                //hatSwitchPosition = .125 if you go up left
                //hatSwitchPosition = .25 if you go straight up
                //...and so on.
                if(componentIdentifier == Component.Identifier.Axis.POV){
                    float hatSwitchPosition = component.getPollData();
                    window.setHatSwitch(hatSwitchPosition);
                    toSend[i] = (int) (hatSwitchPosition*8);
                    
                    // We know that this component was hat switch so we can skip to next component.
                    continue;
                }
                
                // Axes
                if(component.isAnalog()){
                    float axisValue = component.getPollData();
                    int axisValueInPercentage = getAxisValueInPercentage(axisValue);
                    
                    // "X Axis" i = 1
                    if(componentIdentifier == Component.Identifier.Axis.X){
                        xAxisPercentage = axisValueInPercentage;
                        toSend[i] = xAxisPercentage;
                        continue; // Go to next component.
                    }
                    // "Y Axis" i = 0
                    if(componentIdentifier == Component.Identifier.Axis.Y){
                        yAxisPercentage = axisValueInPercentage; 
                        toSend[i] = yAxisPercentage;
                        continue; // Go to next component.
                    }
                    
                    // Other axis
                    //"Y Rotation" i = 2
                    if(component.getName().equals("Y Rotation")) {
                    	toSend[i] = axisValueInPercentage;
                    }
                    //"X Rotation" i = 3
                    if(component.getName().equals("X Rotation")) {
                    	toSend[i] = axisValueInPercentage;
                    }
                    //"Z Axis" i = 4
                    if(component.getName().equals("Z Axis")) {
                    	toSend[i] = axisValueInPercentage;
                    }
                    JLabel progressBarLabel = new JLabel(component.getName());
                    JProgressBar progressBar = new JProgressBar(0, 100);
                    progressBar.setValue(axisValueInPercentage);
                    axesPanel.add(progressBarLabel);
                    axesPanel.add(progressBar);
                }
            }
            
            // Now that we go trough all controller components,
            // we add buttons panel to window,
            window.setControllerButtons(buttonsPanel);
            // set x and y axes,
            window.setXYAxis(xAxisPercentage, yAxisPercentage);
            // add other axes panel to window.
            window.addAxisPanel(axesPanel);
            
            //Let's see what's in toSend
//            for(int i = 0; i < toSend.length; i++) {
//            	System.out.print(i + ": " + toSend[i] + "\t");
//            }
//            System.out.println();
            
            // We have to give processor some rest.
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
			return toSend;
        
    }
    
    
    
    /**
     * Given value of axis in percentage.
     * Percentages increases from left/top to right/bottom.
     * If idle (in center) returns 50, if joystick axis is pushed to the left/top 
     * edge returns 0 and if it's pushed to the right/bottom returns 100.
     * 
     * @return value of axis in percentage.
     */
    public int getAxisValueInPercentage(float axisValue)
    {
        return (int)(((2 - (1 - axisValue)) * 100) / 2);
    }
}
