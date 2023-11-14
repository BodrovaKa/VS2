package plotter;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.GridLayout;

@SuppressWarnings("serial")
public class Main extends JFrame {
	private GraphPlotter graph;
	private Dimension screenSize;

	public Main(String title) {
		super(title);
		
		screenSize = getToolkit().getScreenSize();
		setBounds(0, 0, screenSize.width, screenSize.height);

		graph = new GraphPlotter(screenSize.width, screenSize.height);
		setLayout(new GridLayout(1, 1, 20, 20));
		add(graph);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		new Main("Function plotter");
	}
}