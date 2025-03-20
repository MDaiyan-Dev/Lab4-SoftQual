package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App
{
	public static void main( String[] args )
	{
		String filePath="model.csv";
		FileReader filereader;
		List<String[]> allData;
		double ce = 0.0;
		double epsilon = 1e-10;
		int numClasses = 5;
		int[][] confusionMatrix = new int[numClasses][numClasses];

		try{
			filereader = new FileReader(filePath);
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}

		int count = allData.size();
		for (String[] row : allData) {
			int y_true = Integer.parseInt(row[0]);
			double[] y_predicted = new double[numClasses];

			for(int i = 0; i < numClasses; i++){
				y_predicted[i] = Double.parseDouble(row[i+1]);
			}

			ce += Math.log(y_predicted[y_true - 1] + epsilon);
			int predictedClass = 0;
			double maxProb = y_predicted[0];
			for (int i = 1; i < numClasses; i++) {
				if (y_predicted[i] > maxProb) {
					maxProb = y_predicted[i];
					predictedClass = i;
				}
			}
			
			confusionMatrix[predictedClass][y_true - 1] += 1;
		}

		ce = -ce / count;
		System.out.printf("CE =%.7f\n", ce);
		System.out.println("Confusion matrix");
		System.out.print("                ");
		for (int i = 1; i <= numClasses; i++) {
			System.out.printf("y=%-7d", i);
		}

		System.out.println();

		for (int i = 0; i < numClasses; i++) {
			System.out.printf("        y^=%-2d", i + 1);
			for (int j = 0; j < numClasses; j++) {
				System.out.printf("%-8d", confusionMatrix[i][j]);
			}
			System.out.println();
		}
	}
}
