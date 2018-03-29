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
	
	public static double[][] neurons;  //store the state of each neuron (or input and output node)
	public static double[][][] weights;//stores what is says on the tin
	public static int numLayers; //number of layers, first always input, end always output, middle hidden
	public static int hiddenNeurons; //number of nodes in each hidden layer, probably should be per layer, but whatever
	public static int numClasses; //number of clasifications, futureproofing
	public static double learningRate; //the magic learning rate aka alpha
	
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

		
		putDataInArrayList();
		
		//Here's a title that's a little more descriptive.
		randomlySelectDataForTrainingAndTestingPurposesAndPutThemInDifferentArrayLists();

		
		
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
		
			listOfTrainingData= new ArrayList<double[]>();
			listOfTrainingClassifications= new ArrayList<Integer>();
		
			listOfTestData= new ArrayList<double[]>();
			listOfTestClassifications= new ArrayList<Integer>();
			
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
	
	public static void randomlySelectDataForTrainingAndTestingPurposesAndPutThemInDifferentArrayLists()
	{
		int eightyPercentOfData= (int) (listOfTestData.size()*.8);
		
		for(int i=0; i<eightyPercentOfData; i++)
		{
			int toRemove= (int) (Math.random()*listOfTestData.size());
			listOfTrainingData.add(listOfTestData.remove(toRemove));
			listOfTrainingClassifications.add(listOfTestClassifications.remove(toRemove));
		}
		System.out.println("Testing Data Size: " + listOfTestData.size() + "\tTesting Classification Size: " + listOfTestClassifications.size() + "\tThese should be equal.");
		System.out.println("Training Data Size: " + listOfTrainingData.size() + "\tTraining Classification Size: " + listOfTrainingClassifications.size() + "\tThese should be equal.");
	}

	public static double sigmoid(double inp){   //the sigmoid function
	return 1 / (1+ Math.exp(0-inp));	// this will probably need to be scaled
	}

	public static void initWeights(boolean rand){
		weights = new double[numLayers][hiddenNeurons][hiddenNeurons]; //yes, this is a memory hog.
		double fixed = 1/hiddenNeurons; // this would have the first weights basically cause each neuron
		// to return an average of its inputs
		for(int cl=1; cl<numLayers; cl++){
			for(int x=0; x<hiddenNeurons; x++){
				for(int y=0; y<hiddenNeurons; y++){
					if(rand){
						weights[cl][x][y] = Math.random();
					}
					else{
						weights[cl][x][y]=fixed;
					}
				}
			}
		}
	}

		
	public static void think(double[] input){ // perform a run of the neural network on one datapoint. this will update network
		int dimension = input.length;
		neurons = new double[numLayers][hiddenNeurons];
		for(int i=0; i<dimension; i++){ //populate the input layer
			neurons[0][i] = sigmoid(input[i]);  //clamp everything to 0-1
		}
		
		double currentSum=0; //this will store the sum
		int dim=dimension; //temp variable
		for(int cl = 1; cl<numLayers-1; cl++){
			if(cl>1){dim = hiddenNeurons;} //avoid null pointers (in case there are fewer dimensions in the input than neurons per hidden layer)
			for(int y=0; y<dim; y++){ //all neurons of the current layer
				currentSum=0; //reset the sum for each neuron
				for(int x=0; x<hiddenNeurons; x++){ // add up all neurons of the previous layer
				currentSum+=neurons[cl-1][x]*weights[cl][x][y];
				}
				neurons[cl][y]=sigmoid(currentSum); //clamp the number to 0-1
			}
		}
		
		//just a little different for the last layer
		for(int y=0; y<numClasses; y++){ //the output layer
			currentSum=0;
			for(int x=0; x<hiddenNeurons; x++){ // add up all neurons of the previous layer
				currentSum+=neurons[numLayers-1][x]*weights[numLayers][x][y];
			}
			neurons[numLayers][y]=sigmoid(currentSum);
		}
	}
}