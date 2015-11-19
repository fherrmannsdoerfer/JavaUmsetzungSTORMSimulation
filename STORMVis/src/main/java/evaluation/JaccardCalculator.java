package evaluation;
/**
 * class calculates jaccard index out of ground truth and reckognised points
 * @author Niels Schlusser
 * @date 16112015
 */



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.io.*;

public class JaccardCalculator {

  /**
   * constructor
   */
  public JaccardCalculator() {
    
  }
  
  /**
   * main method
   * @param args
   */
  public static void main(String[] args) {
//    ArrayList<float[]> gr = new ArrayList<float[]>();
//    ArrayList<float[]> rec = new ArrayList<float[]>();
//    float[] a1 = {0,0,0,4,4};
//    gr.add(a1);
//    float[] a2 = {1,2,0,4,4};
//    rec.add(a2);
//    float[] b = {5,5,3,4,5};
//    gr.add(b);
//    rec.add(b);
//    float[] d = {1,1,3,4,4};
//    rec.add(d);
//    float[] c = {20,20,30,4,5};
//    rec.add(c);
//    gr.add(c);
//    
//    int[] perm = {1,0,2,3,4};
//    System.out.println(calcMeasure(gr, rec, 4, 5, "recall", perm));  
//       System.out.println(calcMeasure("C:\\Users\\Niels\\Desktop\\test.txt", 
//         "C:\\Users\\Niels\\Desktop\\test.txt", 1, 1, perm, perm)[6]);
  }
  
  /**
   * method calls calcMeasure with the files converted to lists
   * @param grTrFileName
   * @param recFileName
   * @param pTolR
   * @param pTolZ
   * @param pOutput
   * @param pPermVecGr
   * @param pPermVecRec
   * @return
   */
  public static float[] calcMeasure(String grTrFileName, String recFileName, float pTolR, float pTolZ, 
      float shiftx, float shifty, int[] pPermVecGr, int[] pPermVecRec) {
    return calcMeasure(convertFileToList(grTrFileName), convertFileToList(recFileName), pTolR, pTolZ, 
        shiftx, shifty, pPermVecGr, pPermVecRec);
  }

  /**
   * method calculates the required measure of congruence of the data sets
   * @param grTruth
   * @param recPoints
   * @param tolR : tolerance in radial direction
   * @param tolZ : tolerance in z-direction
   * @param output : kind of output-measure
   * @param permVec : permutation vector
   */
  private static float[] calcMeasure(List<float[]> grTruth, List<float[]> recPoints, float tolR, float tolZ, 
      float shiftx, float shifty, int[] permVecGr, int[] permVecRec) {
    int truePositive = 0;
    int falsePositive = 0;
    int falseNegative = 0;
    float deltaX = 0;
    float deltaY = 0;
    float rmseXY = 0;
    float rmseTot = 0;
    //permute the lists
    grTruth = permute(permVecGr, grTruth);
    recPoints = permute(permVecRec, recPoints);
    
    for(int i = 0; i<grTruth.size(); i++){
    	grTruth.get(i)[0] += shiftx;
    	grTruth.get(i)[1] += shifty;
    }
    
    while(grTruth.size() > 0) {
      //write points for current frame out of lists
      List<float[]> currGrTr = new ArrayList<float[]>();
      do {
        currGrTr.add(grTruth.get(0));
        grTruth.remove(0);
      }
      while(grTruth.size() > 0 && currGrTr.get(0)[3] == grTruth.get(0)[3]);
      
      while (recPoints.get(0)[3]<currGrTr.get(0)[3]){
    	  recPoints.remove(0);
    	  falsePositive += 1;
      }
      
      List<float[]> currRecPoints = new ArrayList<float[]>();
      do {
        if (recPoints.size()>0&&recPoints.get(0)[3] == currGrTr.get(0)[3]) {
          currRecPoints.add(recPoints.get(0));
          recPoints.remove(0);
        }
      }
      while(recPoints.size() > 0 && currRecPoints.size()>0&& currRecPoints.get(0)[3] == recPoints.get(0)[3] 
          && recPoints.get(0)[3] == currGrTr.get(0)[3]);
        
      //count how many points are recognized 
      int recPts = 0; 
      for(int j = 0; j < currGrTr.size(); j++) {
        for(int k = 0; k < currRecPoints.size(); k++) {
          if(isInEllipse(tolR, tolZ, currRecPoints.get(k), currGrTr.get(j)) == true) {
        	deltaX += currRecPoints.get(k)[0] - currGrTr.get(j)[0];
        	deltaY += currRecPoints.get(k)[1] - currGrTr.get(j)[1];
        	rmseXY += Math.pow(Math.pow(currRecPoints.get(k)[0] - currGrTr.get(j)[0], 2)+Math.pow(currRecPoints.get(k)[1] - currGrTr.get(j)[1], 2),1/2.0);
        	rmseTot += Math.pow(Math.pow(currRecPoints.get(k)[0] - currGrTr.get(j)[0], 2)+Math.pow(currRecPoints.get(k)[1] - currGrTr.get(j)[1], 2)+Math.pow(currRecPoints.get(k)[2] - currGrTr.get(j)[3], 2),1/3.);
            recPts++;
            currRecPoints.remove(k);
            k--;
          }
        }
      }
      
      //update statistical categories
      if(recPts <= currGrTr.size()) {
        truePositive += recPts;
        falseNegative += currGrTr.size() - recPts;
      }
      else{
        truePositive += currGrTr.size();
        falsePositive += recPts - currGrTr.size();
      }
    }
    deltaX /= truePositive;
    deltaY /= truePositive;
    rmseXY /= truePositive;
    rmseTot /= truePositive;
    //calculate precision and recall
    float r = truePositive;
    r /= (truePositive + falseNegative);
    float p = truePositive;
    p /= (truePositive + falsePositive);
    
    float[] re = new float[11];
    re[0] = tolR;
    re[1] = tolZ;
    re[2] = (float) falsePositive + truePositive;
    re[3] = (float) truePositive;
    re[4] = (float) falsePositive;
    float jaccard = truePositive;
    jaccard /= (truePositive + falsePositive + falseNegative);
    re[5] = jaccard;
    float f = 2*p*r;
    f /= (p+r);
    re[6] = f;
    re[7] = deltaX;
    re[8] = deltaY;
    re[9] = rmseXY;
    re[10] = rmseTot;
    return re;
  }
  
  /**
   * method converts text-file to a List of float[]-arrays
   * @param fileName
   * @return
   * @throws FileNotFoundException 
   */
  public static List<float[]> convertFileToList(String fileName) {
    
    List<float[]> data = new ArrayList<float[]>();
    FileReader fr;
    
    try {
      fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
            
      String currLine = br.readLine();
      currLine = br.readLine();//skipp header
      while(currLine != null) {
        char[] currChar = currLine.toCharArray();
                
        float[] currFloat = new float[5];
        int i = 0; //pointer 
        
        for (int j = 0; j < 5; j++) { 
          // skip spaces  
          while (i < currChar.length && currChar[i] == ' ') {
            i++;
          }
          
          //check whether line contains description, if so, skip it
          if(Character.isAlphabetic(currChar[i])) {
            break;
          }

          // save value in a char list
          ArrayList<Character> value = new ArrayList<Character>();
          while (i < currChar.length && currChar[i] != ' ') {
            value.add(currChar[i]);
            i++;
          }
          
          // convert to float
          char[] valueArray = new char[value.size()];
          for (int k = 0; k < value.size(); k++) {
            valueArray[k] = value.get(k);
          }
          String valueString = new String(valueArray);
          currFloat[j] = Float.parseFloat(valueString);
        }
        if(currFloat[0] != 0) data.add(currFloat);
        
        currLine = br.readLine();
        
        
      } 
      
      br.close();
      
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    
    return data;
  }
  
  /**
   * auxiliary method determines whether point lies in an ellipse
   * @param rHalfAxis
   * @param zHalfAxis
   * @param coordinates
   * @return
   */
  private static boolean isInEllipse(float rHalfAxis, float zHalfAxis, float[] coordinates, float[] ref) {
    float r = (float) Math.sqrt(Math.pow(coordinates[0]-ref[0], 2) + Math.pow(coordinates[1]-ref[1], 2));
    double h = Math.pow(r/rHalfAxis, 2) + Math.pow((coordinates[2]-ref[2])/zHalfAxis, 2);
    if(h <= 1) {
      return true;
    }
    else{
      return false;
    }
  }
  
  
  /**
   * method permutes input list
   * @param perm :  permutation vecotr
   * @param inp : input list
   * @return
   */
  public static List<float[]> permute(int[] perm, List<float[]> inp) {
    //check whether permutation vector is valid
    if(isPermVec(perm) == false) {
      System.out.println("invalid permutation vector");
      return null;
    }
    else{
      for(int i = 0; i < inp.size(); i++) {
        float[] tmp = new float[5];
        for(int k = 0; k < 5; k++) {
          tmp[k] = inp.get(i)[k];
        }
        for(int j = 0; j < inp.get(i).length; j++) {
          inp.get(i)[j] = tmp[perm[j]];
        }
      }
      return inp;
    }
  }
  
  /**
   * method checks whether permutation vector is valid
   * @param per : permutation vector
   * @return validness of permutation vector
   */
  private static boolean isPermVec(int[] per) {
    HashSet<Integer> h = new HashSet<Integer>();
    for(int i = 0; i < per.length; i++) {
      h.add(per[i]);
    }
    if(h.size() != 5) {return false;}
    else{
      if(h.contains(0) == false || h.contains(1) == false || h.contains(2) == false || 
          h.contains(3) == false || h.contains(4) == false) {
        return false;
      }
      else {
        return true;
      }
    }
  }
  
}
