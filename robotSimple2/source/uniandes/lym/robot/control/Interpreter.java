package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;



/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;   

	private HashMap<String, Integer> hash;

	public Interpreter()
	{
		hash = new HashMap<>();

	}


	/**
	 * Creates a new interpreter for a given world
	 * @param world 
	 */


	public Interpreter(RobotWorld mundo)
	{

		hash = new HashMap<>();
		this.world =  (RobotWorldDec) mundo;

	}


	/**
	 * sets a the world
	 * @param world 
	 */

	public void setWorld(RobotWorld m) 
	{
		world = (RobotWorldDec) m;

	}



	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  M:  moves forward
	 *  R:  turns right
	 *  
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 */

	public String process(String input) throws Error
	{   
		StringBuffer output=new StringBuffer("SYSTEM RESPONSE: -->\n");	
		input = input.replace(" ", "");
		if(input.startsWith("ROBOT_R"))
		{
			if(input.endsWith("END")){
				input = input.substring(0, input.length()-3);
			}
//			else throw new Error();
			if(input.contains("VARS"))
			{
				String[] vars = input.split("VARS");
				String[] names = vars[1].split(",");
				for (int i = 0; i < names.length; i++) {

					if(i == names.length-1){
						String[] begin = names[i].split("BEGIN");
						hash.put(begin[0], 0);
					}
					else{
						hash.put(names[i], 0);
					}
				}
				//System.err.println(hash.keySet());
			}
			if(input.contains("BEGIN"))
			{
			String[] begin= input.split("BEGIN");
			String[] instructions = begin[1].split(";");
			for (int i = 0; i < instructions.length; i++)
			{
				if(instructions[i].startsWith("assign") )
				{
					String asignar = instructions[i].substring(7);
					String[] aux = asignar.split("to:");
					//System.out.println(aux[1]);
					hash.replace(aux[1], Integer.valueOf(aux[0]));
					//System.out.println(hash.get(aux[1]));

				}
				else if (instructions[i].startsWith("move"))
				{
					if(instructions[i].contains("toThe"))
					{
						String instr = instructions[i].substring(5); 
						String[] aux = instr.split("toThe:"); 
						Integer n = hash.get(aux[0]); 
						Integer value = Integer.getInteger(aux[0]); 
						
						if(n == null){
							world.moveVertically(value);
						}
						else {
							world.moveVertically(n);
						}
						
						
					}
					else if(instructions[i].contains("inDir"))
					{

					}
					else
					{
						String num = instructions[i].substring(5);
						Integer valor = hash.get(num);
						if(valor == null)
						{
							world.moveForward(Integer.valueOf(num));
						}
						else 
							world.moveForward(valor);
						
					}
				}
				else if(instructions[i].startsWith("turn") )
				{
					String girar = instructions[i].substring(5);
					if(girar.equals("left"))
					{
						world.turnRight();
						world.turnRight();
						world.turnRight();
					}
					else if (girar.equals("around"))
					{
						world.turnRight();
						world.turnRight();
					}
					else
						world.turnRight();
				}
				else if(instructions[i].startsWith("face") )
				{
					String orientacion = instructions[i].substring(5);
					int or = world.getOrientacion();
					if (orientacion.equals("north"))
					{
						while (or!=0) {
							world.turnRight();
							or = world.getOrientacion();
						}
					}
					if (orientacion.equals("south"))
					{
						while(or!=1){
							world.turnRight();
							or = world.getOrientacion();
						}
					}
					if (orientacion.equals("east"))
					{
						while(or!=2){
							world.turnRight();
							or = world.getOrientacion();
						}
					}
					if (orientacion.equals("west"))
					{
						while(or!=3){
							world.turnRight();
							or = world.getOrientacion();
						}
					}
				}
				else if(instructions[i].startsWith("put") )
				{
					String num = instructions[i].substring(4);
					String[] BoC = num.split("of:");
					Integer valor = hash.get(BoC[0]);
					if(valor == null)
					{
						if(BoC[1].equals("Balloons"))
						{
							world.putBalloons(Integer.valueOf(BoC[0]));
						}
						else
							world.putChips(Integer.valueOf(BoC[0]));
					}
					else {
						if(BoC[1].equals("Balloons"))
						{
							world.putBalloons(valor);
						}
						else
							world.putChips(valor);
					}
						
				}
				else if(instructions[i].startsWith("pick") )
				{
					String num = instructions[i].substring(5);
					String[] BoC = num.split("of:");
					Integer valor = hash.get(BoC[0]);
					if(valor == null)
					{
						if(BoC[1].equals("Balloons"))
						{
							world.grabBalloons(Integer.valueOf(BoC[0]));
						}
						else
							world.pickChips(Integer.valueOf(BoC[0]));
					}
					else {
						if(BoC[1].equals("Balloons"))
						{
							world.grabBalloons(valor);
						}
						else
							world.pickChips(valor);
					}
				}
				else if(instructions[i].startsWith("skip") || instructions[i].startsWith("Skip") )
				{
					
				}
			}
			}

		}
		else{

			int i;
			int n;
			boolean ok = true;
			n= input.length();

			i  = 0;
			try	    {
				while (i < n &&  ok) {
					switch (input.charAt(i)) {
					case 'M': world.moveForward(1); output.append("move \n");break;
					case 'R': world.turnRight(); output.append("turnRignt \n");break;
					case 'C': world.putChips(1); output.append("putChip \n");break;
					case 'B': world.putBalloons(1); output.append("putBalloon \n");break;
					case  'c': world.pickChips(1); output.append("getChip \n");break;
					case  'b': world.grabBalloons(1); output.append("getBalloon \n");break;
					default: output.append(" Unrecognized command:  "+ input.charAt(i)); ok=false;
					}

					if (ok) {
						if  (i+1 == n)  { output.append("expected ';' ; found end of input; ");  ok = false ;}
						else if (input.charAt(i+1) == ';') 
						{
							i= i+2;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								System.err.format("IOException: %s%n", e);
							}

						}
						else {output.append(" Expecting ;  found: "+ input.charAt(i+1)); ok=false;
						}
					}
				}


			}


			catch (Error e ){
				output.append("Error!!!  "+e.getMessage());

			}

		}
		return output.toString();	
	}



}
