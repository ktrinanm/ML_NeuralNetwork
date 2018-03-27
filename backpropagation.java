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

    public static int numLines=569;




    //start myStuff
    public static int trainingReps = 100000; //will be used when running mine and joes code.
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

        //Here's a title that's a little more descriptive.
        randomlySelectDataForTrainingAndTestingPurposesAndPutThemInDifferentArrayLists();


        //example useage of the think method and init method

        //init the variables
        numLayers = 2; //0=input, 1=hidden, 2=output
        hiddenNeurons = 6; //an arbitrary number i picked
        numInputs = 10;
        numClasses = 2; //technically 2, since zero counts
        learningRate = .02; //ditto
        sigmoidScale = .5 ; // scale the input to the sigmoid function
        int currentSample=1;


        initWeights(true); // true means it will be random


        int sample = 0;
        for (int i = 0; i < trainingReps; i++){
            sample ++;
            if (sample == listOfTrainingData.size()){
                sample = 0;
            }
            think(listOfTrainingData.get(sample));//put the first data point through the network
            backPropagate(sample);//put the first datapoint through backPropagate
            //System.out.println(Arrays.toString(weights[numLayers][1]));
            //System.out.println(Arrays.toString(neurons[numLayers]));
            //System.out.println(classification[sample]);

        }

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




        double guess;
        double real;
        double difference;

        int begninRight = 0;
        int begninWrong = 0;
        int malignantRight = 0;
        int malignantWrong = 0;


        int errors = 0;
        int correct= 0;
        for (int i = 1; i < listOfTestData.size(); i++){
            think(listOfTestData.get(i));
            if (neurons[numLayers][0] > neurons[numLayers][1]){
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
        System.out.println("Benign weight: "+neurons[numLayers][0]);
        System.out.println("Malignant weight: "+neurons[numLayers][1]);
        System.out.println("Output error:");
        if(listOfTestClassifications.get(1)==0){ //if the tumor is benign
            System.out.println(neurons[numLayers][0]-1);  //this should be 1, show the difference
            System.out.println(neurons[numLayers][1]);    //this should be 0, show the difference
        }
        else{ //if the tumor is malignant
            System.out.println(neurons[numLayers][0]);    //this should be 0, show the difference
            System.out.println(neurons[numLayers][1]-1);  //this should be 1, show the difference
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
            System.out.println(line);
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
                for(int y=0; y<hiddenNeurons; y++){
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
            for(int y=0; y<numClasses; y++){
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
        int dimension = input.length;

        for (int i = 0; i < dimension; i++) { //populate the input layer
            neurons[0][i] = input[i];  //clamp everything to 0-1
        }

        double currentSum = 0; //this will store the sum
        int dim = dimension; //temp variable
        for (int cl = 1; cl <= numLayers - 1; cl++) {
            if (cl > 1) {
                dim = hiddenNeurons;
            } //avoid null pointers (in case there are fewer dimensions in the input than neurons per hidden layer)
            for (int y = 0; y < dim; y++) { //all neurons of the current layer
                currentSum = 0; //reset the sum for each neuron
                for (int x = 0; x < numInputs; x++) { // add up all neurons of the previous layer
                    currentSum += neurons[cl - 1][x] * weights[cl][x][y];
                }
                neurons[cl][y] = sigmoid(currentSum); //clamp the number to 0-1
            }
        }

        //just a little different for the last layer
        for (int y = 0; y < numClasses; y++) { //the output layer
            currentSum = 0;
            for (int x = 0; x < hiddenNeurons; x++) { // add up all neurons of the previous layer
                currentSum += neurons[numLayers - 1][x] * weights[numLayers][x][y];
            }
            neurons[numLayers][y] = sigmoid(currentSum);
        }
    }


    //BACKPROBAGATE
    private static void backPropagate(int n) { //n is the datapoint index we are working on
        int INPUT_NEURONS = numInputs;
        int HIDDEN_NEURONS = hiddenNeurons; //UNKNOWN
        int OUTPUT_NEURONS = numClasses;
        learningRate = .01;

        //activations
        double[] x = neurons[0];
        double z[] = new double[hiddenNeurons];
        double y[] = new double[numClasses]; //actual array is given by outside method for random weights to start.

        // Unit errors.
        double erro[] = new double[numClasses]; // erro array is the array that stores all of the changes that need to be made to the weights attached to an output node
        double errh[] = new double[hiddenNeurons]; // errh array is the array that stores the changes that need to be made between hidden layers; again this will need to be changed depending on how we handle hidden layers.

        int[] r = new int[2]; //Target is the expected output neuron for each data set.
        z = neurons[1];

        if (listOfTrainingClassifications.get(n) == 1){
            r[0] = 0; // node 0 = 0, node 1 = 1
            r[1] = 1;
        } else {
            r[0] = 1; // node 0 = 1, node 1 = 0
            r[1] = 0;
        }
        //System.out.println(Arrays.toString(x) + ": " +Arrays.toString(r));
        y=neurons[numLayers]; //the value of the actual output neurons

        //layer is the current layer, weather it be the input layer of weights or the hidden layer of weights
        int layer = 0;//for now layer will be 0, or the input layer, because i'm not sure how our layers are working.
        double[][] w = weights[layer+1]; //wih is weights between input and hidden layers.
        double[][] v = weights[(weights.length-1)]; //who is weights of hidden to output
        double deltaW[][] = new double[numInputs+1][hiddenNeurons]; //+1 for bias
        double deltaV[][] = new double[hiddenNeurons+1][numClasses]; //+1 for bias

        //Calculate z(hidden layer neuron values)
        double sum = 0.0;
        for (int h = 0; h < INPUT_NEURONS; h++){
            double s = vectorTransposeMultiplication(weights[1][h], x);
            if(h>6) {
                z[h] = 0.0;
            } else {
                z[h] = sigmoid(s);
            }
        }

        //Calculate y(output layer neuron values)
        for (int i = 1; i < OUTPUT_NEURONS; i++){
            y[i] = vectorTransposeMultiplication(v[i], z);

        }

        //Find Change in v weights (weights[numLayers])
        for (int i = 0; i < OUTPUT_NEURONS; i++){
            double s = learningRate*(r[i] - y[i]);
            for (int h = 0; h  < HIDDEN_NEURONS; h++){
                deltaV[h][i] = z[h]*s; //vectorMultiplicationWithScalar(z, s);
                //System.out.println("DELTA V["+h+"]["+i+"]: " + deltaV[h][i]);
            }
        }
        //System.out.println(Arrays.deepToString(deltaV));

        //Find Change in w weights (weights[1])
        for (int h = 0; h < HIDDEN_NEURONS; h++){
             for (int i = 0; i < OUTPUT_NEURONS; i++){ //get the sum of (r[i]-y[i])v[i][h]
                 sum += (r[i] - y[i])*v[h][i];
             }
             //System.out.println(sum);
             //System.out.println(z[h]);
             double s = learningRate*sum*sigmoidDerivative(z[h]);
             for(int input = 0; input < numInputs; input++){
                //deltaW[h] = vectorMultiplicationWithScalar(x, s); //deltaW[h] = [s*x[i]]
                deltaW[input][h] = x[input]*s;
                //System.out.println("DELTA W["+input+"]["+h+"]: " + deltaW[input][h] + ": " +z[h]);
             }
            sum = 0;
            //System.out.println(Arrays.toString(deltaW[h]));
        }

        //Update v weights (weights[numLayers])
        for (int i = 0; i < OUTPUT_NEURONS; i++){
            for(int h = 0; h < HIDDEN_NEURONS; h++){
                //v[i] = addVectors(v[i], deltaV[i]);
                v[h][i] = v[h][i] + deltaV[h][i];
                //System.out.println("V["+h+"]["+i+"]: " + v[h][i]);
            }

        }
        //Update w weights (weights[1])
        for (int h = 0; h < HIDDEN_NEURONS; h++){
            //w[h] = addVectors(w[h], deltaW[h]);
            for (int input = 0; input < numInputs; input++){
                w[input][h] = w[input][h] + deltaW[input][h];
            }
        }

        weights[layer+1] = w; //actually update the weights
        weights[weights.length-1] = v; //actually update the



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
        if (A.length != B.length){
            System.out.println("ERRROOOORRRROOORRROOOORR");
        }
        double result = 0.0;
        for (int i = 0; i < A.length; i++){
            result += (A[i]*B[i]);
        }
        return result;
    }

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

}