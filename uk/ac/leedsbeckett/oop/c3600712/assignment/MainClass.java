package uk.ac.leedsbeckett.oop.c3600712.assignment;

import java.awt.FlowLayout;
import javax.swing.JFrame;

public class MainClass {
	static GraphicsSystem gfxInstance = new GraphicsSystem();
	
	public static void main(String[] args) throws InterruptedException {
		init();
		gfxInstance.clear();
		/*gfxInstance.about();
		Thread.sleep(2500);
		gfxInstance.clear();
		gfxInstance.reset();*/
	}
	
	private static void init() {
		JFrame Frame = new JFrame();
		Frame.setLayout(new FlowLayout());
		Frame.add(gfxInstance);
		Frame.pack();
		Frame.setVisible(true);
	}
}
