/* Batuhan Erden S004345 Department of Computer Science */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class SegmentedLeastSquares {

	private static final ArrayList<Point> POINTS = new ArrayList<Point>();
	private int N = 0; // number of elements (coordinates).
	private int n = 0; // index of the last element (coordinate).
	private int L = 0; // number of lines.
	private double C = 0; // the coefficient that determines the tradeoff.
	private double[][] a = null; // the array for slopes.
	private double[][] b = null; // the array for intercepts.
	private double[][] errors = null; // the array for errors.
	private double[] minCosts = null; // the array for minimum costs.
	private int[] minIndexes = null; // the array for minimum indexes of minimum costs.
	private int[] segments = null; // the array for the segments.

	public static void main(String[] args) {
		SegmentedLeastSquares segmentedLeastSquares = new SegmentedLeastSquares();
		InputMaster inputMaster = segmentedLeastSquares.new InputMaster();

		POINTS.add(segmentedLeastSquares.new Point(0, 0)); // POINTS[0] = ORIGIN.

		inputMaster.readCoordinatesFromFile();
		inputMaster.readCFromUser();

		segmentedLeastSquares.computeDynamicProgrammingSolution();
		segmentedLeastSquares.printDynamicProgrammingSolution();
		segmentedLeastSquares.displayTheSolutionGraphically();
	}

	private void computeDynamicProgrammingSolution() {
		initializeArrays();

		for (int j = 1; j <= n; j++)
			for (int i = 1; i <= j; i++)
				computeTheLeastSquareErrorFor(i, j);

		computeOptimalSolution();
		computeSegments();
	}

	private void initializeArrays() {
		N = POINTS.size();
		n = N - 1;

		a = new double[N][N];
		b = new double[N][N];
		errors = new double[N][N];
		minCosts = new double[N];
		minIndexes = new int[N];
		segments = new int[N];

		minCosts[0] = 0;
	}

	private void computeTheLeastSquareErrorFor(int i, int j) {
		int diff = j - i + 1;

		double sum_x = 0;
		double sum_y = 0;
		double sum_x2 = 0;
		double sum_xy = 0;

		for (int k = i; k <= j; k++) { // Calculating sums..
			sum_x += POINTS.get(k).x;
			sum_y += POINTS.get(k).y;
			sum_x2 += POINTS.get(k).x * POINTS.get(k).x;
			sum_xy += POINTS.get(k).x * POINTS.get(k).y;
		}

		a[i][j] = (diff * sum_xy - sum_x * sum_y) / (diff * sum_x2 - sum_x * sum_x);
		b[i][j] = (sum_y - a[i][j] * sum_x) / diff;

		for (int k = i; k <= j; k++) // Calculating SSE..
			errors[i][j] += Math.pow(POINTS.get(k).y - a[i][j] * POINTS.get(k).x - b[i][j], 2);
	}

	private void computeOptimalSolution() {
		for (int j = 1; j <= n; j++) {
			double min = Double.POSITIVE_INFINITY;
			int minIndex = 0;

			for (int i = 1; i <= j; i++) {
				double current = errors[i][j] + C + minCosts[i - 1];

				if (current < min) {
					min = current;
					minIndex = i;
				}
			}

			minCosts[j] = min;
			minIndexes[j] = minIndex;
		}
	}

	private void computeSegments() {
		for (int next = n; next >= 1; next--) {
			int current = minIndexes[next];

			if (next == current)
				segments[--current] = next;
			else
				segments[current] = next;

			next = current;
		}
	}

	private void printDynamicProgrammingSolution() {
		System.out.println("----------- Dynamic Programming Solution of Segmented Least Squares -----------");
		System.out.println("c = " + C);
		System.out.println("Minimum Cost = " + minCosts[n]);
		System.out.println("------------------------");

		for (int current = 1; current <= n; current++) {
			int next = segments[current];

			if (next != 0) {
				String bString = (b[current][next] < 0) ? (" - " + (b[current][next] * -1))
						: (" + " + (b[current][next]));
				String yString = a[current][next] + "x" + bString;

				System.out.println((++L) + ") Starting index = " + current + "     End index = " + next + "     a = "
						+ a[current][next] + "     b = " + b[current][next] + "     SSE = " + errors[current][next]
						+ "\n   y = " + yString);
			}
		}
	}

	private void displayTheSolutionGraphically() {
		JFrame frame = new JFrame("Segmented Least Squares");
		JPanel panel = new SolutionPanel(frame);

		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(panel);
		frame.setVisible(true);
	}

	private class Point {

		private double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

	private class InputMaster {

		private static final String FILE_NAME = "Points.txt";
		private static final String DIGIT_REGEX = "(.*)(\\d+)(.*)";
		private Scanner scanner;

		private void readCoordinatesFromFile() {
			File file = new File(FILE_NAME);
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;

				while ((line = reader.readLine()) != null)
					addPoint(line);
			} catch (Exception e) {
				terminateApplication("File could not be read");
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					terminateApplication("Stream could not be closed");
				}
			}
		}

		private void addPoint(String line) {
			if (!line.matches(DIGIT_REGEX))
				return;

			StringTokenizer token = new StringTokenizer(line, " ");
			int tokenCount = token.countTokens();

			if (tokenCount != 2)
				terminateApplication("Wrong input format");

			double x = Double.parseDouble(token.nextToken());
			double y = Double.parseDouble(token.nextToken());

			POINTS.add(new Point(x, y));
		}

		private void readCFromUser() {
			scanner = new Scanner(System.in);

			try {
				System.out.print("> Please enter c: ");
				C = scanner.nextDouble();
			} catch (InputMismatchException e) {
				terminateApplication("C must be a number");
			} finally {
				try {
					scanner.close();
				} catch (Exception e) {
					terminateApplication("Scanner could not be closed");
				}
			}

			System.out.println();
		}

		private void terminateApplication(String errorInfo) {
			System.out.println("Error: " + errorInfo + "!");
			System.out.println("# Application is being terminated..");

			System.exit(0);
		}
	}

	@SuppressWarnings("serial")
	private class SolutionPanel extends JPanel {

		private static final int SIZE = 3;
		private Color[] colors = { Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN };

		public SolutionPanel(JFrame frame) {
			this.setSize(frame.getWidth(), frame.getHeight());
		}

		public void paint(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
			drawCoordinateSystem(g);

			g.drawString("C: " + C, this.getWidth() / 2, 50);
			g.translate(50, this.getHeight() * 3 / 4);

			drawOvals(g2D);
			drawLines(g, g2D);
		}

		private void drawCoordinateSystem(Graphics g) {
			g.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);

			g.drawString("x", this.getWidth() - 30, this.getHeight() - 30);
			g.drawLine(10, this.getHeight() - 20, this.getWidth() - 20, this.getHeight() - 20);

			g.drawString("y", 20, 20);
			g.drawLine(10, 10, 10, this.getHeight() - 20);
		}

		private void drawOvals(Graphics2D g2D) {
			for (int i = 1; i <= n; i++) {
				Ellipse2D.Double oval = new Ellipse2D.Double(POINTS.get(i).x * SIZE - SIZE / 2,
						-POINTS.get(i).y * SIZE - SIZE / 2, SIZE, SIZE);
				g2D.draw(oval);
			}
		}

		private void drawLines(Graphics g, Graphics2D g2D) {
			for (int current = 1, i = 0; current <= n; current++) {
				int next = segments[current];

				if (next != 0) {
					g.setColor(colors[i++ % colors.length]);

					double x1 = POINTS.get(current).x;
					double x2 = POINTS.get(next).x;
					double y1 = -(a[current][next] * x1 + b[current][next]);
					double y2 = -(a[current][next] * x2 + b[current][next]);

					Line2D.Double line = new Line2D.Double(x1 * SIZE, y1 * SIZE, x2 * SIZE, y2 * SIZE);
					g2D.draw(line);
				}
			}
		}
	}
}
