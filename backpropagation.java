import java.util.*;
import java.lang.Math;


public class backpropagation{
	private static final int INPUT_NEURONS = 10;
    private static final int HIDDEN_NEURONS = 4; //UNKNOWN
    private static final int OUTPUT_NEURONS = 2;
    private static final int TRAINING_REPS = 10000;

    // Input to Hidden Weights (with Biases).
    private static double wih[][] = new double[INPUT_NEURONS + 1][HIDDEN_NEURONS];

    // Hidden to Output Weights (with Biases).
    private static double who[][] = new double[HIDDEN_NEURONS + 1][OUTPUT_NEURONS];

    // Activations.
    private static double inputs[] = new double[INPUT_NEURONS]; //not sure where to find our input nodes in our code, I think its in the listOfTrainingData?
    private static double hidden[] = new double[HIDDEN_NEURONS];
    private static double target[] = new double[OUTPUT_NEURONS];
    private static double actual[] = new double[OUTPUT_NEURONS];


   // private static final int MAX_SAMPLES = 14;

    //This is the group code
    public static int [] classification;
    public static double [] radii, textures, perimeters, area, smoothness,
            compactness, concavity, concavePnts, symmetry, fractalDim;
    public static ArrayList<double[]> listOfTrainingData;
    public static ArrayList<Integer> listOfTrainingClassifications;

    public static double[][] neurons;  //store the state of each neuron (or input and output node)
    public static double[][][] weights;//stores what is says on the tin [numLayers][hiddenNeurons][hiddenNeurons]
    public static int numLayers; //number of layers, first always input, end always output, middle hidden
    public static int hiddenNeurons; //number of nodes in each hidden layer, probably should be per layer, but whatever
    public static int numClasses; //number of clasifications, futureproofing
    public static double learningRate; //the magic learning rate aka alpha

    public static ArrayList<double[]> listOfTestData;
    public static ArrayList<Integer> listOfTestClassifications;

    public static int numLines=569;


	private static void backPropagate()
    {
        private static final int INPUT_NEURONS = 10;
        private static final int HIDDEN_NEURONS = hiddenNeurons; //UNKNOWN
        private static final int OUTPUT_NEURONS = numClasses;
        learningRate = 0.2;

        // Unit errors.
        private static double erro[] = new double[OUTPUT_NEURONS]; // erro array is the array that stores all of the changes that need to be made to the weights attached to an output node
        private static double errh[] = new double[HIDDEN_NEURONS]; // errh array is the array that stores the changes that need to be made between hidden layers; again this will need to be changed depending on how we handle hidden layers.

        //target

        private static double targets[][] = new double[classifications.size()]; //targets can be the source for target[]
        //[[cl0,!cl0],....,[cln,!cln]] for all classifications, changes for each iteration of backprop.
        //target array is given by outside method that runs the backPropagate() method, contains weights for each node in the hidden layer to the output [0,1] or [1,0]
        //actual array is given by outside method for random weights to start.



        //layer is the current layer, weather it be the input layer of weights or the hidden layer of weights
        int layer = 0;//for now layer will be 0, or the input layer, because i'm not sure how our layers are working.
        whi = weights[layer]; //whi is weights between input and hidden layers.
        who = weights[weights.size()-1]; //who is weights of hidden to output

        for (int i = 0; i < classification.size(); i++){
            if (classifications[i] == 1){
                targets[i][0] = 1;
                targets[i][1] = 0;
            } else if (classification[i] == 0){
                targets[i][0] = 0;
                targets[i][1] = 1;
            }
        }

        // Calculate the output layer error
        for(int out = 0; out < OUTPUT_NEURONS; out++) {
            erro[out] = (target[out] - actual[out]) * sigmoidDerivative(actual[out]);
        }

        // Calculate the hidden layer error
        for(int h = 0; h < HIDDEN_NEURONS; h++) //(for h = 1, ..., H)
        {
            errh[h] = 0.0;
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
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
        return;
    }
	
	private static double sigmoid(final double val){
        return (1.0 / (1.0 + Math.exp(-val)));
    }

    private static double sigmoidDerivative(final double val){
        return (val * (1.0 - val));
    }
}