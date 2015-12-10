package cz.muni.neural.network;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cz.muni.neural.network.model.LabeledPoint;
import cz.muni.neural.network.model.Result;
import cz.muni.neural.network.util.CSVReader;
import cz.muni.neural.network.util.TestUtils;
import cz.muni.neural.network.util.Utils;

/**
 * @author Pavol Loffay
 */
@Ignore
public class TimeSeriesPredictionTest {

    @Test
    public void testOnCSVPrediction() throws IOException {

        int TRAIN = 500000;
        int TEST = 500000;
        double ALPHA = 0.05;
        int ITER = 500;
        boolean REGULARIZE = true;
        double LAMBDA = 1;

        List<LabeledPoint> trainPoints = CSVReader.read(TestUtils.CSV_TRAIN_PATH, ";", TRAIN, false);
        int features = trainPoints.get(0).getFeatures().length;

        NeuralNetwork network = NeuralNetwork.newBuilder()
                .withGradientAlpha(ALPHA)
                .withGradientIterations(ITER)
                .withRegularize(REGULARIZE)
                .withRegularizeLambda(LAMBDA)
                .withInputLayer(features)
                .addLayer(30)
                .addLastLayer(1);

        /**
         * train
         */
        network.train(trainPoints);

        /**
         * test
         */
        List<LabeledPoint> testPoints = CSVReader.read(TestUtils.CSV_TEST_PATH, ";", TEST, false);


        double[] labels = new double[testPoints.size()];
        double[] predictions = new double[testPoints.size()];
        for (int i = 0; i < testPoints.size(); i++) {

            LabeledPoint labeledPoint = testPoints.get(i);
            Result result = network.predict(labeledPoint);

            System.out.println(result);
            System.out.println("Label = " + labeledPoint.getLabel() + " predicted = " + result.getData()[0]);
            labels[i] = labeledPoint.getLabel();
            predictions[i] = result.getData()[0];
        }

        Double rmse = Utils.rmse(labels, predictions);

        try {
            CSVReader.write(TestUtils.CSV_RESULTS_PATH, ";", labels, predictions);
        } catch (Exception e) {
            System.out.println("Result file writing failed.");
        }

        System.out.println("Test examples = " + testPoints.size());
        System.out.println("RMSE = " + rmse);

        assertThat(rmse,  is(lessThanOrEqualTo(new Double(0.1))));
    }

    @Test
    public void testOnCSVClassification() throws IOException {

        int TRAIN = 999999;
        int TEST = 999999;
        double ALPHA = 0.5;
        int ITER = 200;
        boolean REGULARIZE = true;
        double LAMBDA = 1;

        List<LabeledPoint> trainPoints = CSVReader.read(TestUtils.CSV_CLASS_TRAIN_PATH, ";", TRAIN, false);
        int features = trainPoints.get(0).getFeatures().length;

        NeuralNetwork network = NeuralNetwork.newBuilder()
                .withGradientAlpha(ALPHA)
                .withGradientIterations(ITER)
                .withRegularize(REGULARIZE)
                .withRegularizeLambda(LAMBDA)
                .withInputLayer(features)
                .addLayer(30)
                .addLastLayer(3);

        /**
         * train
         */
        network.train(trainPoints);

        /**
         * test
         */
        List<LabeledPoint> testPoints = CSVReader.read(TestUtils.CSV_CLASS_TEST_PATH, ";", TEST, false);


        int ok = 0;
        for (LabeledPoint labeledPoint: testPoints) {

            Result result = network.predict(labeledPoint);

            System.out.println(result);
            System.out.println("Label = " + labeledPoint.getLabel() + " predicted = " + result.getMaxIndex());
            if (labeledPoint.getLabel() == result.getMaxIndex()) {
                ok++;
            }
        }

        Double success = (ok / (double)testPoints.size()) * 100D;

        System.out.println("\n\nSuccessfully predicted = " + ok);
        System.out.println("Test examples = " + testPoints.size());
        System.out.println("Success = " + success + "%");

        assertThat(success,  is(greaterThanOrEqualTo(new Double(70))));
    }
}