import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

	DTNode rootDTNode;
	int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
	//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
	public static final long serialVersionUID = 343L;
	public DecisionTree(ArrayList<Datum> datalist , int min) {
		minSizeDatalist = min;
		rootDTNode = (new DTNode()).fillDTNode(datalist);
	}

	class DTNode implements Serializable{
		//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
		public static final long serialVersionUID = 438L;
		boolean leaf;
		int label = -1;      // only defined if node is a leaf
		int attribute; // only defined if node is not a leaf
		double threshold;  // only defined if node is not a leaf



		DTNode left, right; //the left and right child of a particular node. (null if leaf)

		DTNode() {
			leaf = true;
			threshold = Double.MAX_VALUE;
		}



		// this method takes in a datalist (ArrayList of type datum) and a minSizeInClassification (int) and returns
		// the calling DTNode object as the root of a decision tree trained using the datapoints present in the
		// datalist variable
		// Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
		DTNode fillDTNode(ArrayList<Datum> datalist) {

			//YOUR CODE HERE

			if(datalist.size() >= minSizeDatalist) {

				int label = datalist.get(0).y;
				boolean allequal = true; 


				for (Datum dat : datalist) {

					if(dat.y != label) {
						allequal = false; 
						break;
					}


				}

				if (allequal == true) {
					
					this.leaf = true;
					this.label = label;
					return this;

				}

				else {

					// create a best attribute test question

					double best_avg_entropy = Double.MAX_VALUE;
					int best_attr = -1; 
					double best_threshold = -1;
					

					for(int i=0; i<datalist.get(0).x.length; i++) {

						for(int j = 0; j < datalist.size(); j++) {
							
							ArrayList<Datum> d1 = new ArrayList<Datum>();
							ArrayList<Datum> d2 = new ArrayList<Datum>();
							
							for(int k = 0; k < datalist.size(); k++) {
								
									if(datalist.get(k).x[i] < datalist.get(j).x[i]) {
										d1.add(datalist.get(k));
									}
	
									else {
										d2.add(datalist.get(k));
									}

								

								}

							double current_avg_entropy = ((double)d1.size()/datalist.size())*calcEntropy(d1) + ((double)d2.size()/datalist.size())*calcEntropy(d2); 

							if(best_avg_entropy > current_avg_entropy) {

								best_avg_entropy = current_avg_entropy;
								best_attr = i;
								best_threshold = datalist.get(j).x[i];
							
							}
						

						}

				

					}				

					// create a new node and store the attribute test in that node, namely attribute and threshold

					this.leaf = false;
					this.attribute = best_attr;
					this.threshold = best_threshold;
					

					// split the set of data items into two subsets, data 1 and data 2, according to the answers to test question

					ArrayList<Datum> data1 = new ArrayList<Datum>();
					ArrayList<Datum> data2 = new ArrayList<Datum>();

					for(Datum D1 : datalist) {

						if(D1.x[best_attr] < best_threshold) {
							data1.add(D1);
						}

						if(D1.x[best_attr] >= best_threshold) {
							data2.add(D1);
						}

					}

					if(data1.size() != 0 && data2.size() != 0) {
						
						this.left=(new DTNode()).fillDTNode(data1);

						this.right=(new DTNode()).fillDTNode(data2);

						return this;
					}

					if(data1.size() != 0 && data2.size() == 0 || data1.size() == 0 && data2.size() != 0 || data1.size() == 0 || data2.size() == 0) {
						this.leaf = true;
						this.label = findMajority(datalist);
						return this;
					}


				}
			}


			this.leaf = true;
			this.label = findMajority(datalist);
			return this;


		}



		//This is a helper method. Given a datalist, this method returns the label that has the most
		// occurences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
		int findMajority(ArrayList<Datum> datalist)
		{
			int l = datalist.get(0).x.length;
			int [] votes = new int[l];

			//loop through the data and count the occurrences of datapoints of each label
			for (Datum data : datalist)
			{
				votes[data.y]+=1;
			}
			int max = -1;
			int max_index = -1;
			//find the label with the max occurrences
			for (int i = 0 ; i < l ;i++)
			{
				if (max<votes[i])
				{
					max = votes[i];
					max_index = i;
				}
			}
			return max_index;
		}




		// This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
		// returns its corresponding label, as determined by the decision tree
		int classifyAtNode(double[] xQuery) {
			//YOUR CODE HERE

			DTNode pos = this;
			
			while(pos.leaf != true) {
				
			if (pos.attribute == 0) {
					

					if(xQuery[0] < pos.threshold) {
						return pos.left.classifyAtNode(xQuery);	
					}

					else  {
						return pos.right.classifyAtNode(xQuery);	
					}
				}
					
				else {
						
					
					if(xQuery[1] < pos.threshold) {
						return pos.left.classifyAtNode(xQuery);	
					}

					else  {
						return pos.right.classifyAtNode(xQuery);	
					}

				}

			}
			
			return pos.label;


		}


		//given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
		//at DTNode object passed as the parameter
		public boolean equals(Object dt2)
		{

			//YOUR CODE HERE

			DTNode DT2 = (DTNode) dt2;

			//if (this == null && DT2 == null) {
			//return true; 
			//} 

			if (this == null && DT2 != null || this != null && DT2 == null) {
				return false;
			}

			if (DT2.leaf == true  && this.leaf == false || this.leaf == true && DT2.leaf == false) {
				return false;
			}

			if (this.leaf == true && DT2.leaf == true && this.label == (DT2.label)) {

				return true;
			} 


			if(this != null && DT2 != null) {

				if(this.attribute == DT2.attribute && this.threshold == DT2.threshold) {	
					
					if(this.left == null && DT2.left != null || this.left != null && DT2.left == null) {
						return false;
					}
					
					if(this.right == null && DT2.right != null || this.right != null && DT2.right == null) {
						return false;
					}
					
					if(this.left == null && DT2.left == null) {
						return (this.right.equals(DT2.right));
					}
				
				
					if(this.right == null && DT2.right == null) {
						return (this.left.equals(DT2.left));
					}

					return(this.left.equals(DT2.left) && this.right.equals(DT2.right)); 

				}

			}

			return false;
		}
	}



	//Given a dataset, this retuns the entropy of the dataset
	double calcEntropy(ArrayList<Datum> datalist)
	{
		double entropy = 0;
		double px = 0;
		float [] counter= new float[2];
		if (datalist.size()==0)
			return 0;
		double num0 = 0.00000001,num1 = 0.000000001;

		//calculates the number of points belonging to each of the labels
		for (Datum d : datalist)
		{
			counter[d.y]+=1;
		}
		//calculates the entropy using the formula specified in the document
		for (int i = 0 ; i< counter.length ; i++)
		{
			if (counter[i]>0)
			{
				px = counter[i]/datalist.size();
				entropy -= (px*Math.log(px)/Math.log(2));
			}
		}

		return entropy;
	}


	// given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
	int classify(double[] xQuery ) {
		DTNode node = this.rootDTNode;
		return node.classifyAtNode( xQuery );
	}

	// Checks the performance of a DecisionTree on a dataset
	//  This method is provided in case you would like to compare your
	//results with the reference values provided in the PDF in the Data
	//section of the PDF

	String checkPerformance( ArrayList<Datum> datalist)
	{
		DecimalFormat df = new DecimalFormat("0.000");
		float total = datalist.size();
		float count = 0;

		for (int s = 0 ; s < datalist.size() ; s++) {
			double[] x = datalist.get(s).x;
			int result = datalist.get(s).y;
			if (classify(x) != result) {
				count = count + 1;
			}
		}

		return df.format((count/total));
	}


	//Given two DecisionTree objects, this method checks if both the trees are equal by
	//calling onto the DTNode.equals() method
	public static boolean equals(DecisionTree dt1,  DecisionTree dt2)
	{
		boolean flag = true;
		flag = dt1.rootDTNode.equals(dt2.rootDTNode);
		return flag;
	}

}
