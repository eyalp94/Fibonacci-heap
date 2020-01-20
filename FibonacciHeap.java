import java.util.Arrays;
// eyalpintzov 205685167
// itayshalev 206283228

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap
{
	public HeapNode lst;
	public HeapNode min;
	public int size;
	public int numOfMarks;
	public int numOfTrees;
	
	public static int totalCuts;
	public static int totalLinks;
	
	
	public FibonacciHeap() // create new FibonacciHeap
	{
		this.lst = null;
		this.min = null;
		this.size = 0;
		this.numOfTrees = 0;
		this.numOfMarks = 0;
	}

   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	return size == 0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)// insert new HeapNode as a new tree to the heap
    {   
    	HeapNode node = new HeapNode(""+key, key);
    	if(this.lst != null)
    	{
    		HeapNode last = this.lst.prev;
        	HeapNode first = this.lst;
        	SetBrother(node,first);
        	SetBrother(last,node);
    	}    
    	this.lst = node;
    	this.setMin(node);   
    	this.size ++;
    	this.numOfTrees ++;
    	return node;
    
    }
    
    public void addAtStart(HeapNode node)//adding a root to the first layer
    {
    	HeapNode last = this.lst.prev;
    	HeapNode first = this.lst;
    	SetBrother(node,first);
    	SetBrother(last,node);
    	this.lst = node;
    	this.numOfTrees ++;
    }
    
    public void setMin(HeapNode node)// check if a node key is lower than the min set the min
    {
    	if(this.min == null)
    	{
    		this.min = node;
    	}
    	else if(node.key < this.min.key)
    	{
    		this.min = node;
    	}
    }
    
    
    public void SetBrother(HeapNode first, HeapNode other)//set the first next to be other and other prev to be first
    {
    	first.next = other;
    	other.prev = first;
    	//System.out.println("We Set the next of " + first.key + " to be " + other.key);
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	if(!this.actualDel())
    	{
    		this.fromBuckets(this.toBuckets());
    	}
    }
   
    public boolean actualDel() // delete the min node return true if deleted the last node of the tree, else false
    {
    	this.numOfTrees --;
    	this.size--;
    	
    	boolean seen = false;
     	HeapNode child = this.min.child;
     	//System.out.println(child);
     	HeapNode current;
     	HeapNode nodeToStart; // remember the first node added to the roots.
     	if (child != null)
     	{
     		if(this.min.next == this.min)
     		{
     			while(child != this.min.child || !seen )
         		{
         			seen = true;
         			this.numOfMarks -= child.mark;
         			child.mark = 0;
         			child.parent = null;
         			child = child.next;
         			//System.out.println(child);
         		}
     			this.min = null;
     			this.numOfTrees += this.lst.rank;
     			this.lst = child;
     			return false;
     		}
     		nodeToStart = this.min.child;
     		while(child != this.min.child || !seen )
     		{
     			seen = true;
     			this.numOfMarks -= child.mark;
     			child.mark = 0;
     			current = child;
     			child = child.next;
     			
     			
     			this.addAtStart(current);
     			current.parent = null;
     			
     			//System.out.println(child.key);
     		}
     	}
     	else
     	{
     		if(this.min.next == this.min)
     		{
     			this.lst = null;
     			this.min = null;
     			return true;
     		}
     		nodeToStart = this.min.next;
     	}
     	this.SetBrother(this.min.prev, this.min.next);
     	if(this.lst == this.min)
     	{
     		//System.out.println("Need to print 9: " + nodeToStart);
     		this.lst = nodeToStart;
     	}
     	this.min = null;
     	//System.out.println("lst pointer: " + this.lst + "" + this.min);
     	return false;
    }
    
    public HeapNode link(HeapNode first, HeapNode second)// perform link between 2 binomial trees
    {
    	HeapNode large;
    	HeapNode small;
    	totalLinks ++;
    	if(first.key <= second.key)
    	{
    		large = second;
    		small = first;
    	}
    	else
    	{
    		large = first;
    		small = second;
    	}
    	//this.setMin(small);
    	HeapNode nextlst = large.next;
    	if(large.prev.next == null)
    	{
    		large.next.prev = large.prev;
    	}
    	else
    	{
    		large.prev.next = large.next;
    		if(large.next != null)
    		{
    			large.next.prev = large.prev;
    		}
    		else
    		{
    			this.lst.prev = large.prev;
    		}
    	}
    	if(small.child == null)
    	{
    		small.child = large;
    		large.parent = small;
    		this.SetBrother(large, large);
    	}
    	else
    	{
    		large.parent = small;
    		HeapNode child = small.child;
        	HeapNode childPrev = child.prev;
        	this.SetBrother(large, child);
        	this.SetBrother(childPrev, large);
    	}
    	if(this.lst == large)
    	{
    		this.lst = nextlst;
    	}
    	small.rank ++;
    	this.numOfTrees --;
    	return small;
    	
    }
    
    public HeapNode[] toBuckets()//create array of linked trees, if arr[i] != null -> arr[i].rank = i
    {
    	HeapNode[] B = new HeapNode[(int)Math.round((Math.log10(this.size)/Math.log10(2))) + 1];
    	HeapNode x = this.lst;
    	HeapNode y;
    	for(int i = 0; i < B.length ; i++)
    	{
    		B[i] = null;
    	}
    	x.prev.next = null;
    	//System.out.println("The X prev is: " + x.prev.key);
    	while(x != null)
    	{
    		//System.out.println("The X is: " + x.key);
    		y = x;
    		x = x.next;
    		while(B[y.rank] != null)
    		{
    			//System.out.println("The key is: " + y.getKey() + " the key is:" + B[y.rank].key);
    			y = this.link(y, B[y.rank]);
    			//System.out.println("The key is: " + y.getKey() + " the rank is:" + y.rank);
    			B[y.rank - 1] = null;
    		}
    		B[y.rank] = y;
    	}
    	return B;
    }
    

    public void fromBuckets(HeapNode[] B)//create new root list
    {
    	HeapNode x = null;
    	HeapNode next;
    	for(int i = 0; i < B.length; i++)
    	{
    		if(B[i] != null)
    		{
    			this.setMin(B[i]);
    			if(x == null)
    			{
    				x = B[i];
    				x.next = x;
    				x.prev = x;
    			}
    			else
    			{
    				next = x.next;
    				this.SetBrother(x, B[i]);
    				this.SetBrother(B[i], next);
    			}
    		}
    	}
    	this.lst = x;
    }
    
   
    /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
  
    /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	HeapNode first2 = heap2.lst;
    	HeapNode last2 = first2.prev;
    	HeapNode first = this.lst;
    	HeapNode last = first.prev;
    	this.SetBrother(last, first2);
    	this.SetBrother(last2, first);
    	if(this.min.key > heap2.min.key)
    	{
    		this.min = heap2.min;
    	}
    	this.size += heap2.size;
    	this.numOfMarks += heap2.numOfMarks;
    	this.numOfTrees += heap2.numOfTrees;
    }

   
    /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	if(this.lst == null)
    	{
    		return new int[0];
    	}
    	int[] arr = new int[this.size];
    	boolean seenFirst = false;
    	int i = this.size-1;
    	HeapNode node = this.lst;
    	while(node != this.lst || !seenFirst)
    	{
    		seenFirst = true;
    		arr[node.rank] ++;
    		node = node.next;
    	}
    	while(arr[i] == 0)
    	{
    		i--;
    	}
    	int [] arr2 = new int[i+1];
    	for(int j = 0; j < arr2.length ;j++)
    	{
    		arr2[j] = arr[j];
    	}
    	return arr2;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, x.key + 1);
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.key -= delta;
    	if(x.parent != null)
    	{
    		if(x.key < x.parent.key)
    		{
    			this.cascadingCut(x);
    		}
    	}
    	this.setMin(x);
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.numOfTrees + 2 * this.numOfMarks;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }
    
    public void cut(HeapNode node)// cut the node from its parent
    {
    	totalCuts ++;
    	HeapNode parent = node.parent;
    	node.parent = null;
    	this.numOfMarks -= node.mark;
    	node.mark = 0;
    	parent.rank --;
    	if(node.next == node)
    	{
    		parent.child = null;
    	}
    	else
    	{
    		parent.child = node.next;
    		node.prev.next = node.next;
    		node.next.prev = node.prev;
    	}
    	this.addAtStart(node);
    }
    
    public void cascadingCut(HeapNode node)//perform cascading cut from x
    {
    	HeapNode parent = node.parent;
    	this.cut(node);
    	if(parent.parent != null)
    	{
    		if(parent.mark == 0)
    		{
    			parent.mark = 1;
    			this.numOfMarks ++;
    		}
    		else
    		{
    			this.cascadingCut(parent);
    		}
    			
    	}
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    
    	public String value;
    	public int key;
    	public int rank;
    	public int mark; // 0 - off 1 - on
    	public HeapNode child;
    	public HeapNode next;
    	public HeapNode prev;
    	public HeapNode parent;
	

    	public HeapNode(String value, int key) {
    		this.value = value;
    		this.key = key;
    		this.rank = 0;
    		this.mark = 0;
    		this.child = null;
    		this.next = this;
    		this.prev = this;
    		this.parent = null;
    	}

    	public int getKey() {
    		return this.key;
    	}

    }

    
}
