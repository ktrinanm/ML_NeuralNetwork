import java.io.*;
import java.lang.Integer;

public class ClassificationNeuralNetwork
{
	public static int [] classification;
	public static double [] radii, textures, perimeters, area, smoothness,
		   compactness, concavity, concavePnts, symmetry, fractalDim;
	
	public static void main(String [] args)
	{
		int numLines = 569;
		classification = new int[numLines];
		radii = new double[numLines];
		textures = new double[numLines];
		perimeters = new double[numLines];
		area = new double[numLines];
		smoothness = new double[numLines];
		compactness = new double[numLines];
		concavity = new double[numLines];
		concavePnts = new double[numLines];
		symmetry = new double[numLines];
		fractalDim = new double[numLines];

	}

	public static void readDataFromFile()
	{
		String dataFileName = "data.txt";
		String line = "";
		int currLineNum = 0;

		try
		{
			FileReader fileReader = new FileReader(dataFileName);
			BufferedReader bufferedReader 
				= new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != "")
			{
				String [] data = line.split(",");

				classification[currLineNum] 
					= (data[1].charAt(0) == 'M' ? 1 : 0);
				radii[currLineNum] = Double.parseDouble(data[2]);
				textures[currLineNum] = Double.parseDouble(data[3]);
				perimeters[currLineNum] = Double.parseDouble(data[4]);
				area[currLineNum] = Double.parseDouble(data[5]);
				smoothness[currLineNum] = Double.parseDouble(data[6]);
				compactness[currLineNum] = Double.parseDouble(data[7]);
				concavity[currLineNum] = Double.parseDouble(data[8]);
				concavePnts[currLineNum] = Double.parseDouble(data[9]);
				symmetry[currLineNum] = Double.parseDouble(data[10]);
				fractalDim[currLineNum] = Double.parseDouble(data[11]);
				currLineNum++;
			}

			bufferedReader.close();
		}
		catch (Exception exc)
		{
			System.out.println("Error reading data from file at line "
					+ currLineNum);
		}
	}
}
