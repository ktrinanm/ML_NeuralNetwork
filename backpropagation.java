import java.util.*;
import java.lang.Math;


public class backpropagation{
	private static final int INPUT_NEURONS = 4;
    private static final int HIDDEN_NEURONS = 6;
    private static final int OUTPUT_NEURONS = 14;

    private static final double LEARN_RATE = 0.2;    // Rho.
    private static final double NOISE_FACTOR = 0.45;
    private static final int TRAINING_REPS = 10000;

    // Input to Hidden Weights (with Biases).
    private static double wih[][] = new double[INPUT_NEURONS + 1][HIDDEN_NEURONS];

    // Hidden to Output Weights (with Biases).
    private static double who[][] = new double[HIDDEN_NEURONS + 1][OUTPUT_NEURONS];

    // Activations.
    private static double inputs[] = new double[INPUT_NEURONS];
    private static double hidden[] = new double[HIDDEN_NEURONS];
    private static double target[] = new double[OUTPUT_NEURONS];
    private static double actual[] = new double[OUTPUT_NEURONS];

    // Unit errors.
    private static double erro[] = new double[OUTPUT_NEURONS];
    private static double errh[] = new double[HIDDEN_NEURONS];

    private static final int MAX_SAMPLES = 14;

    private static int trainInputs[][] = new int[][] {{1, 1, 1, 0}, 
                                                      {1, 1, 0, 0}, 
                                                      {0, 1, 1, 0}, 
                                                      {1, 0, 1, 0}, 
                                                      {1, 0, 0, 0}, 
                                                      {0, 1, 0, 0}, 
                                                      {0, 0, 1, 0}, 
                                                      {1, 1, 1, 1}, 
                                                      {1, 1, 0, 1}, 
                                                      {0, 1, 1, 1}, 
                                                      {1, 0, 1, 1}, 
                                                      {1, 0, 0, 1}, 
                                                      {0, 1, 0, 1}, 
                                                      {0, 0, 1, 1}};

    private static int trainOutput[][] = new int[][] 
                                        {{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, 
                                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}};
	
	
	private static void backPropagate()
    {
        // Calculate the output layer error (step 3 for output cell).
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            erro[out] = (target[out] - actual[out]) * sigmoidDerivative(actual[out]);
        }

        // Calculate the hidden layer error (step 3 for hidden cell).
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            errh[hid] = 0.0;
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
                errh[hid] += erro[out] * who[hid][out];
            }
            errh[hid] *= sigmoidDerivative(hidden[hid]);
        }

        // Update the weights for the output layer (step 4).
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                who[hid][out] += (LEARN_RATE * erro[out] * hidden[hid]);
            } // hid
            who[HIDDEN_NEURONS][out] += (LEARN_RATE * erro[out]); // Update the bias.
        } // out

        // Update the weights for the hidden layer (step 4).
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            for(int inp = 0; inp < INPUT_NEURONS; inp++)
            {
                wih[inp][hid] += (LEARN_RATE * errh[hid] * inputs[inp]);
            } // inp
            wih[INPUT_NEURONS][hid] += (LEARN_RATE * errh[hid]); // Update the bias.
        } // hid
        return;
    }
	
	private static double sigmoid(final double val)
    {
        return (1.0 / (1.0 + Math.exp(-val)));
    }

    private static double sigmoidDerivative(final double val)
    {
        return (val * (1.0 - val));
    }
}