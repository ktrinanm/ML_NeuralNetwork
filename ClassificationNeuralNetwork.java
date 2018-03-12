import java.io.*;
import java.lang.Integer;

public class ClassificationNeuralNetwork
{
	public static void main(String [] args)
	{
	}

	public static void readDataFromFile(int [] classification,
			double [] radii, double [] textures, double [] perimeters,
			double [] area, double [] smoothness, double [] compactness,
			double [] concavity, double [] concavePnts, double [] symmetry, double [] fractalDim)
	{
		String dataFileName = "data.txt";
		String line = "";
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
