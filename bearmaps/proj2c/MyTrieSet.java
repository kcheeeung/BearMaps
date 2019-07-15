package bearmaps.proj2c;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * MyTrieSet
 * @source StringBuilder technique from Princeton Library
 */
public class MyTrieSet implements TrieSet61B {
    private Node root;

    private class Node {
        private char ch;
        private boolean isKey;
        private HashMap<Character, Node> map;
        
        public Node() {
            map = new HashMap<>();
        }

        public Node(char c, boolean b) {
            ch = c;
            isKey = b;
            map = new HashMap<>();
        }
    }

    public MyTrieSet() {
        root = new Node(); //first dummy node
    }

    @Override
    public void add(String str) {
        if (str == null || str.length() < 1) {
            return;
        }
        Node current = root;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!current.map.containsKey(c)) {
                current.map.put(c, new Node(c, false));
            }
            current = current.map.get(c);
        }
        current.isKey = true;
    }

    @Override
    public boolean contains(String key) {
        boolean isFound = true; // assume true
        Node current = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (current.map.containsKey(c)) {
                current = current.map.get(c);
                isFound = current.isKey;
            } else {
                isFound = false;
                break;
            }
        }
        return isFound;
    }

    @Override
    public void clear() {
        root = null;
        root = new Node();
    }

    @Override
    public List<String> keysWithPrefix(String prefix) {
        List<String> x = new LinkedList<>();
        Node current = root;
        // Test if has prefix
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (current.map.containsKey(c)) {
                current = current.map.get(c);
            }
        }

        if (current.isKey) {
            x.add(prefix);
        }
        Set<Character> keys = current.map.keySet();
        for (char c : keys) {
            keyPrefixHelper(current.map.get(c), x, new StringBuilder(prefix));
        }
        return x;
    }

    private void keyPrefixHelper(Node n, List<String> mylist, StringBuilder prefix) {
        if (n.isKey) {
            mylist.add(prefix.append(n.ch).toString());
            prefix.deleteCharAt(prefix.length() - 1);
        }

        Set<Character> keys = n.map.keySet();
        for (char c : keys) {
            keyPrefixHelper(n.map.get(c), mylist, new StringBuilder(prefix.append(n.ch)));
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    @Override
    public String longestPrefixOf(String key) {
        throw new UnsupportedOperationException();
    }

    // public static void main(String[] args) {
    //     MyTrieSet t = new MyTrieSet();
    //     t.add("heax");
    //     t.add("heay");
    //     t.add("heaz");
    //     t.add("heb");
    //     t.add("hec");
    //     t.add("heiiii");
    //     t.add("hniiii");
    //     t.add("h16");
    //     t.add("h");
    //     // System.out.println(t.contains("hello"));
    //     // System.out.println(t.contains("hell"));
    //     // System.out.println(t.contains("he"));
    //     System.out.println(t.keysWithPrefix("h"));
    //     System.out.println(t.keysWithPrefix("he"));

    //     String[] saStrings = new String[]{"same", "sam", "sad", "sap"};
    //     String[] otherStrings = new String[]{"a", "awls", "hello"};
    //     MyTrieSet ts = new MyTrieSet();
    //     for (String s : saStrings) {
    //         ts.add(s);
    //     }
    //     System.out.println(ts.keysWithPrefix("sa"));
    // }
}
