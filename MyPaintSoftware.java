/*
Neil Patel
Semester: Fall 2016 Semester
Professor John Ramirez
Objective/Purpose: The purpose of this assignment is to create your own paint editor with various functionality described in the assignment instructions page. 
GitHub: neilpatel
Last Modified Date: Fall 2016 Semester
*/

// CS 0401 Fall 2016
// Starter Program

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.print.*;
import java.lang.Math.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;

public class MyPaintSoftware
{
  public static String software = "Neil Patel's Mosaic Art Software";
	private Mosaic m;
	private DrawPanel thePanel; 	
	private JPanel buttonPanel;
	private JFrame theWindow;
	private JButton paintIt, eraseIt,editIt;
	private ArrayList<Mosaic> chunks;
	
	private double X, Y;
	private double newSize;
	private Color newColor, background;
	private int selected;

	private boolean painting, erasing,editing;
	private String currFile;
		
	private JMenuBar theBar;
	private JMenu fileMenu;
	private JMenuItem endProgram, saveAs, printScene, newM, openM, saveM, saveAsJPG;
	private JMenuItem help;
	
	private JMenu defaultsMenu,setShape; 
	private JMenuItem setColor, setSize, set2Circle, set2Square;
	
	private JMenu effectsMenu;
	private JMenuItem startTShapes, startTColors;
	
	private boolean Circle = true; 
	private boolean Square;
	private int index;
	
	private JPopupMenu editingMenu;
	private JMenuItem editRecolor,editResize;
	private double editNewSize;
	private Color editNewColor;
	
	private boolean moving;
	private double movingX,movingY;
	private int count=0;
	
	//This is how I coded the twisting shapes tab. 
	private boolean twistS;
	private boolean twistC;
	private JMenuItem stopTC,stopTS;
	private int shapeTracker=0;
	private int colorTracker=0;
	private Color newColor2;
	private boolean firstColor=true;
	
	//If the user selects File>Save
	private boolean saved = false;
	private boolean need2save = true;
	private boolean SavedAlready = false;
	
	private String filename;
	
	public MyPaintSoftware()
	{
		theWindow = new JFrame(software);
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		thePanel = new DrawPanel(750, 750);
		newSize = 15;
		newColor = Color.RED;

		selected = -1;
		painting = false;
		erasing = false;
		paintIt = new JButton("Paint");
		eraseIt = new JButton("Erase");
		editIt = new JButton("Edit");
		ActionListener bListen = new ButtonListener();
		paintIt.addActionListener(bListen);
		eraseIt.addActionListener(bListen);
		editIt.addActionListener(bListen);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,2));
		buttonPanel.add(paintIt);
		buttonPanel.add(eraseIt);
		buttonPanel.add(editIt);
		theWindow.add(buttonPanel, BorderLayout.SOUTH);
		theWindow.add(thePanel, BorderLayout.NORTH);
		
		//This next portion contains all the options in the menu
		
		theBar = new JMenuBar();
		theWindow.setJMenuBar(theBar);
		fileMenu = new JMenu("File");
		theBar.add(fileMenu);
		newM= new JMenuItem("New");
		openM= new JMenuItem("Open");
		saveM= new JMenuItem("Save");
		saveAs = new JMenuItem("Save As");
		saveAsJPG = new JMenuItem("Save As JPG");
		printScene = new JMenuItem("Print");
		help = new JMenuItem("Help");
		endProgram = new JMenuItem("Exit");

		fileMenu.add(newM);
		fileMenu.add(openM);
		fileMenu.add(saveM);
		fileMenu.add(saveAs);
		fileMenu.add(saveAsJPG);
		fileMenu.add(printScene);
		fileMenu.add(help);
		fileMenu.add(endProgram);
		saveAs.addActionListener(bListen);
		printScene.addActionListener(bListen);
		endProgram.addActionListener(bListen);
		newM.addActionListener(bListen);
		openM.addActionListener(bListen);
		saveM.addActionListener(bListen);
		help.addActionListener(bListen);
		
		//These are the items in the default menu that you must have. 
		
		defaultsMenu = new JMenu("Defaults");
		theBar.add(defaultsMenu);
		
		//Setting the Color and setting the size options
		setColor = new JMenuItem("Set Color");
		setSize = new JMenuItem("Set Size");
		
		setShape = new JMenu("Set Shape");
		
		set2Square = new JMenuItem("Square");
		set2Circle = new JMenuItem("Circle");

		setShape.add(set2Square);
		setShape.add(set2Circle);
			
		
		defaultsMenu.add(setColor);
		defaultsMenu.add(setSize);
		defaultsMenu.add(setShape);
		
		setColor.addActionListener(bListen);
		setSize.addActionListener(bListen);
		set2Circle.addActionListener(bListen);
		set2Square.addActionListener(bListen);
		
		
		//Effects Tab in the Menu
		effectsMenu = new JMenu("Effects");
		theBar.add(effectsMenu);
		
		//Start Twisting Shapes/Color Buttons in Menu
		startTShapes = new JMenuItem("Start Twisting Shapes");
		startTColors = new JMenuItem("Start Twisting Colors");
		
		effectsMenu.add(startTShapes);
		effectsMenu.add(startTColors);
		
		startTShapes.addActionListener(bListen);
		startTColors.addActionListener(bListen);
		
		theWindow.pack();
		theWindow.setVisible(true);
		
		//PopUp Menu
		editingMenu = new JPopupMenu();
		editRecolor = new JMenuItem("Recolor");
		editResize = new JMenuItem("Resize");
		
		//Using JPopupMenu
		editingMenu.add(editRecolor);
		editingMenu.add(editResize);
		
		editResize.addActionListener(bListen);
		editRecolor.addActionListener(bListen);
		
		stopTC = new JMenuItem("Stop Twisting Colors");
		stopTS = new JMenuItem("Stop Twisting Shapes");
		
		stopTC.addActionListener(bListen);
		stopTS.addActionListener(bListen);
		
	}
	
	private class DrawPanel extends JPanel
	{
		private int prefwid, prefht;
		
		// Initialize the DrawPanel by creating a new ArrayList for the images
		// and creating a MouseListener to respond to clicks in the panel.
		public DrawPanel(int wid, int ht)
		{
			prefwid = wid;
			prefht = ht;
			
			chunks = new ArrayList<Mosaic>();
			
			// Add MouseListener to this JPanel to respond to the user
			// pressing the mouse.  In your assignment you will also need a
			// MouseMotionListener to respond to the user dragging the mouse.
			addMouseListener(new MListen());
			addMouseMotionListener(new MMListen());
		}
		
		// This method allows a window that encloses this panel to determine
		// how much space the panel needs.  In particular, when the "pack()"
		// method is called from an outer JFrame, this method is called
		// implicitly and the result determines how much space is needed for
		// the JPanel
		public Dimension getPreferredSize()
		{
			return new Dimension(prefwid, prefht);
		}
		
		// This method is responsible for rendering the images within the
		// JPanel.  You should not have to change this code.
		public void paintComponent (Graphics g)       
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			for (int i = 0; i < chunks.size(); i++)
			{
				chunks.get(i).draw(g2d);
			}
		}
		
		// Add a new Mosaic and repaint.  The repaint() method call requests
		// that the panel be redrawn.  Make sure that you call repaint()
		// after changes to your scenes so that the changes are actually
		// exhibited in the display.
		public void add(Mosaic m)
		{
			chunks.add(m);
			repaint();
		}
		
		// Remove the Mosaic at index i and repaint
		public void remove(int i)
		{
			if (chunks.size() > i)
				chunks.remove(i);
			repaint();
		}
		
		// Select a Mosaic that contains the point (x, y).  Note that this
		// is using the contains() method of the Mosaic class, which in turn
		// is checking within the underlying RectangularShape of the object.
		public int select(double x, double y)
		{
			for (int i = 0; i < chunks.size(); i++)
			{
				if (chunks.get(i).contains(x, y))
				{
					return i;
				}
			}
			return -1;
		}
	}
	// Save the images within the window to a file.  Run this program to see the 
	// format of the saved file.
	public void saveImages()
	{
		try
		{
			PrintWriter P = new PrintWriter(new File(currFile));
			P.println(chunks.size());
			for (int i = 0; i < chunks.size(); i++)
			{
				P.println(chunks.get(i).saveFile());
			}
			P.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(theWindow, "I/O Problem - File not Saved");
		}
	}

	// Listener for some buttons.  Note that the JMenuItems are also listened
	// for here.  Like JButtons, JMenuItems also generate ActionEvents when
	// they are clicked on.  You will need to add more JButtons and JMenuItems
	// to your program and the logic of handling them will also be more
	// complex.  See details in the Assignment 5 specifications.
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == paintIt)
			{
				painting = true;
				paintIt.setForeground(newColor);
				erasing = false;
				eraseIt.setForeground(Color.BLACK);
				editing = false;
				editIt.setForeground(Color.BLACK);
				saved = false;
			}
			else if (e.getSource() == eraseIt)
			{
				painting = false;
				paintIt.setForeground(Color.BLACK);
				erasing = true;
				eraseIt.setForeground(newColor);
				editing = false;
				editIt.setForeground(Color.BLACK);
				saved = false;
			}
			//edit it
			else if (e.getSource() == editIt)
			{
				count = 0;
				editNewColor = newColor;
				editNewSize = newSize;
				painting = false;
				paintIt.setForeground(Color.BLACK);
				erasing = false;
				eraseIt.setForeground(Color.BLACK);
				editing = true;
				editIt.setForeground(newColor);
				saved = false;
			}
			else if (e.getSource() == saveAs)
			{
				currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
				saveImages();
				theWindow.setTitle(software + " - " + currFile);
				need2save=false;
				SavedAlready=true;
			}
			else if (e.getSource() == endProgram)
			{
        		if (saved)
        		{
  
        		}
        		else
        		{
        			int opt = JOptionPane.showConfirmDialog(null, "Would you like to save?", "Select One", JOptionPane.YES_NO_OPTION);
          			//0=Yes
					//1=No
        			if (opt == 0)//yes
        			{
        				if (need2save)
        				{
        					currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
        					
        					//This is to see if the user already saved the file. 
        					if (SavedAlready)
        					{
        						int ask = JOptionPane.showConfirmDialog(null, "Would you like to overwrite your previous file?", "Select One", JOptionPane.YES_NO_OPTION);
        						
        					
        						if (ask == 1)
        						{
        							saveImages();
        							theWindow.setTitle(software + " - " + currFile);
        							saved = true;
        							need2save = false;
        						}
        						else if (ask == 0)
        						{
        							JOptionPane.showMessageDialog(null, "The file was not overwritten.");
        						}
        					}
        					else
        					{
        						saveImages();
        						theWindow.setTitle(software + " - " + currFile);
        						saved = true;
        						need2save = false;
        					}
        				}
        				else 
        				{
        					
        				}
        			}
				}
				System.exit(0);
			}
			else if(e.getSource() == help) 
			{
                JOptionPane.showMessageDialog(theWindow, "Welcome to the help menu. Below is a description of the functions of this program.\n" +
                        "Drawing and creating are fun and valuable experiences for children and adults alike. \nIn this program you will be able to use " +
                        "a simple drawing tool that utilizes small circle and square \n\"mosaic\" shapes to produce large pictures in a window.  " +
                        "You will have some options for the \nwith regard to the size, shape and color of the mosaics and once drawn you will be able " +
                        "to edit \nand / or delete the individual shapes.  You will also be able to save the created scenes to a file and restore them at a later date. \n\n The message was written by: Neil Patel  ");

            }
			else if (e.getSource() == printScene)
			{
				 Printable thePPanel = new thePrintPanel(thePanel); 
			     PrinterJob job = PrinterJob.getPrinterJob();
         		 job.setPrintable(thePPanel);
         		 boolean ok = job.printDialog();
         		 if (ok) 
         		 {
             	 	try {
                  		job.print();
             		} 
             		catch (PrinterException ex) {
              		//Print Not Completed Successfully
             		}
             	 }
        	}
        	
        	else if (e.getSource() == setColor)
        	{
        		painting = true;
        		Color intBack = setColor.getBackground();
        		
        		background = JColorChooser.showDialog(null, "Choose A Shape Color",intBack);
        		
        		paintIt.setForeground(background);
        		
        		newColor = background;
        		
        		if (twistC)
        		{
        			effectsMenu.remove(stopTC);
        			effectsMenu.add(startTColors);
        			twistC = false;
        			JOptionPane.showMessageDialog(null, "Twisting Colors Turned Off.");
        		}
        	}
        	else if (e.getSource() == setSize)
        	{
        		newSize=Double.parseDouble(JOptionPane.showInputDialog(null,"Enter New Default Size"));
        	}
        	else if (e.getSource() == set2Circle)
        	{
        		Circle = true;
        		Square = false;
        		if (twistS)
        		{
        			
        			effectsMenu.remove(stopTS);
        			effectsMenu.add(startTShapes);
        			twistS = false;
        			JOptionPane.showMessageDialog(null, "Twisting Shapes Turned Off.");
        		}
        	}
        	else if (e.getSource() == set2Square)
        	{
        		
        		Circle = false;
        		Square = true;
        	}
        	
        	else if (e.getSource() == startTShapes)
        	{
        		effectsMenu.remove(startTShapes);
        		effectsMenu.add(stopTS);
        		twistS = true;
        	}
        	else if (e.getSource() == startTColors)
        	{
        		effectsMenu.remove(startTColors);
        		effectsMenu.add(stopTC);
        		twistC = true;
        		Color intBack = setColor.getBackground();
        		newColor2 = JColorChooser.showDialog(null, "Choose A Second Shape Color",intBack);
        	}
           	else if (e.getSource() == stopTS) 
        	{
        		effectsMenu.remove(stopTS);
        		effectsMenu.add(startTShapes);
        		twistS = false;
        		JOptionPane.showMessageDialog(null, "Twisting Shapes Turned Off.");
        	}
        	else if (e.getSource() == stopTC)
        	{
        		effectsMenu.remove(stopTC);
        		effectsMenu.add(startTColors);
        		twistC = false;
        		JOptionPane.showMessageDialog(null, "Twisting Colors Turned Off.");
        	}
        	
        	else if (e.getSource() == newM)
        	{
        		//Creating a new file
        		currFile = null;
        		theWindow.setTitle(software + " - " + currFile);
        		if (saved)
        		{
        			chunks = new ArrayList<Mosaic>();
        			thePanel.repaint();
           			need2save = false;
        		}
        		else 
        		{
        			int opt = JOptionPane.showConfirmDialog(null, "Would you like to save?", "Select One", JOptionPane.YES_NO_OPTION);
        			//0 = Yes
					//1 = No
        			if (opt == 0)
        			{
        				if (need2save)
        				{
        					currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
        					if (SavedAlready)
        					{
        						int ask = JOptionPane.showConfirmDialog(null, "Would you like to overwrite your previous file?", "Select One", JOptionPane.YES_NO_OPTION);
        						if (ask == 1)
        						{
        							saveImages();
        							
        						}
        						else if (ask == 0)
        						{
        							JOptionPane.showMessageDialog(null, "The file was not overwritten.");
        						}
        					}
        					else
        					{
        						saveImages();
        						
        					}
        					
        				}
        				else
        				{
        					int ask = JOptionPane.showConfirmDialog(null, "Would you like to overwrite your previous file?", "Select One", JOptionPane.YES_NO_OPTION);
        						if (ask == 0)
        						{
        							saveImages();
        							theWindow.setTitle(software + " - " + currFile);
        							JOptionPane.showMessageDialog(null, "The file was overwritten.");
        						}
        						else if (ask == 1)
        						{
        							JOptionPane.showMessageDialog(null, "The file was not overwritten.");
        						}
        				}	
						chunks= new ArrayList<Mosaic>();
        				thePanel.repaint();
        				need2save = false;
        			}
        			else if (opt == 1)
        			{
        				chunks = new ArrayList<Mosaic>();
        				thePanel.repaint();
        				need2save = false;
        			}
        		}
        	}
        	else if (e.getSource() == openM)
        	{        		
        		if (saved)
        		{
					chunks = new ArrayList<Mosaic>();
        			thePanel.repaint();
        			need2save = false;
        		}
        		else
        		{
        			int opt = JOptionPane.showConfirmDialog(null, "Would you like to save?", "Select One", JOptionPane.YES_NO_OPTION);
        			if (opt == 0)
        			{
        				if (need2save)
        				{
        					currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
        					
        					if (SavedAlready)
        					{
        						int ask = JOptionPane.showConfirmDialog(null, "Would you like to overwrite your previous file?", "Select One", JOptionPane.YES_NO_OPTION);
        						if (ask == 1)
        						{
        							saveImages();
        							theWindow.setTitle(software + " - " + currFile);
        							saved = true;
        							need2save = false;
        						}
        						else if (ask == 0)
        						{
        							JOptionPane.showMessageDialog(null, "The file was not overwritten.");
        						}
        					}
        					else
        					{
        						saveImages();
        						theWindow.setTitle(software + " - " + currFile);
        						saved = true;
        						need2save = false;
        					}
        				}
        				else
        				{
        					
        					int ask = JOptionPane.showConfirmDialog(null, "Would you like to overwrite your previous file?", "Select One", JOptionPane.YES_NO_OPTION);
        						if (ask == 0)
        						{
        							saveImages();
        							theWindow.setTitle(software + " - " + currFile);
        							JOptionPane.showMessageDialog(null, "The file was overwritten.");
        							saved = true;
        							need2save = false;
        						}
        						else if (ask == 1)
        						{
        							JOptionPane.showMessageDialog(null, "The file was not overwritten.");
        						}
        				}
        			}
					chunks= new ArrayList<Mosaic>();
        			thePanel.repaint();
					
        			boolean notFound = true;
        			while (notFound)
        			{
        				try
        				{
        					filename = JOptionPane.showInputDialog(theWindow,"Enter the file to be retrieved:");
        					File fileIn = new File(filename);
        					Scanner inFile = new Scanner(fileIn);
        					String StartLine;
        					int numItems = Integer.parseInt(inFile.nextLine());
        					String [] val4pic = new String[500]; 
        					StartLine = inFile.nextLine();
        					
        					while ((StartLine = inFile.nextLine()) != null) 
        					{
        						val4pic = StartLine.split(",");
        						String WhichShape = val4pic[0];
        						double TempSize = Double.parseDouble(val4pic[1]);
        						double TempX = Double.parseDouble(val4pic[2]);
        						double TempY = Double.parseDouble(val4pic[3]);
        						
        						String [] colArr = new String[5];
        						colArr = val4pic[4].split(":");
        						
        						int amtRed = Integer.parseInt(colArr[0]);
        						int amtGreen = Integer.parseInt(colArr[1]);
        						int amtBlue = Integer.parseInt(colArr[2]);
        						Color openTempColor = new Color(amtRed,amtGreen,amtBlue);
        						
                				if (WhichShape.equals("Circle"))
                				{
                					m = new MCircle(TempSize, TempX, TempY, openTempColor);
                				}
                				else if (WhichShape.equals("Square"))
                				{
                					m = new MSquare(TempSize, TempX, TempY, openTempColor);
                				}
                				thePanel.add(m);
            				}
            				currFile = filename;
            				theWindow.setTitle(software + " - " + currFile);
        					notFound = false;
        				}
        				catch (IOException except)
        				{
        				}
        				catch (NoSuchElementException except)
        				{
        					
        					notFound = false;
        				}
        			}
        		}
        				
        	}
        	else if (e.getSource() == saveM)
        	{
        		if (saved)
        		{
        			
        			saveImages();
        			need2save = false;
					SavedAlready = true;
        		}
        		else
        		{	
        			
        			currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
					saveImages();
					theWindow.setTitle(software + " - " + currFile);
					need2save = false;
					SavedAlready = true;
        		}
        		
				saved = true;
				
        	}
       
        	else if (e.getSource() == editRecolor)
        	{
        		Color intBack = setColor.getBackground();
        		try
        		{
        			editNewColor = JColorChooser.showDialog(null, "Choose A Shape Color",intBack);
        			chunks.get(index).setColor(editNewColor);
        			thePanel.repaint();
        		}
        		catch (NullPointerException except)
        		{}
					editingMenu.setVisible(false);
        	}
        	else if (e.getSource() == editResize)
        	{	
        		try
        		{
        			editNewSize=Double.parseDouble(JOptionPane.showInputDialog(null,"Enter New Size"));
        			chunks.get(index).setSize(editNewSize);
        			thePanel.repaint();
        		}
        		catch (NullPointerException except)
        		{}
					editingMenu.setVisible(false);
        	}
		}
	}
	

	private class MListen extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			X = e.getX();
			Y = e.getY();
			editingMenu.setVisible(false);
			if (painting)
			{
				if ( !(twistS) && !(twistC))
				{
					if (Circle)
					{
						m = new MCircle(newSize, X, Y, newColor);
					}
					else if (Square)
					{
						m = new MSquare(newSize, X, Y, newColor);
					}
					saved = false;	
					thePanel.add(m);
				}
				else if (twistS && twistC)
				{
					
					if ((shapeTracker%2)==1)
					{
						Circle = true;
						Square = false;
					}
					else 
					{
						Circle = false;
						Square = true;
					}
					if ((colorTracker%2) == 1)
					{
						firstColor = true;
					}
					else
					{
						firstColor = false;
					}
					if (Circle)
					{
						if (firstColor)
						{
							m = new MCircle(newSize, X, Y, newColor);
						}
						else
						{
							m = new MCircle(newSize, X, Y, newColor2);
						}
						shapeTracker++;
						colorTracker++;
					}
					else if (Square)
					{
						if (firstColor)
						{
							m = new MSquare(newSize, X, Y, newColor);
						}
						else
						{
							m = new MSquare(newSize, X, Y, newColor2);
						}
						shapeTracker++;
						colorTracker++;
					}
				
					saved = false;	
					thePanel.add(m);
				}
				else if (twistC)
				{
					
					if ((colorTracker%2) == 1)
					{
						firstColor = true;
					}
					else
					{
						firstColor = false;
					}
					
					if (firstColor)
					{
						m = new MCircle(newSize, X, Y, newColor);
						colorTracker++;
					}
					else
					{
						m = new MCircle(newSize, X, Y, newColor2);
						colorTracker++;
					}
					saved = false;	
					thePanel.add(m);
				}
				else if (twistS)
				{
					if ((shapeTracker%2) == 1)
					{
						Circle = true;
						Square = false;
					}
					else
					{
						Circle = false;
						Square = true;
					}
					if (Circle)
					{
						m = new MCircle(newSize, X, Y, newColor);
						shapeTracker++;
					}
					else if (Square)
					{
						m = new MSquare(newSize, X, Y, newColor);
						shapeTracker++;
					}
					saved = false;	
				
					thePanel.add(m);	
				}
			}
			else if (erasing)
			{
				int loc = thePanel.select(X, Y);
				if (loc > -1)
				{
					thePanel.remove(loc);
				}
				saved = false;
			}
			else if (editing)
			{
				if (SwingUtilities.isLeftMouseButton(e)) //for a left click
				{
					try
					{
						if (index>= 0)
						{
							chunks.get(index).highlight(false);
						}
						index = thePanel.select(X,Y);
						if (index >= 0)
						{
							chunks.get(index).highlight(true);
						}
						thePanel.repaint();
						saved = false;
					}
					catch (ArrayIndexOutOfBoundsException except)
					{
					}
				}
				else if (SwingUtilities.isRightMouseButton(e))
				{
					try
					{
						if (index >= 0)
						{
							chunks.get(index).highlight(false);
						}
						index = thePanel.select(X,Y);
						if (index >= 0)
						{
							chunks.get(index).highlight(true);
						}
						thePanel.repaint();
					}
					catch (ArrayIndexOutOfBoundsException except)
					{
					}
					editingMenu.setLocation((int) X,(int) Y);
					editingMenu.setVisible(true);
				}
			}	
		}	
		public void mouseReleased(MouseEvent e)
		{
			
			if (editing)
			{
				if(moving)
				{
					int loc = thePanel.select(movingX, movingY);
					editNewColor=(chunks.get(loc)).getColor();
					editNewSize=(chunks.get(loc)).getSize();
					if (loc > -1)
					{
						thePanel.remove(loc);
					}
				
					X = e.getX();
					Y = e.getY();
					
					if (Circle)
					{
						m = new MCircle(editNewSize, X, Y, editNewColor);
					}
					else if (Square)
					{
						m = new MSquare(editNewSize, X, Y, editNewColor);
					}
					thePanel.add(m);
					thePanel.repaint();
					saved = false;
					count = 0;
					moving = false;
				}
			}
		}		
	}


	private class MMListen implements MouseMotionListener
	{

		private double [] arrX = new double[100000];
		private double [] arrY = new double[100000];
		private double startX, startY, endX, endY;
		
		public void mouseMoved(MouseEvent e)
        {
        }
		
		public void mouseDragged(MouseEvent e)
        {
        	X = e.getX();  
			Y = e.getY();
        	if (painting)
        	{
        		if ( !(twistC) && !(twistS))
				{
					if (count == 0)
					{
						startX = X;
						startY = Y;
						endX = X;
						endY = Y;
					}
					arrX[count]=X;
					arrY[count]=Y;
					double distance = Math.sqrt(Math.pow(X-endX,2)+Math.pow(Y-endY,2));
			
					if (distance >= newSize) 
					{
						if (Circle)
						{
							m = new MCircle(newSize, X, Y, newColor);
						}
						else if (Square)
						{
							m = new MSquare(newSize, X, Y, newColor);
						}
						thePanel.add(m);
						endX = X;
						endY = Y;
					}
			
					saved = false;
				
					count++;
				}
				else if (twistS && twistC)
				{
					if ((shapeTracker%2)==1)
					{
						Circle = true;
						Square = false;
					}
					else
					{
						Circle = false;
						Square = true;
					}
					
					if ((colorTracker%2) == 1)
					{
						firstColor = true;
					}
					else
					{
						firstColor = false;
					}
					
					if (count == 0)
					{
						startX = X;
						startY = Y;
						endX = X;
						endY = Y;
					}
					arrX[count]=X;
					arrY[count]=Y;
			
					double distance= Math.sqrt(Math.pow(X-endX,2)+Math.pow(Y-endY,2));
			
					if (distance >= newSize)
					{
						
						if (Circle)
						{
							if (firstColor)
							{
								m = new MCircle(newSize, X, Y, newColor);
							}
							else
							{
								m = new MCircle(newSize, X, Y, newColor2);
							}
							shapeTracker++;
							colorTracker++;
						}
						else if (Square)
						{
							if (firstColor)
							{
								m = new MSquare(newSize, X, Y, newColor);
							}
							else
							{
								m = new MSquare(newSize, X, Y, newColor2);
							}
							shapeTracker++;
							colorTracker++;
						}
						thePanel.add(m);
						endX = X;
						endY = Y;
					}
				
					saved = false;
				

					count++;
					
				}
				else if (twistC)
				{
					
					if ((colorTracker%2)==1)
					{
						firstColor = true;
						
					}
					else 
					{
						firstColor = false;
					}
					
					if (count==0) 
					{
						startX = X;
						startY = Y;
						endX = X;
						endY = Y;
					}
			
					
					arrX[count]=X;
					arrY[count]=Y;
			
					double distance= Math.sqrt(Math.pow(X-endX,2)+Math.pow(Y-endY,2));
					
					if (distance >= newSize)
					{
						if (firstColor)
						{
							m = new MSquare(newSize, X, Y, newColor);
						}
						else
						{
							m = new MSquare(newSize, X, Y, newColor2);
						}
						colorTracker++;
						thePanel.add(m);
						endX = X;
						endY = Y;
					}
					saved = false;
					count++;
				}
				else if (twistS)
				{
					if ((shapeTracker%2)==1)
					{
						Circle = true;
						Square = false;
					}
					else
					{
						Circle = false;
						Square = true;
					}
				
					if (count==0)
					{
						startX = X;
						startY = Y;
						endX = X;
						endY = Y;
					}
					arrX[count]=X;
					arrY[count]=Y;
			
					double distance= Math.sqrt(Math.pow(X-endX,2)+Math.pow(Y-endY,2));
			
					if (distance >= newSize) 
					{
						if (Circle)
						{
							m = new MCircle(newSize, X, Y, newColor);
							shapeTracker++;
						}
						else if (Square)
						{
							m = new MSquare(newSize, X, Y, newColor);
							shapeTracker++;
						}
						thePanel.add(m);
						endX = X;
						endY = Y;
					}
					saved = false;
					count++;
				}
			}
			else if (erasing)
			{
				int loc = thePanel.select(X, Y);
				if (loc > -1)
				{
					thePanel.remove(loc);
				}
				saved = false;
			}
			else if (editing)
			{
				arrX[count]=X;
				arrY[count]=Y;
				
				movingX=arrX[0];
				movingY=arrY[0];
				count++;
				moving = true;
				saved = false;	
			}
        }
	}

	public static void main(String [] args)
	{
		new MyPaintSoftware();
	}
}

class thePrintPanel implements Printable
{
	JPanel panelToPrint;
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException
    {
        if (page > 0) 
		{
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform t = new AffineTransform();
        t.scale(0.9, 0.9);
        g2d.transform(t);
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        panelToPrint.printAll(g);
		
        return PAGE_EXISTS;
    }
    
    public thePrintPanel(JPanel p)
    {
    	panelToPrint = p;
    }
}
