package com.nopu70.tools;

import java.util.ArrayList;

public class Trie {
	private int SIZE = 26;
	private TrieNode root;// 字典树的根

	public Trie() {// 初始化字典树
		root = new TrieNode();
	}

	private class TrieNode {// 字典树节点
		private int num;// 有多少单词通过这个节点,即节点字符出现的次数
		private TrieNode[] son;// 所有的儿子节点
		private boolean isEnd;// 是不是最后一个节点
		private char val;// 节点的值

		TrieNode() {
			num = 1;
			son = new TrieNode[SIZE];
			isEnd = false;
		}
	}

	// 建立字典树
	public void insert(String str) {// 在字典树中插入一个单词
		if (str == null || str.length() == 0) {
			return;
		}
		TrieNode node = root;
		char[] letters = str.toCharArray();
		for (int i = 1, len = str.length(); i < len; i++) {
			int pos = letters[i] - 'a';
			if (pos>=26||pos<0){
				return;
			}
			if (node.son[pos] == null) {
				node.son[pos] = new TrieNode();
				node.son[pos].val = letters[i];
			} else {
				node.son[pos].num++;
			}
			node = node.son[pos];
		}
		node.isEnd = true;
	}

	// 计算单词前缀的数量
	public int countPrefix(String prefix) {
		if (prefix == null || prefix.length() == 0) {
			return -1;
		}
		TrieNode node = root;
		char[] letters = prefix.toCharArray();
		for (int i = 0, len = prefix.length(); i < len; i++) {
			int pos = letters[i] - 'a';
			if (node.son[pos] == null) {
				return 0;
			} else {
				node = node.son[pos];
			}
		}
		return node.num;
	}

	// 在字典树中查找一个完全匹配的单词.
	public boolean has(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		TrieNode node = root;
		char[] letters = str.toCharArray();
		for (int i = 0, len = str.length(); i < len; i++) {
			int pos = letters[i] - 'a';
			if (node.son[pos] != null) {
				node = node.son[pos];
			} else {
				return false;
			}
		}
		return node.isEnd;
	}

	public ArrayList<String> hasPrefix(String prefix) {
		ArrayList<String> list = new ArrayList<String>();
		if (prefix == null || prefix.length() == 0) {
			return null;
		}
		TrieNode node = root;
		char[] letters = prefix.toCharArray();
		for (int i = 0, len = prefix.length(); i < len; i++) {
			int pos = letters[i] - 'a';
			if (node.son[pos] == null) {
				return list;
			} else {
				node = node.son[pos];
			}
		}

		preTraverse(node, prefix, list);

		return list;
	}

	public void preTraverse(TrieNode node, String prefix, ArrayList<String> array) {
		if (array.size()<10) {
			if (node.isEnd){
				array.add(prefix);
			}
			for (TrieNode child : node.son) {
				if (child != null) {
					preTraverse(child, prefix + child.val, array);
				}
			}
			return;
		}
	}

	public TrieNode getRoot() {
		return this.root;
	}

}
