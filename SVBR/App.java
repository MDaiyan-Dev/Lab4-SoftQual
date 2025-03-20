package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.util.Arrays;

public class App
{
	public static void main( String[] args )
	{
		String[] filePaths={"model_1.csv", "model_2.csv", "model_3.csv"};
		String bestModelBCE = "", bestModelAccuracy = "", bestModelPrecision = "", bestModelRecall = "", bestModelF1 = "", bestModelAUC = "";
		double minBCE = Double.MAX_VALUE, maxAccuracy = Double.MIN_VALUE, maxPrecision = Double.MIN_VALUE, maxRecall = Double.MIN_VALUE, maxF1 = Double.MIN_VALUE, maxAUC = Double.MIN_VALUE;
		double epsilon = 1e-10;

		for (String filePath : filePaths){
			FileReader filereader;
			List<String[]> allData;
			double bce=0.0;
			int TP=0, FP=0, FN=0, TN=0, n_positive=0, n_negative=0;
			int count=0;
			double[] thresholds = new double[101];
			double[] TPR = new double[101];
			double[] FPR = new double[101];
			double[] y_true;
			double[] y_pred;

			try{
				filereader = new FileReader(filePath);
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
				allData = csvReader.readAll();
				count = allData.size();
				y_true = new double[count];
				y_pred = new double[count];
			}
			catch(Exception e){
				System.out.println( "Error reading the CSV file: " + filePath);
				continue;
			}

			int index = 0;
			for (String[] row : allData) {
				int y_true_value = Integer.parseInt(row[0]);
				double y_pred_value = Double.parseDouble(row[1]);

				y_true[index] = y_true_value;
				y_pred[index] = y_pred_value;

				bce += y_true_value * Math.log(y_pred_value + epsilon) + (1 - y_true_value) * Math.log(1 - y_pred_value + epsilon);

				if (y_true_value == 1) {
					n_positive++;
				} else {
					n_negative++;
				}
				index++;
			}

			bce = -bce / count;

			// Confusion matrix (threshold = 0.5)
			for (int i = 0; i < count; i++) {
				int y_pred_binary = (y_pred[i] >= 0.5) ? 1 : 0;

				if (y_true[i] == 1 && y_pred_binary == 1) {
					TP++;
				} else if (y_true[i] == 0 && y_pred_binary == 1) {
					FP++;
				} else if (y_true[i] == 1 && y_pred_binary == 0) {
					FN++;
				} else {
					TN++;
				}
			}

			double accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
			double precision = (double) TP / (TP + FP + epsilon);
			double recall = (double) TP / (TP + FN + epsilon);
			double f1_score = 2 * (precision * recall) / (precision + recall + epsilon);

			// ROC curve
			for (int i = 0; i <= 100; i++) {
				double th = i / 100.0;
				int TP_roc = 0, FP_roc = 0;

				for (int j = 0; j < count; j++) {
					int y_pred_binary = (y_pred[j] >= th) ? 1 : 0;
					if (y_true[j] == 1 && y_pred_binary == 1) {
						TP_roc++;
					} else if (y_true[j] == 0 && y_pred_binary == 1) {
						FP_roc++;
					}
				}

				TPR[i] = (double) TP_roc / (n_positive + epsilon);
				FPR[i] = (double) FP_roc / (n_negative + epsilon);
			}

			// AUC-ROC
			double auc_roc = 0;
			for (int i = 1; i <= 100; i++) {
				auc_roc += (TPR[i - 1] + TPR[i]) * Math.abs(FPR[i - 1] - FPR[i]) / 2;
			}

			// Print results
			System.out.println("Results for " + filePath + ":");
			System.out.printf("BCE =%.7f\n", bce);
			System.out.println("Confusion matrix");
			System.out.println("y=1     y=0");
			System.out.printf("y^=1     %-8d%-8d\n", TP, FP);
			System.out.printf("y^=0     %-8d%-8d\n", FN, TN);
			System.out.printf("Accuracy =%.4f\n", accuracy);
			System.out.printf("Precision =%.8f\n", precision);
			System.out.printf("Recall =%.8f\n", recall);
			System.out.printf("f1 score =%.8f\n", f1_score);
			System.out.printf("auc roc =%.8f\n\n", auc_roc);

			// Track best models
			if (bce < minBCE) {
				minBCE = bce;
				bestModelBCE = filePath;
			}
			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				bestModelAccuracy = filePath;
			}
			if (precision > maxPrecision) {
				maxPrecision = precision;
				bestModelPrecision = filePath;
			}
			if (recall > maxRecall) {
				maxRecall = recall;
				bestModelRecall = filePath;
			}
			if (f1_score > maxF1) {
				maxF1 = f1_score;
				bestModelF1 = filePath;
			}
			if (auc_roc > maxAUC) {
				maxAUC = auc_roc;
				bestModelAUC = filePath;
			}
		}

		// Print best models
		System.out.println("According to BCE, The best model is " + bestModelBCE);
		System.out.println("According to Accuracy, The best model is " + bestModelAccuracy);
		System.out.println("According to Precision, The best model is " + bestModelPrecision);
		System.out.println("According to Recall, The best model is " + bestModelRecall);
		System.out.println("According to F1 score, The best model is " + bestModelF1);
		System.out.println("According to AUC ROC, The best model is " + bestModelAUC);
	}
}
