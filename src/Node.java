import java.util.ArrayList;
import java.util.List;

public class Node {
	List<Node> children;
	Integer attribute;
	Integer category;
	Boolean result;
	
	Node(Integer attribute, Integer category, Boolean result) {
		children = new ArrayList<Node>();
		this.attribute = attribute;
		this.category = category;
		this.result = result;
	}
	
	void setResult(Boolean result) {this.result = result;}
	
	Integer getAttribute() {return attribute;}
	Integer getCategory() {return category;}
	Boolean getResult() {return result;}
	
	void addNode(Integer attribute, Integer category, Boolean result) {
		children.add(new Node(attribute, category, result));
	}
	
	void addNode(Node node) {
		children.add(node);
	}
	
	Node getNodeByCategory(int category) {
		return children.get(category);
	}
	
	Node getNodeByAttribute(int attribute) {
		return children.get(attribute);
	}

	List<Node> getChildren () {
		return children;
	}
	
}
