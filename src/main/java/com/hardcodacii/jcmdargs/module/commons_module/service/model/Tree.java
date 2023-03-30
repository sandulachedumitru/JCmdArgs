package com.hardcodacii.jcmdargs.module.commons_module.service.model;

import lombok.Getter;

@Getter
public class Tree {
	private Tree parentElem, lastChildElem, backOrderElem, nextOrderElem;
	private TreeProperties treeProperties;
	private Tree root;

	private int levelInTheTree = -1;
	private int numberOfChilds = 0;

	private Tree() {
	}

	public static Tree createNewTree(TreeProperties treeProperties) {
		Tree root = new Tree();
		root.levelInTheTree = 0;
		root.parentElem = null;
		root.backOrderElem = null;
		root.nextOrderElem = null;
		root.root = root;
		root.treeProperties = treeProperties;

		return root;
	}

	// null -> child -> null
	// null -> el -> child -> null
	// null -> child -> el -> null
	// null -> el -> child -> el -> null
	public static boolean deleteChild(Tree child) {
		if (child == null) {
			System.err.println("ERROR: child is null");
			return false;
		}

		Tree parent = child.getParentElem();
		Tree brotherBack = child.getBackOrderElem();
		Tree brotherNext = child.getNextOrderElem();

		if (child.equals(child.root)) {
			System.out.println("delete root");
			System.out.println("child [" + child + "] is actually the root of tree");
			child = null;
		} else if (brotherBack == null && brotherNext == null) {
			System.out.println("null -> child -> null");
			parent.numberOfChilds--;
			if (child.equals(parent.lastChildElem)) parent.lastChildElem = null;
			else {
				System.err.println("ERROR: child [" + child + "] had to be last child elem but it isn't");
				return false;
			}
		} else if (brotherBack != null && brotherNext == null) {
			System.out.println("null -> el -> child -> null");
			parent.numberOfChilds--;
			brotherBack.nextOrderElem = brotherNext;
			if (child.equals(parent.lastChildElem)) parent.lastChildElem = brotherBack;
		} else if (brotherBack == null && brotherNext != null) {
			System.out.println("null -> child -> el -> null");
			parent.numberOfChilds--;
			brotherNext.backOrderElem = brotherBack;
			if (child.equals(parent.lastChildElem)) {
				System.err.println("ERROR: child [" + child + "] cannot be the last element");
				return false;
			}
		} else if (brotherBack != null && brotherNext != null) {
			System.out.println("null -> el -> child -> el -> null");
			parent.numberOfChilds--;
			brotherBack.nextOrderElem = brotherNext;
			brotherNext.backOrderElem = brotherBack;
			if (child.equals(parent.lastChildElem)) {
				System.err.println("ERROR: child [" + child + "] cannot be the last element");
				return false;
			}
		}

		System.out.println("Child was successfuly deleted");
		return true;
	}

	public static void main(String[] aargs) {
		TreeProperties treeProperties = new TreeProperties() {
		};
		Tree root, child, back, next;

		root = createNewTree(treeProperties);
		deleteChild(root);

		System.out.println();

		// null -> child -> null
		root = createNewTree(treeProperties);
		child = root.addNewChild(treeProperties);
		deleteChild(child);

		System.out.println();

		// null -> el -> child -> null
		root = createNewTree(treeProperties);
		back = root.addNewChild(treeProperties);
		child = root.addNewChild(treeProperties);
		deleteChild(child);

		System.out.println();

		// null -> child -> el -> null
		root = createNewTree(treeProperties);
		child = root.addNewChild(treeProperties);
		next = root.addNewChild(treeProperties);
		deleteChild(child);

		System.out.println();

		// null -> el -> child -> el -> null
		root = createNewTree(treeProperties);
		back = root.addNewChild(treeProperties);
		child = root.addNewChild(treeProperties);
		next = root.addNewChild(treeProperties);
		deleteChild(child);

	}

	public Tree addNewChild(TreeProperties treeProperties) {
		Tree newChildElem = new Tree();
		newChildElem.root = this.root;
		newChildElem.treeProperties = treeProperties;
		newChildElem.levelInTheTree = levelInTheTree + 1;
		newChildElem.parentElem = this;
		newChildElem.nextOrderElem = null;
		newChildElem.backOrderElem = lastChildElem;
		if (lastChildElem != null) lastChildElem.nextOrderElem = newChildElem;

		numberOfChilds++;

		lastChildElem = newChildElem;
		return newChildElem;
	}
}
