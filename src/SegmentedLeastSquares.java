/* Batuhan Erden S004345 Department of Computer Science */

import java.io.*;
import java.util.*;

public class SegmentedLeastSquares {

	private static final ArrayList<Point> POINTS = new ArrayList<Point>();
	private int N = 0; // number of elements (coordinates).
	private int n = 0; // index of the last element (coordinate).
	private float C = 0; // the coefficient that determines the tradeoff.
	private float[][] a = null; // the array for slopes.
	private float[][] b = null; // the array for intercepts.
	private float[][] errors = null; // the array for errors.
	private float[] minCosts = null; // the array for minimum costs.
	private int[] minIndexes = null; // the array for minimum indexes of minimum costs.
	private boolean[][] segments = null; // the array for the segments.

	public static void main(String[] args) {
		SegmentedLeastSquares segmentedLeastSquares = new SegmentedLeastSquares();
		InputMaster inputMaster = segmentedLeastSquares.new InputMaster();
		
		POINTS.add(segmentedLeastSquares.new Point(0, 0)); // POINTS[0] = ORIGIN.

		inputMaster.readCoordinatesFromFile();
		inputMaster.readCFromUser();

		segmentedLeastSquares.computeDynamicProgrammingSolution();
		segmentedLeastSquares.printDynamicProgrammingSolution();
	}

	private void computeDynamicProgrammingSolution() {
		initializeArrays();

		for (int j = 1; j <= n; j++) {
			for (int i = 1; i != j && i <= j; i++) {
				computeTheLeastSquareErrorFor(i, j);
			}
		}

		computeOptimalSolution();
		computeSegments();
	}

	private void initializeArrays() {
		N = POINTS.size();
		n = N - 1;

		a = new float[N][N];
		b = new float[N][N];
		errors = new float[N][N];
		minCosts = new float[N];
		minIndexes = new int[N];
		segments = new boolean[N][N];

		minCosts[0] = 0;
	}

	private void computeTheLeastSquareErrorFor(int i, int j) {
		int diff = j - i + 1;

		float sum_x = 0;
		float sum_y = 0;
		float sum_x2 = 0;
		float sum_xy = 0;

		for (int k = i; k <= j; k++) {
			sum_x += POINTS.get(k).x;
			sum_y += POINTS.get(k).y;
			sum_x2 += POINTS.get(k).x * POINTS.get(k).x;
			sum_xy += POINTS.get(k).x * POINTS.get(k).y;
		}

		a[i][j] = (diff * sum_xy - sum_x * sum_y) / (diff * sum_x2 - sum_x * sum_x);
		b[i][j] = (sum_y - a[i][j] * sum_x) / diff;

		for (int k = i; k <= j; k++)
			errors[i][j] += Math.pow(POINTS.get(k).y - a[i][j] * POINTS.get(k).x - b[i][j], 2);
	}

	private void computeOptimalSolution() {
		for (int j = 1; j <= n; j++) {
			float min = Float.MAX_VALUE;
			int minIndex = 0;

			for (int i = 1; i <= j; i++) {
				float current = errors[i][j] + C + minCosts[i - 1];

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
				segments[--current][next] = true;
			else
				segments[current][next] = true;
			
			next = current;
		}
	}

	private void printDynamicProgrammingSolution() {
		System.out.println("----------- Dynamic Programming Solution of Segmented Least Squares -----------");
		System.out.println("c = " + C);
		System.out.println("Minimum Cost = " + minCosts[n]);
		System.out.println("------------------------");

		for (int current = 1, i = 1; current <= n; current++) {
			for (int next = 1; next <= n; next++) {
				if (segments[current][next]) {
					String bString = (b[current][next] < 0) ? 
									(" - " + (b[current][next] * -1)) : 
										(" + " + (b[current][next]));
					String yString = a[current][next] + "x" + bString;

					System.out.println((i++) + ") Starting index = " + current + "     End index = " + next + 
									           "     a = " + a[current][next] + "     b = " + b[current][next] + 
									           "\n   y = " + yString);
				}
			}
		}
	}

	private class Point {

		private float x, y;

		public Point(float x, float y) {
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
			final File file = new File(FILE_NAME);
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

			float x = Float.parseFloat(token.nextToken());
			float y = Float.parseFloat(token.nextToken());

			POINTS.add(new Point(x, y));
		}

		private void readCFromUser() {
			scanner = new Scanner(System.in);
			
			try {
				System.out.print("> Please enter c: ");
				C = scanner.nextFloat();
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
}