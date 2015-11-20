package cz.muni.neural.network;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import cz.muni.neural.network.model.LabeledPoint;
import cz.muni.neural.network.model.Result;
import cz.muni.neural.network.util.MNISTReader;

/**
 * @author Pavol Loffay
 */
public class NeuralNetworkTest {

    @Test
    public void testOnImages() throws IOException {

        int TRAIN = 500;
        int TEST = 50;
        double ALPHA = 0.1;
        int ITER = 200;
        boolean REGULARIZE = true;
        double LAMBDA = 1;

        List<LabeledPoint> trainPoints = MNISTReader.read(TestUtils.IMAGES_TRAIN_PATH,
                TestUtils.LABELS_TRAIN_PATH, TRAIN);
        int features = trainPoints.get(0).getFeatures().length;

        /**
         * train
         */
        NeuralNetwork network = NeuralNetwork.newBuilder()
                .withGradientAlpha(ALPHA)
                .withGradientIterations(ITER)
                .withRegularize(REGULARIZE)
                .withRegularizeLambda(LAMBDA)
                .withInputLayer(features)
                .addLayer(30)
                .addLastLayer(10);

//
        network.train(trainPoints);

        /**
         * test
         */
        List<LabeledPoint> testPoints = MNISTReader.read(TestUtils.IMAGES_TRAIN_PATH,
                TestUtils.LABELS_TRAIN_PATH, TEST);

        int ok = 0;
        for (LabeledPoint labeledPoint: testPoints) {

            Result result = network.predict(labeledPoint);

            System.out.println(result);
            System.out.println("Label = " + labeledPoint.getLabel() + " predicted = " + result.getMaxIndex());
            if (labeledPoint.getLabel() == result.getMaxIndex()) {
                ok++;
            }
        }

        System.out.println("\n\nSuccessfully predicted = " + ok);
        System.out.println("Test examples = " + testPoints.size());
        System.out.println("Success = " + (ok / (double)testPoints.size()) * 100D + "%");
    }
}
