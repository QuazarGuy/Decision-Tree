import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Data {
	String data[][] = new String[1000][16];
	int categorized[][] = new int[1000][16];
	
	Data(File file) throws IOException {
		String line = "";
		int count = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				data[count] = line.split(",");
				count++;
			}
		}
		
		//categorizing
		for(int i = 0; i < 1000; i++) {
			
			// 0 checking balance
			switch (data[i][0]) {
				case "unknown": categorized[i][0] = 0;
					break;
				case "< 0 DM": categorized[i][0] = 1;
					break;
				case "1 - 200 DM": categorized[i][0] = 2;
					break;
				case "> 200 DM": categorized[i][0] = 3;
					break;
			}
			
			// 1 loan duration
			int months = Integer.parseInt(data[i][1]);
			if (months <= 12) {
				categorized[i][1] = 0;
			} else if (months <= 24) {
				categorized[i][1] = 1;
			} else if (months <= 36) {
				categorized[i][1] = 2;
			} else {
				categorized[i][1] = 3;
			}
			
			// 2 credit rating
			switch (data[i][2]) {
				case "critical": categorized[i][2] = 0;
					break;
				case "poor": categorized[i][2] = 1;
					break;
				case "good": categorized[i][2] = 2;
					break;
				case "very good": categorized[i][2] = 3;
					break;
				case "perfect": categorized[i][2] = 4;
					break;
			}
			
			// 3 purpose
			switch (data[i][3]) {
				case "car0": categorized[i][3] = 0;
					break;
				case "car": categorized[i][3] = 1;
					break;
				case "business": categorized[i][3] = 2;
					break;
				case "education": categorized[i][3] = 3;
					break;
				case "furniture/appliances": categorized[i][3] = 4;
					break;
				case "renovations": categorized[i][3] = 5;
					break;
			}
			
			// 4 amount
			int amount = Integer.parseInt(data[i][4]);
			if (amount <= 2000) {
				categorized[i][4] = 0;
			} else if (amount <= 4000) {
				categorized[i][4] = 1;
			} else {
				categorized[i][4] = 2;
			}

			// 5 savings
			switch (data[i][5]) {
				case "unknown": categorized[i][5] = 0;
					break;
				case "< 100 DM": categorized[i][5] = 1;
					break;
				case "100 - 500 DM": categorized[i][5] = 2;
					break;
				case "500 - 1000 DM": categorized[i][5] = 3;
					break;
				case "> 1000 DM": categorized[i][5] = 4;
					break;
			}

			// 6 employment
			switch (data[i][6]) {
				case "unemployed": categorized[i][6] = 0;
					break;
				case "< 1 year": categorized[i][6] = 1;
					break;
				case "1 - 4 years": categorized[i][6] = 2;
					break;
				case "4 - 7 years": categorized[i][6] = 3;
					break;
				case "> 7 years": categorized[i][6] = 4;
					break;
			}
			
			// 7 percent of income
			categorized[i][7] = Integer.parseInt(data[i][7]) - 1;

			// 8 years at residence
			categorized[i][8] = Integer.parseInt(data[i][8]) - 1;

			// 9 age
			int age = Integer.parseInt(data[i][9]);
			if (age <= 30) {
				categorized[i][9] = 0;
			} else if (age <= 40) {
				categorized[i][9] = 1;
			} else {
				categorized[i][9] = 2;
			}

			// 10 other credit
			switch (data[i][10]) {
				case "none": categorized[i][10] = 0;
					break;
				case "bank": categorized[i][10] = 1;
					break;
				case "store": categorized[i][10] = 2;
					break;
			}
			
			// 11 housing
			switch (data[i][11]) {
				case "other": categorized[i][11] = 0;
					break;
				case "own": categorized[i][11] = 1;
					break;
				case "rent": categorized[i][11] = 2;
					break;
			}
			
			// 12 existing loans count
			categorized[i][12] = Integer.parseInt(data[i][12]) - 1;

			// 13 job
			switch (data[i][13]) {
				case "unemployed": categorized[i][13] = 0;
					break;
				case "unskilled": categorized[i][13] = 1;
					break;
				case "skilled": categorized[i][13] = 2;
					break;
				case "management": categorized[i][13] = 3;
					break;
			}

			// 14 dependents
			categorized[i][14] = Integer.parseInt(data[i][14]) - 1;

			// 15 defaulted
			if (data[i][15].equals("no")) {
				categorized[i][15] = 0;
			} else {
				categorized[i][15] = 1;
			}
			
		}
	}

	public int[][] getData() {
		return categorized;
	}
	
	
}

























