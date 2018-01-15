class Solution {
    public int[] pourWater(int[] heights, int V, int K) {
        for(int i =0; i< V; i++){            
            int lowestleft = K>0?findleftlowest(heights,K):-1;
            int lowestright = K<heights.length-1?findrightlowest(heights, K):-1;
            if( lowestleft >=0 && heights[lowestleft]<heights[K]){
                heights[lowestleft] +=1;
                continue;
            }           
            if(lowestright >=0 && heights[lowestright]<heights[K]){
                heights[lowestright] +=1;
                continue;
            }
            heights[K] +=1;
            
        }        
        return heights;
    }
    
    public int findleftlowest(int [] heights, int K){
        int min = K;
        for(int j=K-1; j>-1 && heights[j] <= heights[j+1]; j-- ){
            if (heights[j] < heights[min]){
                min = j;
            } 
        }
        return min;
    }
    
        
    public int findrightlowest(int [] heights, int K){
        int min = K;
        for(int j=K+1; j<heights.length && heights[j] <= heights[j-1]; j++ ){
            if (heights[j] < heights[min]){
                min = j;
            } 
        }
        return min;
    }
}