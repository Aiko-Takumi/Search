import java.util.*;

public class Search {
	Node[] node;
	Node goal;
	Node start;

	Search(boolean rand) {
		makeStateSpace(rand);
	}

  private int random(boolean rand, int def){
    return rand ? (int)(Math.random()*10):def;
  }
	private void makeStateSpace(boolean rand) {
		node = new Node[10];
		// 状態空間の生成
		node[0] = new Node("L.A.Airport", 0);
		node[1] = new Node("UCLA", random(rand, 7));
		node[2] = new Node("Hoolywood", random(rand, 4));
		node[3] = new Node("Anaheim", random(rand, 6));
		node[4] = new Node("GrandCanyon", random(rand, 1));
		node[5] = new Node("SanDiego", random(rand, 2));
		node[6] = new Node("Downtown", random(rand, 3));
		node[7] = new Node("Pasadena", random(rand, 4));
		node[8] = new Node("DisneyLand", random(rand, 2));
		node[9] = new Node("Las Vegas", 0);
		start = node[0];
		goal = node[9];

		node[0].addChild(node[1], random(rand, 1));
		node[0].addChild(node[2], random(rand, 3));
		node[1].addChild(node[2], random(rand, 1));
		node[1].addChild(node[6], random(rand, 6));
		node[2].addChild(node[3], random(rand, 6));
		node[2].addChild(node[6], random(rand, 6));
		node[2].addChild(node[7], random(rand, 3));
		node[3].addChild(node[4], random(rand, 5));
		node[3].addChild(node[7], random(rand, 2));
		node[3].addChild(node[8], random(rand, 4));
		node[4].addChild(node[8], random(rand, 2));
		node[4].addChild(node[9], random(rand, 1));
		node[5].addChild(node[1], random(rand, 1));
		node[6].addChild(node[5], random(rand, 7));
		node[6].addChild(node[7], random(rand, 2));
		node[7].addChild(node[8], random(rand, 1));
		node[7].addChild(node[9], random(rand, 7));
		node[8].addChild(node[9], random(rand, 5));

		for(Node single : node){
			single.initialize();
			single.print();
		}
	}

	/***
	 * 幅優先探索
	 */
	public void breadthFirst() {
		long startTime1 = System.nanoTime();
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					// 子節点 m が open にも closed にも含まれていなければ，
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							if (m == goal) {
								open.add(0, m);
							} else {
								open.add(m);
							}
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
		long endTime1 = System.nanoTime();
		System.out.println("処理時間:"+(endTime1-startTime1)+" ステップ数:"+step);
	}

	/***
	 * 深さ優先探索
	 */
	public void depthFirst() {
		long startTime2 = System.nanoTime();
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					// 子節点 m が open にも closed にも含まれていなければ，
					// 以下を実行．幅優先探索と異なるのはこの部分である．
					// j は複数の子節点を適切にopenの先頭に置くために位置
					// を調整する変数であり，一般には展開したときの子節点
					// の位置は任意でかまわない．
					int j = 0;
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける
							m.setPointer(node);
							if (m == goal) {
								open.add(0, m);
							} else {
								open.add(j, m);
							}
							j++;
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
		long endTime2 = System.nanoTime();
		System.out.println("処理時間:"+(endTime2-startTime2)+" パス数:"+step);
	}

	/***
	 * 分岐限定法
	 */
	public void branchAndBound() {
		long startTime3 = System.nanoTime();
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							// nodeまでの評価値とnode->mのコストを
							// 足したものをmの評価値とする
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
							open.add(m);
						}
						// 子節点mがopenに含まれているならば，
						if (open.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							if (gmn < m.getGValue()) {
								m.setGValue(gmn);
								m.setPointer(node);
							}
						}
					}
				}
			}
			open = sortUpperByGValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
		long endTime3 = System.nanoTime();
		System.out.println("処理時間:"+(endTime3-startTime3)+" ステップ数:"+step);
	}

	/***
	 * 山登り法
	 */
	public void hillClimbing() {
		long startTime4 = System.nanoTime();
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;

		// Start を node とする．
		Node node = start;
		int st = 0;
		for (int step = 0;;step++) {
			// node は ゴールか？
			if (step  == 100){
				success = false;
				break;
			}else if (node == goal) {
				success = true;
				break;
			} else {
				// node を展開して子節点をすべて求める．
				ArrayList<Node> children = node.getChildren();
				System.out.println(children.toString());
				for (int i = 0; i < children.size(); i++) {
					Node m = children.get(i);
					// m から node へのポインタを付ける．
					m.setPointer(node);
				}
				// 子節点の中に goal があれば goal を node とする．
				// なければ，最小の hValue を持つ子節点 m を node
				// とする．
				boolean goalp = false;
				Node min = children.get(0);
				for (int i = 0; i < children.size(); i++) {
					Node a = children.get(i);
					if (a == goal) {
						goalp = true;
					} else if (min.getHValue() > a.getHValue()) {
						min = a;
					}
				}
				if (goalp) {
					node = goal;
				} else {
					node = min;
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
			long endTime4 = System.nanoTime();
			System.out.println("処理時間:"+(endTime4-startTime4)+" ステップ数:"+st);
		}else{
			System.out.println("*** Failure ***");
			System.out.println("Over 100 Step");
		}
	}

	/***
	 * 最良優先探索
	 */
	public void bestFirst() {
		long startTime5 = System.nanoTime();
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							open.add(m);
						}
					}
				}
			}
			open = sortUpperByHValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
		long endTime5 = System.nanoTime();
		System.out.println("処理時間:"+(endTime5-startTime5)+" ステップ数:"+step);
	}

	/***
	 * A* アルゴリズム
	 */
	public void aStar() {
		long startTime6 = System.nanoTime();
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		start.setFValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						int gmn = node.getGValue() + node.getCost(m);
						int fmn = gmn + m.getHValue();

						// 各子節点mの評価値とポインタを設定する
						if (!open.contains(m) && !closed.contains(m)) {
							// 子節点mがopenにもclosedにも含まれていない場合
							// m から node へのポインタを付ける．
							m.setGValue(gmn);
							m.setFValue(fmn);
							m.setPointer(node);
							// mをopenに追加
							open.add(m);
						} else if (open.contains(m)) {
							// 子節点mがopenに含まれている場合
							if (gmn < m.getGValue()) {
								// 評価値を更新し，m から node へのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
							}
						} else if (closed.contains(m)) {
							if (gmn < m.getGValue()) {
								// 子節点mがclosedに含まれていて fmn < fm となる場合
								// 評価値を更新し，mからnodeへのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
								// 子節点mをclosedからopenに移動
								closed.remove(m);
								open.add(m);
							}
						}
					}
				}
			}
			open = sortUpperByFValue(open);
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
		long endTime6 = System.nanoTime();
		System.out.println("処理時間:"+(endTime6-startTime6)+" ステップ数:"+step);
	}

	
	
	/***
	 * 解の表示
	 */
	public void printSolution(Node theNode) {
		if (theNode == start) {
			System.out.println(theNode.toString());
		} else {
			System.out.print(theNode.toString() + " <- ");
			printSolution(theNode.getPointer());
		}
	}

	/***
	 * ArrayList を Node の fValue の昇順（小さい順）に列べ換える．
	 */
	public ArrayList<Node> sortUpperByFValue(ArrayList<Node> theOpen) {
		ArrayList<Node> newOpen = new ArrayList<Node>();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.get(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.get(i);
				if (min.getFValue() > tmp.getFValue()) {
					min = tmp;
				}
			}
			newOpen.add(min);
			theOpen.remove(min);
		}
		return newOpen;
	}

	
	/***
	 * ArrayList を Node の gValue の昇順（小さい順）に列べ換える．
	 */
	public ArrayList<Node> sortUpperByGValue(ArrayList<Node> theOpen) {
		ArrayList<Node> newOpen = new ArrayList<Node>();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.get(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.get(i);
				if (min.getGValue() > tmp.getGValue()) {
					min = tmp;
				}
			}
			newOpen.add(min);
			theOpen.remove(min);
		}
		return newOpen;
	}

	/***
	 * ArrayList を Node の hValue の昇順（小さい順）に列べ換える．
	 */
	public ArrayList<Node> sortUpperByHValue(ArrayList<Node> theOpen) {
		ArrayList<Node> newOpen = new ArrayList<Node>();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.get(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.get(i);
				if (min.getHValue() > tmp.getHValue()) {
					min = tmp;
				}
			}
			newOpen.add(min);
			theOpen.remove(min);
		}
		return newOpen;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("USAGE:");
			System.out.println("java Search [Number] [rand(0 or 1)]");
			System.out.println("[Number] = 1 : Bredth First Search");
			System.out.println("[Number] = 2 : Depth  First Search");
			System.out.println("[Number] = 3 : Branch and Bound Search");
			System.out.println("[Number] = 4 : Hill Climbing Search");
			System.out.println("[Number] = 5 : Best First Search");
			System.out.println("[Number] = 6 : A star Algorithm");
		} else {
			int which = Integer.parseInt(args[0]);
			Search search = new Search(Integer.parseInt(args[1]) == 1);
			for(int i = 1; i < (Integer.parseInt(args[1]) == 1 ? 7 : 2);i++){
				switch (Integer.parseInt(args[1]) == 1 ? i : which) {
				case 1:
					// 幅優先探索
					System.out.println("\nBreadth First Search");
					search.breadthFirst();
					break;
				case 2:
					// 深さ優先探索
					System.out.println("\nDepth First Search");
					search.depthFirst();
					break;
				case 3:
					// 分岐限定法
					System.out.println("\nBranch and Bound Search");
					search.branchAndBound();
					break;
				case 4:
					// 山登り法
					System.out.println("\nHill Climbing Search");
					search.hillClimbing();
					break;
				case 5:
					// 最良優先探索
					System.out.println("\nBest First Search");
					search.bestFirst();
					break;
				case 6:
					// A*アルゴリズム
					System.out.println("\nA star Algorithm");
					search.aStar();
					break;
				default:
					System.out.println("Please input numbers 1 to 6");
				}
			}
		}
	}
}

class Node {
	String name;
	ArrayList<Node> children;
	HashMap<Node,Integer> childrenCosts;
	Node pointer; // 解表示のためのポインタ
	int gValue; // コスト
	int hValue; // ヒューリスティック値
	int fValue; // 評価値
	boolean hasGValue = false;
	boolean hasFValue = false;

	public void print(){
		System.out.println(this.toString());
		for(Node child : this.getChildren()){
			System.out.println("  children:"+child.getName()+":"+this.getCost(child));
		}
	}

	public void initialize(){
		hasGValue = false;
		hasFValue = false;
	}

	Node(String theName, int theHValue) {
		name = theName;
		children = new ArrayList<Node>();
		childrenCosts = new HashMap<Node,Integer>();
		hValue = theHValue;
	}

	public String getName() {
		return name;
	}

	public void setPointer(Node theNode) {
		this.pointer = theNode;
	}

	public Node getPointer() {
		return this.pointer;
	}

	public int getGValue() {
		return gValue;
	}

	public void setGValue(int theGValue) {
		hasGValue = true;
		this.gValue = theGValue;
	}

	public int getHValue() {
		return hValue;
	}

	public int getFValue() {
		return fValue;
	}

	public void setFValue(int theFValue) {
		hasFValue = true;
		this.fValue = theFValue;
	}

	
	/***
	 * theChild この節点の子節点 theCost その子節点までのコスト
	 */
	public void addChild(Node theChild, int theCost) {
		children.add(theChild);
		childrenCosts.put(theChild, new Integer(theCost));
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public int getCost(Node theChild) {
		return childrenCosts.get(theChild).intValue();
	}

	public String toString() {
		String result = name + "(h:" + hValue + ")";
		if (hasGValue) {
			result = result + "(g:" + gValue + ")";
		}
		if (hasFValue) {
			result = result + "(f:" + fValue + ")";
		}
		return result;
	}
}
