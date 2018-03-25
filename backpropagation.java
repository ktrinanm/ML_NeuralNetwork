import java.io.*;
import java.util.*;
import java.lang.Integer;

public class ClassificationNeuralNetwork {
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
    public static int trainingReps = 20; //will be used when running mine and joes code.
    int maxSamples = numLines;

    //end myStuff


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
        learningRate = .2; //ditto
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
        }


        for (int i = 1; i <= numLayers; i++){
            for (int j = 0; j<= 10; j++) {
                System.out.println(j + ": " + Arrays.toString(weights[i][j]));
            }
            System.out.println();
        }

        //Test the first data point
        think(listOfTestData.get(1));
        System.out.println(Arrays.toString(neurons[2]));
        System.out.println(listOfTestClassifications.get(1));



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
        return 1 / (1+ Math.exp(0-inp*sigmoidScale));	// this will probably need to be scaled
    }

    public static void initWeights(boolean rand){
        weights = new double[numLayers+1][numInputs+1][numInputs+1]; //yes, this is a memory hog.
        double fixed = 1/hiddenNeurons; // this would have the first weights basically cause each neuron
        // to return an average of its inputs
        for(int cl=1; cl<=numLayers-1; cl++){
            for(int x=0; x<numInputs; x++){
                for(int y=0; y<hiddenNeurons; y++){
                    if(rand){
                        weights[cl][x][y] = Math.random()*2-1;
                    }
                    else{
                        weights[cl][x][y]=fixed;
                    }
                }
            }
        }

        for(int x=0; x<numInputs; x++){
            for(int y=0; y<numClasses; y++){
                if(rand){
                    weights[2][x][y] = Math.random()*2-1;
                }
                else{
                    weights[2][x][y]=fixed;
                }
            }
        }

    }


    public static void think(double[] input) { // perform a run of the neural network on one datapoint. this will update network
        int dimension = input.length;
        neurons = new double[numLayers + 1][dimension + 1];
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
                for (int x = 0; x < hiddenNeurons; x++) { // add up all neurons of the previous layer
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
        learningRate = 0.2;

        //activations
        double[] inputs = neurons[0];
        double hidden[] = new double[hiddenNeurons];
        double actual[] = new double[numClasses]; //actual array is given by outside method for random weights to start.

        // Unit errors.
        double erro[] = new double[numClasses]; // erro array is the array that stores all of the changes that need to be made to the weights attached to an output node
        double errh[] = new double[hiddenNeurons]; // errh array is the array that stores the changes that need to be made between hidden layers; again this will need to be changed depending on how we handle hidden layers.


        //creating viable targets directory for target
        double targets[][] = new double[numLines][numClasses]; //targets can be the source for target[]
        //[[c0,!c0],....,[cn,!cn]] for all classifications, changes for each iteration of backprop.

        for (int i=0; i<numLines; i++){
            if (classification[i] == 1){
                targets[i][0] = 1;
                targets[i][1] = 0;
            } else if (classification[i] == 0){
                targets[i][0] = 0;
                targets[i][1] = 1;
            }
        }

        int[] target = new int[2]; //Target is the expected output neuron for each data set.
        hidden = neurons[1];
        // ??????????????????
        if (classification[n] == 1){
            target[0] = 0; // node 0 = 0, node 1 = 1
            target[1] = 1;
        } else {
            target[0] = 1; // node 0 = 1, node 1 = 0
            target[1] = 0;
        }
        actual=neurons[2]; //the value of the actual output neurons
        System.out.println(Arrays.toString(actual));
        System.out.println(Arrays.toString(target));

        //layer is the current layer, weather it be the input layer of weights or the hidden layer of weights
        int layer = 0;//for now layer will be 0, or the input layer, because i'm not sure how our layers are working.
        double[][] wih = weights[layer+1]; //whi is weights between input and hidden layers.
        double[][] who = weights[(weights.length-1)]; //who is weights of hidden to output

        // Calculate the output layer error
        for(int out = 0; out < OUTPUT_NEURONS; out++) {
            erro[out] = (target[out] - actual[out]) * sigmoidDerivative(actual[out]);
        }
        // Calculate the hidden layer error
        for(int h = 1; h < HIDDEN_NEURONS; h++) //(for h = 1, ..., H)
        {
            errh[h] = 0.0;
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
                //System.out.println(who[h][out]);
                errh[h] += erro[out] * who[h][out];
            }
            errh[h] *= sigmoidDerivative(hidden[h]);
        }
        // Update the weights for the output layer
        for(int out = 0; out < OUTPUT_NEURONS; out++) // for i = 1, ... ,K
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                who[hid][out] += (learningRate * erro[out] * hidden[hid]);
            } // hid
            who[HIDDEN_NEURONS][out] += (learningRate * erro[out]); // Update the bias.
        } // out
        // Update the weights for the hidden layer
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            for(int inp = 0; inp < INPUT_NEURONS; inp++)
            {
                wih[inp][hid] += (learningRate * errh[hid] * inputs[inp]);
            } // inp
            wih[INPUT_NEURONS][hid] += (learningRate * errh[hid]); // Update the bias.
        } // hid

        weights[layer+1] = wih; //actually update the weights
        weights[weights.length-1] = who; //actually update the
        return;
    }

    private static double sigmoidDerivative(final double val){
        return (val * (1.0 - val));
    }





}