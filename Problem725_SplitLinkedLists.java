/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode[] splitListToParts(ListNode root, int k) {
        ListNode node = root;
        int len =0;
        while(node != null){
            len++;
            node = node.next;
        }

        int q = len / k;
        int r = len % k;
        ListNode head = root;
        node = root;
        List<ListNode> listnodes = new ArrayList<ListNode>();
        while (node!=null){
            int next_lim = r>0?q:q-1;
            for(int i=0; i<next_lim; i++){
                node = node.next;
            }
            if(r>0){
                r--;
            }
            ListNode temp = node;
            node = node.next;
            temp.next = null;
            listnodes.add(head);
            head = node;

        }
        while(listnodes.size()<k){
            ListNode temp = null;
            listnodes.add(temp);
        }
        return listnodes.toArray(new ListNode[0]);
    }
}