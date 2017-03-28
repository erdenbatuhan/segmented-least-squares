/* Batuhan Erden S004345 Department of Computer Science */

import java.io.*;
import java.util.*;

public class SegmentedLeastSquares {

	private static final Point ORIGIN = new Point(0, 0);
	private static final ArrayList<Point> POINTS = new ArrayList<Point>();
	private static int N = 0; // number of elements (coordinates).
	private static int n = 0; // index of the last element (coordinate).
	private static float C = 0; // the coefficient that determines the tradeoff.
	private static float[][] a = null; // the array for slopes.
	private static float[][] b = null; // the array for intercepts.
	private static float[][] err = null; // the array for errors.
	private static float[] opt = null; // the array for minimum costs.
	private static int[] minIndexes = null; // the array for minimum indexes of minimum costs.
	private static boolean[][] segments = null; // the array for the segments.

	public static void main(String[] args) {
		POINTS.add(ORIGIN); // COORDINATES[0] = ORIGIN.

		InputMaster.readCoordinatesFromFile();
		InputMaster.readCFromUser();

		computeDynamicProgrammingSolution();
		printDynamicProgrammingSolution();
	}

	private static void computeDynamicProgrammingSolution() {
		initializeArrays();

		for (int j = 1; j <= n; j++) {
			for (int i = 1; i != j && i <= j; i++) {
				computeTheLeastSquareErrorFor(i, j);
			}
		}

		computeOptimalSolution();
		computeSegments();
	}

	private static void initializeArrays() {
		N = POINTS.size();
		n = N - 1;

		a = new float[N][N];
		b = new float[N][N];
		err = new float[N][N];
		opt = new float[N];
		minIndexes = new int[N];
		segments = new boolean[N][N];

		opt[0] = 0;
	}

	private static void computeTheLeastSquareErrorFor(int i, int j) {
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
			err[i][j] += Math.pow(POINTS.get(k).y - a[i][j] * POINTS.get(k).x - b[i][j], 2);
	}

	private static void computeOptimalSolution() {
		for (int j = 1; j <= n; j++) {
			float min = Float.MAX_VALUE;
			int minIndex = 0;

			for (int i = 1; i <= j; i++) {
				float current = err[i][j] + C + opt[i - 1];

				if (current < min) {
					min = current;
					minIndex = i;
				}
			}

			opt[j] = min;
			minIndexes[j] = minIndex;
		}
	}

	private static void computeSegments() {
		for (int next = n; next >= 1; next--) {
			int current = minIndexes[next];

			if (next == current)
				segments[--current][next] = true;
			else
				segments[current][next] = true;
			
			next = current;
		}
	}

	private static void printDynamicProgrammingSolution() {
		System.out.println("----------- Dynamic Programming Solution of Segmented Least Squares -----------");
		System.out.println("c = " + C);
		System.out.println("Minimum Cost = " + opt[n]);
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

	private static class Point {

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

	private static class InputMaster {

		private static final String FILE_NAME = "Points.txt";
		private static final String DIGIT_REGEX = "(.*)(\\d+)(.*)";
		private static final Scanner SCANNER = new Scanner(System.in);

		private static void readCoordinatesFromFile() {
			final File file = new File(FILE_NAME);
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;

				while ((line = reader.readLine()) != null)
					addCoordinates(line);
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

		private static void addCoordinates(String line) {
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

		private static void readCFromUser() {
			try {
				System.out.print("> Please enter c: ");
				C = SCANNER.nextFloat();
			} catch (InputMismatchException e) {
				terminateApplication("C must be a number");
			}

			System.out.println();
		}

		private static void terminateApplication(String errorInfo) {
			System.out.println("Error: " + errorInfo + "!");
			System.out.println("# Application is being terminated..");

			System.exit(0);
		}
	}
}