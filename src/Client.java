import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Client {

//		   eliminated      {1,7,9,11,13,14}
//	       attribute % 10  {0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5}
	static int options[] = {4,4,5,6,3,5,5,4,4,3,3,3,4,4,2,2};
	static String labels[] = {"checking-balance", "months-loan-duration", "credit-history", "purpose", "amount", "savings-balance", "employment-duration", "percent-of-income", "years-at-residence", "age", "other-credit", "housing", "existing-loans-count", "job", "dependents", "default"};

	static Node root = new Node(15, 0, null);

	public static void main(String[] args) throws IOException, InterruptedException {
		Data data = new Data(new File("credit.csv"), false, true, 1000);
		
		Boolean testSelection[];
		int[][] trainingSet;
		int[][] testSet;
//		double averageError = 0.0;

//		for (int repeat = 0; repeat < 10000; repeat++) {
			root = new Node(15, 0, null);
			testSelection = randomSelection(data, 100, 1000);
			trainingSet = new int[900][16];
			testSet = new int[100][16];
			
			int j=0, k=0;
			for (int i = 0; i < 1000; i++) {
				if (testSelection[i]) {
					testSet[j] = data.getData()[i];
					j++;
				} else {
					trainingSet[k] = data.getData()[i];
					k++;
				}
			}
			Data trainingCases = new Data(trainingSet);
			trainingCases.writeData(new File("training_data.csv"));
			Data testCases = new Data(testSet);
			testCases.writeData(new File("test_data.csv"));
			
			train(trainingSet);
			double miss = 0.0;
			for (int i = 0; i < 100; i++) {
				Boolean result = test(root, testSet[i]);
				if (!(result == false && testSet[i][15] == 0) && !(result == true && testSet[i][15] == 1)) 
					miss++;
			}
			System.out.println("This model's error rate: " + miss + "%\n");
			
			System.out.print("Enter the name of your test cases file: ");
			Scanner in = new Scanner(System.in);
			String userFile = in.nextLine();
			System.out.print("Enter number of test cases: ");
			int cases = in.nextInt();
			in.close();
			Data userTests = new Data(new File(userFile), false, false, cases);
			int[][] newTests = userTests.getData();
			
			miss = 0.0;
			for (int i = 0; i < cases; i++) {
				Boolean result = test(root, newTests[i]);
				if (!(result == false && newTests[i][15] == 0) && !(result == true && newTests[i][15] == 1)) 
					miss++;
			}

			System.out.println("The user's test case error rate: " + miss/cases*100 + "%\n");

//			averageError += miss;
		}
//		System.out.println("Average Error: " + averageError/10000.0 + "%");
//	}
	
	static Boolean test(Node decisionTree, int[] testCase) {
		Node current = decisionTree;
		while (current.getChildren().size() > 1) {
			int attribute = current.getChildren().get(0).getAttribute();
			current = current.getChildren().get(testCase[attribute]);
		}
		return current.getChildren().get(0).getResult();
	}

	// Builds a decision tree
	static void train(int[][] trainingSet) {
		List<Boolean> traversed = new ArrayList<Boolean>();
		for (int i = 0; i < 15; i++) {traversed.add(false);}

		// Eliminated attributes
		traversed.set(1, true);
		traversed.set(7, true);
		traversed.set(9, true);
		traversed.set(11, true);
		traversed.set(13, true);
		traversed.set(14, true);
		
		train(root, trainingSet, 15, traversed);
	}
	
	// Recursively builds the decision tree
	static Node train(Node root, int[][] trainingSet, int targetAttribute, List<Boolean> traversedAttributes) {
		int examples = trainingSet.length;
		int positives = 0;
		for (int i = 0; i < examples; i++) {
			if (trainingSet[i][15] == 1)
				positives++;
		}
		
		// leaf node if all results are no or there is a tie with 2 left
		if (positives == 0 || (examples == 2 && positives == 1)) {
			root.addNode(null, null, false);
			return root;
			
		// leaf node if all results are yes
		} else if (positives == examples) {
			root.addNode(null, null, true);
			return root;
			
		// leaf node if all attributes have been used
		} else if (!traversedAttributes.contains(false)) {
			if (positives > examples) {
				root.addNode(null, null, true);
				return root;
			} else {
				root.addNode(null, null, false);
				return root;
			}
		} else {
			
			// find attribute with highest gain
			int indexMax = 0;
			double maxGain = 0.0, temp = 0.0;
			for (int i = 0; i < 15; i++) {
				if (!traversedAttributes.get(i)) {
					temp = gain(trainingSet, i, 15);
					if (temp > maxGain) {
						indexMax = i;
						maxGain = temp;
					}
				}
			}
			
			// leaf node if gain is too low
			if (maxGain < 0.04) {
				if (positives > examples) {
					root.addNode(null, null, true);
					return root;
				} else {
					root.addNode(null, null, false);
					return root;
				}
			}
			
			// root node
			traversedAttributes.set(indexMax, true);
			for (int i = 0; i < options[indexMax]; i++) {
				int[][] subset = getSubSet(trainingSet, indexMax, i);
				root.addNode(train(new Node(indexMax, i, null), subset, indexMax, traversedAttributes));
			}
			traversedAttributes.set(indexMax, false);
			return root;
		}
	}
	
	// returns a subset containing a category from the given set
	static int[][] getSubSet(int[][] set, int attribute, int category) {
		int size = 0;
		for (int i = 0; i < set.length; i++) {
			if (set[i][attribute] == category) {size++;}
		}
		int[][] subSet = new int[size][16];
		int count = 0;
		for (int i = 0; i < set.length; i++) {
			if (set[i][attribute] == category) {
				subSet[count] = set[i];
				count++;
			}
		}
		return subSet;
	}
	
	// finds the gain of an attribute
	static double gain(int[][] set, int attribute, int resultCol) {
		return entropySet(set, resultCol) - entropyAttributes(set, attribute, resultCol);
	}
	
	// finds the entropy of the given set
	static double entropySet(int[][] set, int attribute) {
		double examples = set.length, positives = 0.0;
		for (int i = 0; i < examples; i++) {
			if (set[i][attribute] == 0) {
				positives++;
			}
		}
		double entropy = -(positives/examples) * (Math.log(positives/examples)/Math.log(2)) - ((examples-positives)/examples) * (Math.log((examples-positives)/examples)/Math.log(2));
		return entropy;
	}
	
	// finds the entropy of an attribute in the given set
	static double entropyAttributes(int[][] set, int attribute, int resultCol) {
		double entropy = 0.0;
		double categoryCount = 0.0, positives = 0.0;
		int examples = set.length;
		for (int category = 0; category < options[attribute]; category++) {
			for (int i = 0; i < examples; i++) {
				if (set[i][attribute] == category) {
					categoryCount++;
					if (set[i][resultCol] == 0) {
						positives++;
					}
				}
			}
			if (examples != 0 && categoryCount != 0 && positives != 0 && positives != categoryCount) {
				entropy += (categoryCount/examples) * (-(positives/categoryCount) * (Math.log(positives/categoryCount)/Math.log(2.0)) - ((categoryCount-positives)/categoryCount) * (Math.log((categoryCount-positives)/categoryCount)/Math.log(2.0))); 
			}
			categoryCount = 0.0;
			positives = 0.0;
		}
		return entropy;
	}
	
	// Select test cases while maintaining the ratio of yeses to noes
	static Boolean[] randomSelection(Data data, int select, int size) {
		Random r = new Random();
		Boolean[] selection = new Boolean[1000];
		Arrays.fill(selection, false);

		int neg=70, pos=30;
		while (select > 0) {
			int i = r.nextInt(size - 1);
			if (!selection[i]) {
				if ((data.getData()[i][15] == 0) && neg > 0) {
					neg--;
					selection[i] = true;
					select--;
				}
				if ((data.getData()[i][15] == 1) && pos > 0) {
					pos--;
					selection[i] = true;
					select--;
				}
			}
		}
		return selection;
	}
}





























