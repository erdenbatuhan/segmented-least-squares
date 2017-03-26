import java.io.*;
import java.util.*;

public class SegmentedLeastSquares {

	private static final String FILE_NAME = "Points.txt";
	private static final String DIGIT_REGEX = "(.*)(\\d+)(.*)";
	private static final Scanner SCANNER = new Scanner(System.in);
	private static final ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	private static float c = (float) 0.0;

	public static void main(String[] args) {
		readCoordinatesFromFile();
		getCFromUser();
		
		System.out.println(c);
		
		for (Coordinate crd : coordinates)
			System.out.println(crd);
		
		printSolution();
	}

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
		}
		finally {
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
		
		coordinates.add(new Coordinate(x, y));
	}
	
	private static void getCFromUser() {
		System.out.print("Please enter C: ");
		String userInput = SCANNER.nextLine();
		
		if (!userInput.matches(DIGIT_REGEX)) {
			System.out.println("C must be a number, please check your input!");
			getCFromUser();
		} else {
			c = Float.parseFloat(userInput);
		}
	}
	
	private static void printSolution() {
		int n = coordinates.size();
		float[] Opt = new float[n];
		float[][] errors = new float[n][n];
		
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < j; i++) {
				float sum_x  = 0;
				float sum_y  = 0;
				float sum_x2 = 0;
				float sum_xy = 0;
				
				for (int k = i; k < j; k++) {
					sum_x += coordinates.get(k).getX();
					sum_x2 += (float) Math.pow(coordinates.get(k).getX(), 2);
					sum_y += coordinates.get(k).getY();
					sum_xy += coordinates.get(k).getX() * coordinates.get(k).getY();
				}
				
				float a = ((n * sum_xy) - (sum_x * sum_y)) / 
						  ((n * sum_x2) - (float) Math.pow(sum_x, 2));
				float b = (sum_y - (a * sum_x)) / n;
				
				// errors[i][j] = y - a * x - b
				errors[i][j] = (float) Math.pow(coordinates.get(i).getY() - (a * coordinates.get(i).getX()) - b, 2);
			}
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(errors[i][j] + "	");
			}
		
			System.out.println();
		}
		
		Opt[0] = 0;
		
		for (int j = 1; j < n; j++) {
			float min = Float.MAX_VALUE;
			
			for (int i = 1; i < j; i++) {
				float current = errors[i][j] + c + Opt[j - 1];
				
				if (current < min)
					min = current;
			}
			
			Opt[j] = min;
		}
		
		System.out.println("-----");
		
		for (float i : Opt)
			System.out.println(i);
	}
}

class Coordinate {
	
	private float x, y;
	
	public Coordinate(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public float getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}