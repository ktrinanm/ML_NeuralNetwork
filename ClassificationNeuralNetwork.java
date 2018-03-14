import java.io.*;
import java.util.*;
import java.lang.Integer;

public class ClassificationNeuralNetwork
{
	public static int [] classification;
	public static double [] radii, textures, perimeters, area, smoothness,
		   compactness, concavity, concavePnts, symmetry, fractalDim;
	public static ArrayList<double[]> listOfTrainingData;
	public static ArrayList<Integer> listOfTrainingClassifications;
	
	public static ArrayList<double[]> listOfTestData;
	public static ArrayList<Integer> listOfTestClassifications;
	
	public static int numLines=569;
	
	public static void main(String [] args)
	{
		classification = new int[numLines];
		
		//The 10 factors we're accounting for.
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

		listOfTrainingData= new ArrayList<double[]>();
		listOfTrainingClassifications= new ArrayList<Integer>();
		
		listOfTestData= new ArrayList<double[]>();
		listOfTestClassifications= new ArrayList<Integer>();
		
		putDataInArrayList();
		
		//Every day I'm shufflin'
		shuffle();
		
	}

	public static void readDataFromFile()
	{
		String dataFileName = "data.txt";
		String line = "";
		int currLineNum = 0;

		try
		{
			BufferedReader bufferedReader 
				= new BufferedReader(new FileReader(dataFileName));

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
	
	public static void putDataInArrayList()
	{
		for(int i=0; i<numLines; i++)
		{
			double[] toAdd= new double[10];
			
			toAdd[0]=radii[i];
			toAdd[1]=textures[i];
			toAdd[2]=perimeters[i];
			toAdd[3]=area[i];
			toAdd[4]=smoothness[i];
			toAdd[5]=compactness[i];
			toAdd[6]=concavity[i];
			toAdd[7]=concavePnts[i];
			toAdd[8]=symmetry[i];
			toAdd[9]=fractalDim[i];
			
			listOfTestData.add(toAdd);
			listOfTestClassifications.add(classification[i]);
		}
	}
	
	public static void shuffle()
	{
		int eightyPercentOfData= (int) (listOfTestData.size()*.8);
		
		for(int i=0; i<eightyPercentOfData; i++)
		{
			int toRemove= (int) (Math.random()*listOfTestData.size());
			listOfTrainingData.add(listOfTestData.remove(toRemove));
			listOfTrainingClassifications.add(listOfTestClassifications.remove(toRemove));
		}
		
		System.out.println("For reference, the size of the testing data is " + listOfTestData.size() + ", and the testing classification size is " + listOfTestClassifications.size() + ", which should be the same.");
		System.out.println("For reference, the size of the training data is " + listOfTrainingData.size() + ", and the training classification size is " + listOfTrainingClassifications.size() + ", which should be the same.");
	}
}
