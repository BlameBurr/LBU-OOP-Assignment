package uk.ac.leedsbeckett.oop.c3600712.assignment;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.Math;
import java.lang.reflect.Field;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import uk.ac.leedsbeckett.oop.LBUGraphics;

public class GraphicsSystem extends LBUGraphics {
	private static final long serialVersionUID = 1L;
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int commandIndex = 0;
	
	public GraphicsSystem() {
		JTextField textComponent = (JTextField) this.getComponent(0);
		
		textComponent.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (commandIndex < commandHistory.size()) commandIndex += 1;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (commandIndex > -1) commandIndex -= 1;
					if (commandIndex == -1) textComponent.setText("");
				} else commandIndex = 0;
				
				if (commandIndex != 0) {
					String command = commandIndex != -1 ? commandHistory.get(commandHistory.size()-commandIndex) : "";
					textComponent.setText(command);
				}
			}
		});
	}
	
	public void processCommand(String input) {
		String[] inputArray = input.split(" ");
		if (inputArray.length == 0) return;
		String command = inputArray[0].toLowerCase();
		String[] args = Arrays.copyOfRange(inputArray, 1, inputArray.length);
		
		try {
			if (command.equals("clear")) clear(args);
			else if (command.equals("reset")) reset(args);
			else if (command.equals("about")) about(args);
			else if (command.equals("penup")) penUp(args);
			else if (command.equals("pendown")) penDown(args);
			else if (command.equals("forward") || command.equals("backward")) forward(command, args);
			else if (command.equals("turnleft") || command.equals("turnright")) turnLeft(command, args);
			else if (command.equals("save")) save(args);
			else if (command.equals("load")) load(args);
			else if (command.equals("circle")) circle(args);
			else if (command.equals("triangle")) triangle(args);
			else if (command.equals("setpencolour")) input = setPenColour(command, args); // Done so that the command can be logged when colour chooser used
			else if (Arrays.asList("black", "white", "red", "green").contains(command)) colourCommand(command, args);
			else if (command.equals("fill")) fill(args);
			else if (command.equals("history")) history(args);
			else if (command.equals("clearhistory")) clearHistory(args);
			else if (command.equals("pattern")) drawPattern(args);
			
			else throw new UserException("Invalid command.");
			
			if (!Arrays.asList("load", "save", "clearhistory").contains(input.split(" ")[0].toLowerCase())) commandHistory.add(input);
		} catch(UserException e) {
			showDialog(e.getMessage(), "Error", true);
		}
	}
	
	public void clearHistory(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: clearhistory");
		commandHistory.clear();
	}
	
	public void history(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: clearhistory");
		String message = "";
		
		if (commandHistory.size() == 0) message = "The history is empty at the moment. Try again after entering some commands.";
		for (String command : commandHistory) {
			message += (command+"\n");
		}
		showDialog(message, "History", false);
	}
	
	public void drawPattern(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: pattern");
		Point start = new Point(getxPos(), getyPos());
		for (int i=0; i < 180; i++) {
			forward(100);
			turnRight(30);
			forward(20);
			turnLeft(60);
			forward(50);
			turnRight(30);
			setxPos(start.x);
			setyPos(start.y);
			turnRight(6);
		}
	}
	
	public void clear(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: clear");
		if (commandHistory.size() > 0 && !lastCommandEquals("save")) {
			int result = JOptionPane.showConfirmDialog(null, "Changes have been made without saving and will be lost. Would you like to continue?", "Are you sure", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION) return;
		}
		clear();
	}
	
	public void reset(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: reset");
		reset();
	}
	
	public void about(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: about");
		about();
		Graphics2D gfxInstance = getGraphics2DContext();
		gfxInstance.drawString("James Walker", 25, 25);
	}
	
	public void penUp(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: penup");
		penUp();
	}
	
	public void penDown(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: pendown");
		penDown();
	}
	
	public void forward(String command, String[] args) throws UserException {
		if (args.length != 1) throw new UserException("Invalid Usage. Correct Usage: "+command+" *number of pixels*");
		try {
			int multiplier = command.equals("forward") ? 1 : -1;
			int direction = getDirection() + (multiplier == -1 ? 180 : 0);
			int hypotenuse = Integer.parseInt(args[0]);
			
			double x_delta = (double)Math.round(hypotenuse*Math.cos(Math.toRadians(direction))*100d)/100d;
			double y_delta = (double)Math.round(hypotenuse*Math.sin(Math.toRadians(direction))*100d)/100d;
			double x = (getxPos() + x_delta);
			double y = (getyPos() + y_delta);

			if ((getSize().width - x < 0 || x < 0) || (getSize().height - y < 0 || y < 0))
				throw new UserException("Invalid Argument. The number of pixels should not exceed the size of the canvas.");
			else forward(hypotenuse*multiplier);
		} catch (NumberFormatException e) {
			throw new UserException("Invalid Argument. The number of pixels should be an integer.");
		}
	}
	
	public void turnLeft(String command, String[] args) throws UserException {
		if (args.length != 1) throw new UserException("Invalid Usage. Correct Usage: "+command+" *number of degrees*");
		try {
			int multiplier = command.equals("turnleft") ? 1 : -1;
			turnLeft(Integer.parseInt(args[0])*multiplier);
		} catch (NumberFormatException e) {
			throw new UserException("Invalid Argument. The number of degrees should be an integer.");
		}
	} // There's no reason to polymorph multiple functions when one can do both
	
	public void save(String[] args) throws UserException {
		if (args.length != 1) throw new UserException("Invalid Usage. Correct Usage: save *filename*");
		try {
			String filename = args[0];
			File imgFile = new File(filename+".jpg");
			ImageIO.write(getBufferedImage(), "jpg", imgFile);
			File txtFile = new File(filename+".txt");
			txtFile.createNewFile();
			FileWriter writer = new FileWriter(filename+".txt");
			commandHistory.forEach(cmd -> {
				try {
					writer.write((cmd+"\n").toCharArray());
				} catch (IOException e) {
					showDialog("Error: "+e.getMessage(), "Error", true);
				}
			});
			writer.close();
		} catch (IOException e) {
			showDialog("Error: "+e.getMessage(), "Error", true);
		}
	}
	
	public void load(String[] args) throws UserException {
		if (args.length != 1) throw new UserException("Invalid Usage. Correct Usage: load *.jpg/.txt file*");
		if (commandHistory.size() > 0 && !lastCommandEquals("save")) {
			int result = JOptionPane.showConfirmDialog(null, "Changes have been made without saving and will be lost. Would you like to continue?", "Are you sure", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION) return;
		}
		try {
			String filename = args[0];
			String filetype = filename.substring(filename.lastIndexOf(".")+1);
			File loadedFile = new File(filename);
			if (loadedFile.exists() && loadedFile.canRead()) {
				if (filetype.equals("jpg")) {
					BufferedImage buffer = ImageIO.read(loadedFile);
					setBufferedImage(buffer);
				} else if (filetype.equals("txt")) {
					reset();
					Scanner reader = new Scanner(loadedFile);
					while (reader.hasNextLine()) {
						String input = reader.nextLine();
						if (!Arrays.asList("load", "save").contains(input.split(" ")[0].toLowerCase())) processCommand(input);
					}
					reader.close();
				} else throw new UserException("Invalid Argument. The filename should be a .txt or .jpg file.");
			}
			commandHistory.remove(commandHistory.size() - 1);
		} catch(IOException e) {
			showDialog("Error: "+e.getMessage(), "Error", true);
		}
	}
	
	public void circle(String[] args) throws UserException {
		if (args.length != 1) throw new UserException("Invalid Usage. Correct Usage: circle *size of radius*");
		try {
			double radius = Double.parseDouble(args[0]);
			Graphics2D gfxContext = getGraphics2DContext();
			gfxContext.setPaint(getPenColour());
			gfxContext.rotate(Math.toRadians(getDirection()), getxPos(), getyPos());
			gfxContext.draw(new Ellipse2D.Double(getxPos()-radius, getyPos(), 2*radius, 2*radius));
			gfxContext.rotate(Math.toRadians(-getDirection()), getxPos(), getyPos());
		} catch (NumberFormatException e) {
			throw new UserException("Invalid Argument. The size of the radius should be numerical.");
		}
	}
	
	public void triangle(String[] args) throws UserException {
		if (args.length != 1 && args.length != 3) throw new UserException("Invalid Usage. Correct Usage: triangle *size of sides or each side separated by a \" \"*");
		try {
			int x = getxPos(), y = getyPos();
			int[] x_coords, y_coords;
			double[] sides = new double[3];
			if (args.length == 1) Arrays.fill(sides, Integer.parseInt(args[0]));
			else sides = new double[]{Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])};
			Arrays.sort(sides); //Base will always be longest and therefore sides[0]
			if (sides[1] + sides [2]<= sides[0] && sides[2] + sides[0] <= sides[1] && sides[1] + sides[0] <= sides[2])
				throw new UserException("Invalid Argument. These sides cannot form a triangle, try different lengths");
			
			double angle = Math.acos((Math.pow(sides[0], 2)+Math.pow(sides[1], 2)-Math.pow(sides[2], 2))/(2*sides[0]*sides[1]));
			x_coords = new int[] {(int) Math.round(x+Math.cos(angle)*sides[1]), (int) Math.round(x+sides[0]), x};
			y_coords = new int[] {(int) (Math.round(y+Math.sin(angle)*sides[1])), y, y};
			
			Graphics2D gfxContext = getGraphics2DContext();
			gfxContext.setPaint(getPenColour());
			gfxContext.rotate(Math.toRadians(getDirection()), getxPos(), getyPos());
			gfxContext.drawPolygon(x_coords, y_coords, 3);
			gfxContext.rotate(Math.toRadians((-getDirection())), getxPos(), getyPos());
		} catch (NumberFormatException e) {
			throw new UserException("Invalid Argument. The size of the side(s) should be numerical");
		}
	}
	
	public String setPenColour(String command, String[] args) throws UserException {
		if (args.length > 1 && args.length != 3) throw new UserException("Invalid Usage. Correct Usage: setPenColour *rgb (split by \" \"/hex value/blank (for colour picker)");
		Color colour = null;
		String params = "";
		try {
			if (args.length == 0) {
				colour = JColorChooser.showDialog(null, "Select a colour", getPenColour());
				if (colour == null) throw new UserException("Invalid Argument. Colour selected was invalid, please try again");
				params = colour.getRed()+" "+colour.getGreen()+" "+colour.getBlue();
			} else if (args.length == 1) {
				if (args[0].length() == 7 && Character.toString(args[0].charAt(0)).equals("#")) params = args[0];
				else if (args[0].length() == 6 && !Character.toString(args[0].charAt(0)).equals("#")) params = "#"+args[0];
				colour = Color.decode(params);
			} else {
				int r = Integer.parseInt(args[0]), g = Integer.parseInt(args[1]), b = Integer.parseInt(args[2]);
				if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) throw new NumberFormatException();
				params = String.join(" ", args);
				colour = new Color(r, g, b);
			}
			if (colour == null) throw new NumberFormatException();
		} catch(NumberFormatException e) {
			throw new UserException("Invalid Argument. Colour selected was invalid, please try again");
		}
		setPenColour(colour);
		return command+" "+params;
	}
	
	public void colourCommand(String command, String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: "+command);
		Color colour = null;
		try {
			Field colourField = Color.class.getField(command);
			colour = (Color)colourField.get(null);
		} catch (Exception e) {}; // It needs a catch in case it fails to return; however, it'll never fail to return.
		setPenColour(colour);
	}
	
	public void fill(String[] args) throws UserException {
		if (args.length != 0) throw new UserException("Invalid Usage. Correct Usage: fill");
		BufferedImage buffer = getBufferedImage();
		int pixelColour = buffer.getRGB(getxPos(), getyPos());
		int fillColour = getPenColour().getRGB();
		boolean[][] filled = new boolean[buffer.getWidth()][buffer.getHeight()];
		Queue<Point> queue = new LinkedList<Point>();
		
		queue.add(new Point(getxPos(), getyPos()));
		while (!queue.isEmpty() ) {
			Point point = queue.remove();
			int x = point.x, y = point.y;
			if (x < 0 || y < 0 || buffer.getWidth() - 1 <= x || buffer.getHeight() - 1 <= y) continue;
			if (filled[x][y] || buffer.getRGB(x, y) != pixelColour) continue;
			buffer.setRGB(x, y, fillColour);
			filled[x][y] = true;
			queue.add(new Point(x+1, y));
			queue.add(new Point(x-1, y));
			queue.add(new Point(x, y+1));
			queue.add(new Point(x, y-1));
		}
	}
	
	private boolean lastCommandEquals(String command) {
		return commandHistory.get(commandHistory.size() - 1).split(" ")[0].compareToIgnoreCase(command) == 0;
	}
	
	private void showDialog(String message, String title, boolean isError) {
		int errorType = Boolean.compare(isError, false)+1; // Works due to error types being int and information being 0 and errors being 1
		JOptionPane.showMessageDialog(new JFrame(), message, title, errorType);
	}
}
