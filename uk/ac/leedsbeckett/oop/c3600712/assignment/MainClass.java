package uk.ac.leedsbeckett.oop.c3600712.assignment;

import java.awt.FlowLayout;
import javax.swing.JFrame;

public class MainClass {
	static GraphicsSystem gfxInstance = new GraphicsSystem();
	
	public static void main(String[] args) {
		try {
			init();
			while (true) { // This is included to help remedy delay in detecting keypresses for command history navigation
				Thread.sleep(100);
				gfxInstance.repaint();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void init() throws InterruptedException {
		JFrame Frame = new JFrame();
		Frame.setLayout(new FlowLayout());
		Frame.add(gfxInstance);
		Frame.pack();
		Frame.setVisible(true);
		
		gfxInstance.clear();
		gfxInstance.about();
		Thread.sleep(500);
		gfxInstance.clear();
		gfxInstance.reset();
	}
}
