package plotter;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class GraphPlotter extends JPanel {
	private Graphics g;
	private int screenMaxX;
	private int screenMaxY;
	private double xMin, yMin, xMax, yMax;
	private double xUnit, yRange;
	private Point startPoint;
	private Point endPoint;
	private boolean dragging = false;
	private double initialXMin, initialYMin, initialXMax, initialYMax;
	private boolean resetFlag = false;
	private Rectangle selectionRect;

	public GraphPlotter(int screenMaxX, int screenMaxY) {
		this.screenMaxX = screenMaxX;
		this.screenMaxY = screenMaxY;
		//макс. и мин. значения осей на экране
		xMax = 1 * Math.PI;
		xMin = -xMax;
		yMax = 1.5;
		yMin = -yMax;

		xUnit = (xMax - xMin) / screenMaxX;
		yRange = (yMax - yMin);
		//Сохраняем начальные значения минимальных и максимальных координат по осям x и y.
		initialXMin = xMin;
		initialXMax = xMax;
		initialYMin = yMin;
		initialYMax = yMax;

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				startPoint = e.getPoint();
				dragging = true;

				if (e.getButton() == MouseEvent.BUTTON3 && !resetFlag) { // Right-click to reset
					xMin = initialXMin;
					xMax = initialXMax;
					yMin = initialYMin;
					yMax = initialYMax;

					xUnit = (xMax - xMin) / screenMaxX;
					yRange = (yMax - yMin);

					resetFlag = true;
					repaint(); //  Перерисовывает график.

				} else {
					startPoint = e.getPoint();
					dragging = true;
				}
			}
		    public void mouseDragged(MouseEvent e) {
		        if (dragging) {
		            endPoint = e.getPoint();
		            selectionRect = new Rectangle(startPoint,
		                    new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
		            repaint();
		        }
		    }
		    
			public void mouseReleased(MouseEvent e) {
				endPoint = e.getPoint();
				dragging = false;

				if (resetFlag) {
					xMin = initialXMin;
					xMax = initialXMax;
					yMin = initialYMin;
					yMax = initialYMax;

					xUnit = (xMax - xMin) / screenMaxX;
					yRange = (yMax - yMin);

					resetFlag = false;
				} else {
					double x1 = startPoint.x * xUnit + xMin;
					double x2 = endPoint.x * xUnit + xMin;

					xMin = Math.min(x1, x2);
					xMax = Math.max(x1, x2);

					double y1 = (screenMaxY - startPoint.y) * yRange / screenMaxY + yMin;
					double y2 = (screenMaxY - endPoint.y) * yRange / screenMaxY + yMin;

					yMin = Math.min(y1, y2);
					yMax = Math.max(y1, y2);

					xUnit = (xMax - xMin) / screenMaxX;
					yRange = (yMax - yMin);
					selectionRect = new Rectangle(startPoint,
							new Dimension(endPoint.x - startPoint.x, endPoint.y - startPoint.y));
				}

				repaint();
			}
		});

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;

		coordinateSystem();
		plot();
		
		if (dragging && startPoint != null && endPoint != null) {
			g.setColor(new Color(200, 200, 200, 100));
			int width = endPoint.x - startPoint.x;
			int height = endPoint.y - startPoint.y;
			g.fillRect(startPoint.x, startPoint.y, width, height);
			if (selectionRect != null) {
				g.setColor(new Color(0, 0, 255, 100));
				g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
			}
		}
	}

	private void drawPoint(Point p, Color c) {
		g.setColor(c);
		g.drawLine(p.x, p.y, p.x, p.y);
	}

	private void coordinateSystem() {
		g.setColor(Color.BLACK);
		g.drawLine(0, scrrenMidY(), screenMaxX, scrrenMidY());
		g.drawLine(scrrenMidX(), 0, scrrenMidX(), screenMaxY);
		for (int xScreen = 0; xScreen < screenMaxX; xScreen += 50) {
			double xValue = xMin + (xScreen * xUnit);
			g.drawString(String.format("%.2f", xValue), xScreen, scrrenMidY() + 15);
		}

		for (int yScreen = 0; yScreen < screenMaxY; yScreen += 50) {
			double yValue = yMax - ((double) yScreen / screenMaxY) * yRange;
			g.drawString(String.format("%.2f", yValue), scrrenMidX() + 5, yScreen + 15);
		}
	}

	private void plot() {
		double x = xMin;
		for (int xScreen = 0; xScreen < screenMaxX; xScreen++) {
			x = x + xUnit;
			int yScreen = scrrenMidY() - ((int) ((f(x) / yRange) * screenMaxY));
			drawPoint(new Point(xScreen, yScreen), Color.BLUE);
		}
	}

	private int scrrenMidX() {
		return screenMaxX / 2;
	}

	private int scrrenMidY() {
		return screenMaxY / 2;
	}

	private double f(double x) {
		return Math.sin(x);
		//return Math.exp(x);
	}
}