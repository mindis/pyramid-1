package edu.neu.ccs.pyramid.application;

import edu.neu.ccs.pyramid.configuration.Config;
import edu.neu.ccs.pyramid.dataset.DataSetType;
import edu.neu.ccs.pyramid.dataset.RegDataSet;
import edu.neu.ccs.pyramid.dataset.TRECFormat;
import edu.neu.ccs.pyramid.eval.RMSE;
import edu.neu.ccs.pyramid.regression.linear_regression.ElasticNetLinearRegOptimizer;
import edu.neu.ccs.pyramid.regression.linear_regression.LinearRegression;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Linear Regression with Elasticnet (L1+L2) regularization
 * Created by chengli on 2/25/16.
 */
public class App4 {

    public static void main(String[] args) throws Exception{
        if (args.length !=1){
            throw new IllegalArgumentException("Please specify a properties file.");
        }

        Config config = new Config(args[0]);
        System.out.println(config);

        String sparsity = config.getString("featureMatrix.sparsity").toLowerCase();
        DataSetType dataSetType = null;
        switch (sparsity){
            case "dense":
                dataSetType = DataSetType.REG_DENSE;
                break;
            case "sparse":
                dataSetType = DataSetType.REG_SPARSE;
                break;
            default:
                throw new IllegalArgumentException("featureMatrix.sparsity can be either dense or sparse");
        }

        RegDataSet trainSet = TRECFormat.loadRegDataSet(config.getString("input.trainSet"),dataSetType,true);
        RegDataSet testSet = TRECFormat.loadRegDataSet(config.getString("input.testSet"),dataSetType,true);

        LinearRegression linearRegression = new LinearRegression(trainSet.getNumFeatures());
        ElasticNetLinearRegOptimizer optimizer = new ElasticNetLinearRegOptimizer(linearRegression,trainSet);
        optimizer.setRegularization(config.getDouble("regularization"));
        optimizer.setL1Ratio(config.getDouble("l1Ratio"));
        System.out.println("before training");
        System.out.println("training set RMSE = "+ RMSE.rmse(linearRegression,trainSet));
        System.out.println("test set RMSE = "+ RMSE.rmse(linearRegression,testSet));
        System.out.println("start training");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        optimizer.optimize();
        System.out.println("training done");
        System.out.println("time spent on training = "+stopWatch);
        System.out.println("after training");
        System.out.println("training set RMSE = "+ RMSE.rmse(linearRegression,trainSet));
        System.out.println("test set RMSE = "+ RMSE.rmse(linearRegression,testSet));

        System.out.println("number of non-zeros weights in linear regression (not including bias) = "+linearRegression.getWeights().getWeightsWithoutBias().getNumNonZeroElements());
        System.out.println("all non-zero weights in the format of (feature index:feature weight) pairs:");
        System.out.println("(Note that feature indices start from 0)");
        System.out.println(linearRegression.getWeights().getWeightsWithoutBias());

    }
}
