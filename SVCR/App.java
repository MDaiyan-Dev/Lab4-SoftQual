package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App
{
	public static void main( String[] args )
	{
		String[] filePaths={"model_1.csv", "model_2.csv", "model_3.csv"};
		String bestModelMSE = "", bestModelMAE = "", bestModelMARE = "";
		double minMSE = Double.MAX_VALUE, minMAE = Double.MAX_VALUE, minMARE = Double.MAX_VALUE;
		double epsilon = 1e-10;

		for (String filePath : filePaths){
			FileReader filereader;
			List<String[]> allData;
			double mse=0.0, mae=0.0, mare=0.0;
			int count=0;

			try{
				filereader = new FileReader(filePath);
				CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
				allData = csvReader.readAll();
			}
			catch(Exception e){
				System.out.println( "Error reading the CSV file: " + filePath);
				continue;
			}

			for (String[] row : allData) {
				double y_true=Double.parseDouble(row[0]);
				double y_pred=Double.parseDouble(row[1]);

				double error = y_true - y_pred;
				mse += error * error;
				mae += Math.abs(error);
				mare += Math.abs(error) / (Math.abs(y_true) + epsilon);

				count++;
			}

			if (count > 0) {
				mse /= count;
				mae /= count;
				mare = (mare / count) * 100;
			}

			System.out.println("Results for " + filePath + ":");
			System.out.printf("MSE =%.5f\n", mse);
			System.out.printf("MAE =%.7f\n", mae);
			System.out.printf("MARE =%.8f\n\n", mare);

			if (mse < minMSE) {
				minMSE = mse;
				bestModelMSE = filePath;
			}
			if (mae < minMAE) {
				minMAE = mae;
				bestModelMAE = filePath;
			}
			if (mare < minMARE) {
				minMARE = mare;
				bestModelMARE = filePath;
			}
		}

		System.out.println("According to MSE, The best model is " + bestModelMSE);
		System.out.println("According to MAE, The best model is " + bestModelMAE);
		System.out.println("According to MARE, The best model is " + bestModelMARE);
	}
}
