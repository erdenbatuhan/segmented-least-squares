import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class SegmentedLeastSquares {
	
	private static final Point ORIGIN = new Point(0, 0);
	private static final ArrayList<Point> POINTS = new ArrayList<Point>();
	private static int N = 0; // number of elements (coordinates).
	private static int n = 0; // index of the last element (coordinate).
	private static float[] opt = null;
	private static float[][] err = null;
	private static float[][] a = null;
	private static float[][] b = null;
	
	public static void main(String[] args) {
		POINTS.add(ORIGIN); // COORDINATES[0] = ORIGIN.
		
		InputMaster.readCoordinatesFromFile();
		InputMaster.readCFromUser();
		
		initializeArrays();
		printDynamicProgrammingSolution();
	}
	
	private static void initializeArrays() {
		N = POINTS.size();
		n = N - 1;
		
		opt = new float[N];
		err = new float[N][N];	
		a = new float[N][N];
		b = new float[N][N];
		
		opt[0] = 0;
	}
	
	
	private static void printDynamicProgrammingSolution() {
		for (int j = 1; j <= n; j++) {
			for (int i = 1; i != j && i <= j; i++) {
				computeTheLeastSquareErrorFor(i, j);
			}
		}
		
		// TEST
		for (float[] sub_err : err) {
			for (float e : sub_err) {
				System.out.print(new DecimalFormat("#.##").format(e) + "	");
			}
			
			System.out.println();
		}
		
		int[] segments = new int[N];
		
		for (int j = 1; j <= n; j++) {
			float min = Float.MAX_VALUE;
			int minIndex = 0;
			
			for (int i = 1; i <= j; i++) {
				float current = err[i][j] + InputMaster.c + opt[i - 1];
				
				if (current < min) {
					min = current;
					minIndex = i;
				}
			}
			
			opt[j] = min;
			segments[j] = minIndex;
		}
		
		// TEST
		for (int i = 0; i <= n; i++)
			System.out.println("OPT[" + i + "] = " + opt[i]);
		
		for (int i = 0; i <= n; i++)
			System.out.println("segments[" + i + "] = " + segments[i]);
	}
	
	private static void computeTheLeastSquareErrorFor(int i, int j) {
		int diff = j - i + 1;
		
		float sum_x  = 0;
		float sum_y  = 0;
		float sum_x2 = 0;
		float sum_xy = 0;
		
		for (int k = i; k <= j; k++) {
			sum_x  += POINTS.get(k).x;
			sum_y  += POINTS.get(k).y;
			sum_x2 += POINTS.get(k).x * POINTS.get(k).x;
			sum_xy += POINTS.get(k).x * POINTS.get(k).y;
		}
		
		a[i][j] = (diff * sum_xy - sum_x * sum_y) / (diff * sum_x2 - sum_x * sum_x);
		b[i][j] = (sum_y - a[i][j] * sum_x) / diff;
		
		for (int k = i; k <= j; k++)
			err[i][j] += Math.pow(POINTS.get(k).y - a[i][j] * POINTS.get(k).x - b[i][j], 2);
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
		private static float c = (float) 0.0;
		
		private static void readCoordinatesFromFile() {
			final File file = new File(FILE_NAME);
			BufferedReader reader = null;
	        
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
	            
				while ((line = reader.readLine()) != null)
					addCoordinates(line);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("File could not be read..");
				System.exit(0);
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					System.out.print("Stream could not be closed..");
					System.exit(0);
				}
			}
		}
			
		private static void addCoordinates(String line) {
			if (!line.matches(DIGIT_REGEX))
				return;
			
			StringTokenizer token = new StringTokenizer(line, " ");
			int tokenCount = token.countTokens();
			
			if (tokenCount != 2) {
				System.out.println("Wrong input format!");
				System.exit(0);
			}
			
			float x = Float.parseFloat(token.nextToken());
			float y = Float.parseFloat(token.nextToken());
			
			POINTS.add(new Point(x, y));
		}
	
		private static void readCFromUser() {
			System.out.print("Please enter C: ");
			String userInput = SCANNER.nextLine();
			
			if (!userInput.matches(DIGIT_REGEX)) {
				System.out.println("C must be a number, please check your input!");
				readCFromUser();
			} else {
				c = Float.parseFloat(userInput);
			}
		}
	}
}