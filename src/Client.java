import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client {

//	                       {0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5}
	static int options[] = {4,4,5,6,3,5,5,4,4,3,3,3,4,4,2,2};
	static String labels[] = {"checking-balance", "months-loan-duration", "credit-history", "purpose", "amount", "savings-balance", "employment-duration", "percent-of-income", "years-at-residence", "age", "other-credit", "housing", "existing-loans-count", "job", "dependents", "default"};

	static Node root = new Node(15, 0, null);

	public static void main(String[] args) throws IOException, InterruptedException {
		Data data = new Data(new File("credit.csv"));
		
		Boolean testSelection[] = new Boolean[1000];
		int[][] trainingSet = new int[900][16];
		int[][] testSet = new int[100][16];
		double averageAccuracy = 0.0;


		for (int repeat = 0; repeat < 30; repeat++) {
			
			for (int i = 0; i < 1000; i++) {testSelection[i] = false;}
			randomSelection(testSelection, 100, 1000);
			
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
	
	// 1st level gains
	//		
	//		for (int i = 0; i < 15; i++) {
	//			double gain = gain(data.getData(), i, 15);
	//			System.out.println(i + " " + gain);
	//		}
			
	//		entropyAttributes(data.getData(), 7, 15);
			
			train(trainingSet);
			double hit = 0.0;
			for (int i = 0; i < 100; i++) {
				Boolean result = test(root, testSet[i]);
				if ((result == false && testSet[i][15] == 0) || (result == true && testSet[i][15] == 1)) {hit++;}
	//			System.out.println(result + " : " + testSet[i][15]);
			}
//			System.out.println(hit + "%");
			averageAccuracy += hit;
			
	//		printTree(root);

			
		}
		System.out.println(averageAccuracy/30 + "%");
	}
	
	static void printTree(Node root) {
		if (root.getChildren() == null) {
			System.out.println("null children: " + root.getResult());
		} else {
			System.out.println("(\r\n" + root.getAttribute() + ", " + root.getCategory() + ", " + root.getResult());
			for (int i = 0; i < root.getChildren().size(); i++) {
				printTree(root.getChildren().get(i));
			}
			System.out.println(")");
		}
	}

	static Boolean test(Node decisionTree, int[] testCase) {
		Node current = decisionTree;
		while (current.getChildren().size() > 1) {
			int attribute = current.getChildren().get(0).getAttribute();
			current = current.getChildren().get(testCase[attribute]);
		}
		return current.getChildren().get(0).getResult();
	}

	static void train(int[][] trainingSet) throws InterruptedException {
		List<Boolean> traversed = new ArrayList<Boolean>();
		for (int i = 0; i < 15; i++) {traversed.add(false);}
		train(root, trainingSet, 15, traversed);
	}
	
	static Node train(Node root, int[][] trainingSet, int targetAttribute, List<Boolean> traversedAttributes) throws InterruptedException {
//		System.out.print("[" + labels[root.getAttribute()] + "_" + root.getCategory() + " ");
		int examples = trainingSet.length;
		int positives = 0;
		for (int i = 0; i < examples; i++) {
			if (trainingSet[i][15] == 1)
				positives++;
		}
		if (positives == examples || (examples == 2 && positives == 1)) {
			root.addNode(null, null, true);
//			System.out.println("[" + root.getChildren().get(0).getResult() + "]]");
			return root;
		} else if (positives == 0) {
			root.addNode(null, null, false);
//			System.out.println("[" + root.getChildren().get(0).getResult() + "]]");
			return root;
		} else if (!traversedAttributes.contains(false) || examples < 60) {
			if (positives > examples) {
				root.addNode(null, null, true);
//				System.out.println("[" + root.getChildren().get(0).getResult() + "]]");
				return root;
			} else {
				root.addNode(null, null, false);
//				System.out.println("[" + root.getChildren().get(0).getResult() + "]]");
				return root;
			}
		} else {
			int indexMax = 0;
			double maxEntropy = 0.0, anteMaxEntropy = 0.0, temp = 0.0;
			for (int i = 0; i < 15; i++) {
				if (!traversedAttributes.get(i)) {
					temp = gain(trainingSet, i, 15);
					if (temp > maxEntropy) {
						indexMax = i;
//						if (maxEntropy > 0.0) {
							anteMaxEntropy = maxEntropy;
//						}
						maxEntropy = temp;
					}
				}
			}
//			System.out.println(anteMaxEntropy + " " + maxEntropy + " " + (anteMaxEntropy / maxEntropy));
//			if (anteMaxEntropy > 0.0 && (anteMaxEntropy / maxEntropy) <= 0.0125) {
//				if (positives > examples) {
//					root.addNode(null, null, true);
//					System.out.println("[" + root.getChildren().get(0).getResult() + "]]");
//					return root;
//				} else {
//					root.addNode(null, null, false);
//					System.out.println("[" + root.getChildren().get(0).getResult() + "]]");
//					return root;
//				}
//			}
//			if (root.getAttribute() == 0) {
//				System.out.println("here?");
//			}
			traversedAttributes.set(indexMax, true);
//			System.out.println(traversedAttributes.toString());
//			TimeUnit.MILLISECONDS.sleep(100);
			for (int i = 0; i < options[indexMax]; i++) {
				int[][] subset = getSubSet(trainingSet, indexMax, i);
				root.addNode(train(new Node(indexMax, i, null), subset, indexMax, traversedAttributes));
			}
			traversedAttributes.set(indexMax, false);
//			System.out.println("]");
			return root;
		}
	}
	
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
	
	static double gain(int[][] set, int attribute, int resultCol) {
		return entropySet(set, resultCol) - entropyAttributes(set, attribute, resultCol);
	}
	
	static double entropySet(int[][] set, int attribute) {
		double examples = set.length, positives = 0.0;
		for (int i = 0; i < examples; i++) {
			if (set[i][attribute] == 0) {
				positives++;
			}
		}
		double entropy = -(positives/examples) * (Math.log(positives/examples)/Math.log(2)) - ((examples-positives)/examples) * (Math.log((examples-positives)/examples)/Math.log(2));
//		System.out.println(positives + "\t" + examples + "\t" + entropy);
		return entropy;
	}
	
	static double entropyAttributes(int[][] set, int attribute, int resultCol) {
		double entropy = 0.0;
		double categoryCount = 0.0, positives = 0.0;
		int examples = set.length;
		for (int category = 0; category < options[attribute]; category++) {
//			System.out.println(category);
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
//				System.out.println(categoryCount + "\t" + examples + "\t" + positives + "\t" + entropy);
			}
			categoryCount = 0.0;
			positives = 0.0;
		}
		return entropy;
	}
	
	static void randomSelection(Boolean[] a, int select, int size) {
		Random r = new Random();
		
		while (select > 0) {
			int i = r.nextInt(size - 1);
			if (!a[i]) {
				a[i] = true;
				select--;
			}
		}
	}

}





























