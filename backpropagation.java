import java.io.*;
import java.util.*;
import java.lang.Integer;

public class backpropagation {
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
    public static double sigmoidScale; //scale the input of the sigmoid function
    public static ArrayList<double[]> listOfTestData;
    public static ArrayList<Integer> listOfTestClassifications;
    public static double[] layerBias;
    public static int numInputs;
    public static double[] scaleInput; //scale and offset such that all input data is in the range 0-1
    public static double[] offsetInput;

    public static int numLines=569;




    //start myStuff
    public static int trainingReps = 1000 * 455; //will be used when running mine and joes code.
    int maxSamples = numLines;

    //end myStuff


    public static void main(String [] args)
    {
        classification = new int[numLines];
        sigmoidScale=1;
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

        readDataFromFile();
        putDataInArrayList();
        prescale(listOfTestData);
                //Here's a title that's a little more descriptive.
        randomlySelectDataForTrainingAndTestingPurposesAndPutThemInDifferentArrayLists();
        ArrayList<double[]> temp = scaleAllTheThings(listOfTestData);
        listOfTestData = temp;
        //example useage of the think method and init method

        //init the variables
        numLayers = 2; //0=input, 1=hidden, 2=output
        hiddenNeurons = 6; //an arbitrary number i picked
        numInputs = 10;
        numClasses = 1; //technically 2, since zero counts
        learningRate = .0001; //ditto
        //sigmoidScale = .5 ; // scale the input to the sigmoid function
        //int currentSample=1;


        initWeights(true); // true means it will be random


        int sample = 0;
        for (int i = 0; i < trainingReps; i++){
            sample ++;
            if (sample == listOfTrainingData.size()){
                sample = 0;
            }
            think(listOfTrainingData.get(sample));//put the first data point through the network
            backPropagate(sample);//put the first datapoint through backPropagate

        }
/*
        System.out.println();
        for (int i = 1; i <= numLayers; i++){
            for (int j = 0; j<= 10; j++) {
                System.out.println(j + ": " + Arrays.toString(weights[i][j]));
            }
            System.out.println();
        }
        //Test the first data point
        think(listOfTestData.get(1));
        System.out.println(Arrays.toString(neurons[numLayers]));
        System.out.println(listOfTestClassifications.get(1));

        think(listOfTestData.get(3));
        System.out.println(Arrays.toString(neurons[numLayers]));
        System.out.println(listOfTestClassifications.get(3));
*/



        double guess;
        double real;
        //double difference;

        int begninRight = 0;
        int begninWrong = 0;
        int malignantRight = 0;
        int malignantWrong = 0;


        int errors = 0;
        int correct= 0;
        for (int i = 0; i < listOfTestData.size(); i++){
            think(listOfTestData.get(i));
            if (neurons[numLayers][0] < .5){
                guess = 0;
            } else {
                guess = 1;
            }
            real = listOfTestClassifications.get(i);

            if (real == guess){
                correct++;
                if (real == 0){
                    begninRight++;
                } else {
                    malignantRight++;
                }
            }else{
                errors++;
                if (real == 0){
                    begninWrong++;
                } else {
                    malignantWrong++;
                }
            }
        }

        System.out.println("Correctly identified Benign: " + begninRight);
        System.out.println("Incorrectly identified Benign: " + begninWrong);
        System.out.println("Correctly identified Malignant: " + malignantRight);
        System.out.println("Incorrectly identified Malignant: " + malignantWrong);
        System.out.println("Errors: " + errors);
        System.out.println("Correct: " +correct);

        //	print the error (diffenence between the output state and the actual classification)
        //System.out.println("Output weight: "+neurons[numLayers][0]);
        //System.out.println("Malignant weight: "+neurons[numLayers][1]);
        //System.out.println("Output error:");
        if(listOfTestClassifications.get(1)==0){ //if the tumor is benign
            System.out.println(neurons[numLayers][0]-1);  //this should be 1, show the difference
            //System.out.println(neurons[numLayers][1]);    //this should be 0, show the difference
        }
        else{ //if the tumor is malignant
            System.out.println(neurons[numLayers][0]);    //this should be 0, show the difference
            //System.out.println(neurons[numLayers][1]-1);  //this should be 1, show the difference
        }

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

            while((line = bufferedReader.readLine()) != null)
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
			return;
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
/*
        System.out.println("Testing Data Size: " + listOfTestData.size() + "\tTesting Classification Size: " + listOfTestClassifications.size() + "\tThese should be equal.");
        System.out.println("Training Data Size: " + listOfTrainingData.size() + "\tTraining Classification Size: " + listOfTrainingClassifications.size() + "\tThese should be equal.");
*/
    }

    public static double sigmoid(double inp){   //the sigmoid function
        return 1.0 / (1.0+ Math.exp(0-inp*sigmoidScale));	// this will probably need to be scaled
    }

    public static void initWeights(boolean rand){
        int dimension = listOfTestData.get(0).length;
        weights = new double[numLayers+1][numInputs+1][numInputs+1]; //yes, this is a memory hog.
        neurons = new double[numLayers + 1][dimension + 1];
        double fixed = 1/hiddenNeurons; // this would have the first weights basically cause each neuron
        // to return an average of its inputs

        //initializes random weights for everything
        for(int cl=1; cl<=numLayers-1; cl++){
            for(int x=0; x<numInputs+1; x++){
                for(int y=0; y<hiddenNeurons+1; y++){
                    if(rand){
                        weights[cl][x][y] = Math.random()*2-1;
                    }
                    else{
                        weights[cl][x][y]=fixed;
                    }
                }
            }

            neurons[cl][hiddenNeurons+1]=1; // this is the bias
        }
        neurons[0][numInputs]=1;
        //initializes random weights for output
        for(int x=0; x<hiddenNeurons; x++){
            for(int y=0; y<1; y++){
                if(rand){
                    weights[numLayers][x][y] = Math.random()*2-1;
                }
                else{
                    weights[numLayers][x][y]=fixed;
                }
            }
        }

    }


    public static void think(double[] input) { // perform a run of the neural network on one datapoint. this will update network
        //int dimension = input.length;

        for (int i = 0; i < input.length; i++) { //populate the input layer
            neurons[0][i] = input[i];  //clamp everything to 0-1
        }
		neurons[0][numInputs] = 1;

        double currentSum = 0; //this will store the sum
        //int dim = dimension; //temp variable
        for (int cl = 1; cl <= numLayers - 1; cl++) {
            //if (cl > 1) {
                //dim = hiddenNeurons + 1;
            //} //avoid null pointers (in case there are fewer dimensions in the input than neurons per hidden layer)
            for (int y = 0; y < hiddenNeurons + 1; y++) { //all neurons of the current layer
                currentSum = 0; //reset the sum for each neuron
                for (int x = 0; x < numInputs + 1; x++) { // add up all neurons of the previous layer
                    currentSum += neurons[cl - 1][x] * weights[cl][x][y];
                }
                neurons[cl][y] = currentSum; //clamp the number to 0-1
            }
        }

        //just a little different for the last layer
        //for (int y = 0; y < numClasses; y++) { //the output layer
		currentSum = 0;
		for (int x = 0; x < hiddenNeurons + 1; x++) { // add up all neurons of the previous layer
			currentSum += sigmoid(neurons[numLayers - 1][x]) * weights[numLayers][x][0];
		}
		currentSum += weights[numLayers][hiddenNeurons][0];
		neurons[numLayers][0] = currentSum;
        //}
    }


    //BACKPROBAGATE
    private static void backPropagate(int n) { //n is the datapoint index we are working on
        int INPUT_NEURONS = numInputs;
        int HIDDEN_NEURONS = hiddenNeurons; //UNKNOWN
        int OUTPUT_NEURONS = 1;

        //activations
        double[] x = neurons[0];
        double z[] = new double[hiddenNeurons+1]; // Hidden layer
		double zOut[] = new double[hiddenNeurons+1];
        double y[] = new double[numClasses]; //actual array is given by outside method for random weights to start.

        // Unit errors.
        //double erro[] = new double[numClasses]; // erro array is the array that stores all of the changes that need to be made to the weights attached to an output node
        //double errh[] = new double[hiddenNeurons]; // errh array is the array that stores the changes that need to be made between hidden layers; again this will need to be changed depending on how we handle hidden layers.

        z = neurons[1];

/*
		for(int i = 0; i < x.length; i++)
		{
			System.out.println(x[i]);
		}
*/

        double r = listOfTrainingClassifications.get(n);

		//System.out.println(r);
        
		//System.out.println(Arrays.toString(x) + ": " +Arrays.toString(r));
        y=neurons[numLayers]; //the value of the actual output neurons

        //layer is the current layer, weather it be the input layer of weights or the hidden layer of weights
        int layer = 0;//for now layer will be 0, or the input layer, because i'm not sure how our layers are working.
        double[][] w = vectorTranspose(weights[layer+1]); //wih is weights between input and hidden layers.
        double[][] v = vectorTranspose(weights[(weights.length-1)]); //who is weights of hidden to output
        double deltaW[][] = new double[hiddenNeurons+1][INPUT_NEURONS+1]; //+1 for bias
        double deltaV[][] = new double[numClasses][HIDDEN_NEURONS + 1]; //+1 for bias

        //Calculate z(hidden layer neuron values)
        //double sum = 0.0;
        for (int h = 0; h < HIDDEN_NEURONS; h++){
            double s = vectorTransposeMultiplication(w[h], x);
            /*if(h>6) {
                z[h] = 0.0; 
            } else { */
                z[h] = s;
				zOut[h] = sigmoid(s);
            //}
        }
		zOut[HIDDEN_NEURONS] = 1;

        //Calculate y(output layer neuron values)
        for (int i = 0; i < OUTPUT_NEURONS; i++){
            y[i] = vectorTransposeMultiplication(v[i], zOut);
        }

        //Find Change in v weights (weights[numLayers])
        for (int i = 0; i < OUTPUT_NEURONS; i++){
            double s = learningRate*(r - sigmoid(y[i]));
            for (int h = 0; h  < HIDDEN_NEURONS + 1; h++){
                deltaV[i][h] = zOut[h]*s; //vectorMultiplicationWithScalar(z, s);
                //System.out.println("DELTA V["+h+"]["+i+"]: " + deltaV[h][i]);
            }
        }
        //System.out.println(Arrays.deepToString(deltaV));

        //Find Change in w weights (weights[1])
        for (int h = 0; h < HIDDEN_NEURONS; h++){
             double m = (r - sigmoid(y[0]))*v[0][h];
             double s = learningRate*m*sigmoidDerivative(zOut[h]);
             for(int input = 0; input < numInputs + 1; input++){
                //deltaW[h] = vectorMultiplicationWithScalar(x, s); //deltaW[h] = [s*x[i]]
                deltaW[h][input] = x[input]*s;
             }
            //System.out.println(Arrays.toString(deltaW[h]));
        }

        //Update v weights (weights[numLayers])
        for (int i = 0; i < OUTPUT_NEURONS; i++){
            for(int h = 0; h < HIDDEN_NEURONS + 1; h++){
                //v[i] = addVectors(v[i], deltaV[i]);
                v[i][h] = v[i][h] + deltaV[i][h];
                //System.out.println("V["+h+"]["+i+"]: " + v[h][i]);
            }

        }
        //Update w weights (weights[1])
        for (int h = 0; h < HIDDEN_NEURONS; h++){
            //w[h] = addVectors(w[h], deltaW[h]);
            for (int input = 0; input < numInputs + 1; input++){
                w[h][input] = w[h][input] + deltaW[h][input];
            }
        }

        weights[layer+1] = vectorTranspose(w); //actually update the weights
        weights[weights.length-1] = vectorTranspose(v); //actually update the



        //System.out.println(Arrays.toString(y) + ": " + listOfTrainingClassifications.get(n));
        //System.out.println(Arrays.toString(weights[2][0]));
        //System.out.println(Arrays.toString(neurons[2]) + ": " + Arrays.toString(r) + ": " + listOfTrainingClassifications.get(n) + ": " + ((neurons[2][0] > neurons[2][1] && listOfTrainingClassifications.get(n) == 0) ? "SUCCESS" : "FAIL") );
        return;
    }

    private static double sigmoidDerivative(final double val){
        return (val * (1.0 - val));
    }


    //Linear Algebra Helper Functions
    private static double vectorTransposeMultiplication(double[] A, double[] B){
        /*if (A.length != B.length){
            System.out.println("ERRROOOORRRROOORRROOOORR");
        }
		*/
		int len = (A.length > B.length ? B.length : A.length);
        double result = 0.0;
        for (int i = 0; i < len; i++){
            result += (A[i]*B[i]);
        }
        return result;
    }
/*
    private static double[] vectorMultiplicationWithScalar(double[] A, double s){
        double[] result = new double[A.length];
        for (int i = 0; i < A.length; i++){
            result[i] = A[i]*s;
        }
        return result;
    }

    private static double[] addVectors(double[] A, double[] B){
        double[] result = new double[A.length];
        for (int i = 0; i < A.length; i++){
            result[i] = A[i] + B[i];
        }
        return result;
    }
*/

	private static double [][] vectorTranspose(double[][] A)
	{
		double [][] result = new double[A[0].length][A.length];

		for(int i = 0; i < A.length; i++)
		{
			for(int j = 0; j < A[0].length; j++)
			{
				result[j][i] = A[i][j];
			}
		}

		return result;
    }
    
    public static void prescale(ArrayList<double[]> input){
        int datSize = input.get(0).length;
        double[] dataMax = new double[datSize];
        double[] dataMin = new double[datSize];
        offsetInput = new double[datSize];
        scaleInput = new double[datSize];
        double[] dat;
        for(int i=0; i<datSize; i++){
            dataMax[i]=(double)Double.MIN_VALUE;
            dataMin[i]=(double)Double.MAX_VALUE;
        }
        for(int j=0; j<datSize; j++){
            dat = input.get(j);
            for(int i=0; i<datSize; i++){
                if(dat[i] < dataMin[i]){dataMin[i]=dat[i];}
                if(dat[i] > dataMax[i]){dataMax[i]=dat[i];}
            }
        }
        for(int i=0; i<datSize; i++){
            offsetInput[i] = dataMin[i];
            scaleInput[i] = 1/(dataMax[i]-dataMin[i]);
        }
    }
    public static double[] scaled(double[] input){
        double[] temp = input;
        int datSize = input.length;
        for(int i=0; i<datSize; i++){
            temp[i] = (input[i]-offsetInput[i])*scaleInput[i];
        }
        return temp;
    }
    public static ArrayList<double[]> scaleAllTheThings(ArrayList<double[]> input){
        ArrayList<double[]> temp = new ArrayList<double[]>();

      for(double[] d: input){
            temp.add(scaled(d));
        }
        return temp;
    }

}
