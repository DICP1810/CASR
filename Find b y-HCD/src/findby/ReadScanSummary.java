/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package findby;

/**
 *
 * @author admin08
 */
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author shan
 */
//revised on 2012/12/03
//read the scan summary file, find the exerimental spectrum for each scan
//due to the existing of non-unique peptides, in scansummary file,there is redundant dta file name, which doesn't affect the result
////需要定制的部分:读取scan summary文件时,需要读入扫描数(或者每个扫描的文件名),肽的序列,肽的修饰.首先是获得每列的标题,然后读入每行数据,根据扫描数
//读入mgf文件中队应的二级质谱图.

public class ReadScanSummary {
    //this is to define the style of summary files. 
    // for summary file from maxquant, it is 0; for file created from pbuild as described in wuqi's paper, it is 1;
    public int summaryFileStyle=0;
    public List<String> scanSummary0=new ArrayList<>();
    //the data of scan summary file, containing all of the columns
    public HashMap<String,List<String>> scanSummary=new LinkedHashMap<>();
    private File scanSummaryFile;
    private File mgfFile;
    private HashMap<String,Integer> expMSMSBegin=new LinkedHashMap<>();
    private HashMap<String,Integer> expMSMSEnd=new LinkedHashMap<>();
    //store all of the experimental ms/ms listed in the scan summary file
    public HashMap<String,HashMap<Double,Double>> expMSMS=new LinkedHashMap<>();
    public HashMap<String,Double> expMass=new LinkedHashMap<>();
    public HashMap<String,Double> expRT=new LinkedHashMap<>();
    public HashMap<String,Integer> expCharge=new LinkedHashMap<>();
    public HashMap<String,Integer> scanOrder=new LinkedHashMap<>();
    ///////1 stands for label free, 2 stands for silac
    public int option=1;
    ////
    public HashMap<String,Integer> MS2Num=new LinkedHashMap<>();
    ////
    public  ReadScanSummary(List<String> scansummary0,int style){
        scanSummary0=scansummary0;
        readMascot();
    }
    public void readMascot(){
      
           String dataRow;
     
            String pepName;
           
            String modName;
            
          
            List<String> pepN=new ArrayList<>();
           
            List<String> modN=new ArrayList<>();
            List<String> charge=new ArrayList<>();
           
            List<String> scanFile=new ArrayList<>();
           
            List<String> id=new ArrayList<>();
            //System.out.println(scanSummary0.size());
            for (int i=0;i<scanSummary0.size();i++){
                ////
                dataRow=scanSummary0.get(i);
                id.add(dataRow);
                String[] dataArray=dataRow.split("\\t");
                //System.out.println(dataRow);
             
                pepName=dataArray[1];
               
                modName=dataArray[2];
                
            
                pepN.add(pepName);
          
                modN.add(modName);
              
                charge.add(dataArray[3]);
              
                scanFile.add(dataArray[0]);
               
            }
      
            scanSummary.put("Sequence",pepN);
         
            scanSummary.put("Modified Sequence",modN);
            scanSummary.put("Charge",charge);
           
         
            scanSummary.put("scanFile",scanFile);
          
            scanSummary.put("id",id);
          

    }
 
   public void findExpMSMSMascot(File mgffile){
        expMSMS.clear();
        expMSMSBegin.clear();
        expMSMSEnd.clear();
        mgfFile=mgffile;
        scanOrder.clear();
        //should be adjusted according to the file format
        List<String> allDtaFile=scanSummary.get("scanFile");
        //System.out.println(allDtaFile.get(0));
        Pattern p1=Pattern.compile("END IONS");
        Matcher m1;
        int n=0;
        int lineNumber=0;
        String data;
        String[] dataArray;
        String dtaFileName="";
        String[] mzAndIntensity=new String[2];
        int nnn=0;
        try{
            BufferedReader MGFFile =new BufferedReader(new FileReader(mgfFile));
            String dataRow;
            while ((dataRow=MGFFile.readLine())!=null){
                lineNumber=lineNumber+1;
                //System.out.println(dataRow);
                if(dataRow.indexOf("TITLE")!=-1)
                {
                    n=n+1;
                    String[] temp=dataRow.split("=");
                
                    //System.out.println(temp1[1]);
                    if(allDtaFile.indexOf(temp[1])!=-1){
                        dtaFileName=temp[1];
                        System.out.println(dtaFileName);
                        scanOrder.put(dtaFileName,n);
                        expMSMSBegin.put(dtaFileName,lineNumber+4);
                        data=MGFFile.readLine();
                        if(data.charAt(0)=='C'){
                            String cha=String.valueOf(data.charAt(7));
                            expCharge.put(dtaFileName,Integer.parseInt(cha));
                            data=MGFFile.readLine();
                            dataArray=data.split("=");
                            expRT.put(dtaFileName,Double.parseDouble(dataArray[1])/60.0);
                            data=MGFFile.readLine();
                            dataArray=data.split("=");
                            expMass.put(dtaFileName,Double.parseDouble(dataArray[1]));
                            lineNumber=lineNumber+3;
                        }
                        else{
                            dataArray=data.split("=");
                            expRT.put(dtaFileName,Double.parseDouble(dataArray[1])/60.0);
                            data=MGFFile.readLine();
                            dataArray=data.split("=");
                            expMass.put(dtaFileName,Double.parseDouble(dataArray[1]));
                            lineNumber=lineNumber+2;
                        }
                        
                        HashMap<Double,Double> oneSpectrum=new LinkedHashMap<>();
                        double preMz=0,curMz=0,preIn=0,curIn=0;
                        int indexes=0;
                        while((dataRow=MGFFile.readLine())!=null){
                            lineNumber=lineNumber+1;
                            m1=p1.matcher(dataRow);
                            if(!m1.find()){
                                mzAndIntensity=dataRow.split(" ");
                                curMz=Double.parseDouble(mzAndIntensity[0]);
                                curIn=Double.parseDouble(mzAndIntensity[1]);
                                if(oneSpectrum.get(curMz)==null){
                                    oneSpectrum.put(curMz,curIn);
                                    indexes=0;
                                }
                                else{
                                    indexes=indexes+1;
                                    oneSpectrum.put(curMz+0.00001*indexes,curIn);
                                }
                            }
                            else{
                                expMSMSEnd.put(dtaFileName,lineNumber-1);
                                //sorted the mass spectrum to be in the order of descending intensities
                                List<Map.Entry<Double,Double>> entries = new ArrayList<Map.Entry<Double,Double>>(oneSpectrum.entrySet());
                                Collections.sort(entries, new Comparator<Map.Entry<Double,Double>>() {
                                    public int compare(Map.Entry<Double,Double> e1, Map.Entry<Double,Double> e2) {
                                        return e2.getValue().compareTo(e1.getValue());
                                    }
                                });
                                // Put entries back in an ordered map.
                                HashMap<Double, Double> orderedSpectrum = new LinkedHashMap<>();
                                for (Map.Entry<Double,Double> entry : entries) {
                                    orderedSpectrum.put(entry.getKey(), entry.getValue());
                                }
                                nnn++;
                                if(nnn==1){
                                    //System.out.println(orderedSpectrum);
                                    //System.out.println(dtaFileName);
                                    //System.out.println(lineNumber-1);
                                }
                                expMSMS.put(dtaFileName,orderedSpectrum);
                                break;
                            }
                        }
                    }
                }
                    
            }
            MGFFile.close();
            //System.out.println("tag1");
            //System.out.println(expMSMS.size());

        }
        catch(FileNotFoundException e){
            System.out.println("no file found");
        }
        catch(IOException e){
            ;
        }
        System.out.println("succs");
    }
    public void findMS2Number(File mgffile){
        try{  
        BufferedReader MGFFile =new BufferedReader(new FileReader(mgffile));
          String dataRow;
          while ((dataRow=MGFFile.readLine())!=null){
              if(dataRow.indexOf("TITLE")!=-1)
              {
                  String[] temp=dataRow.split(" ");
                  String[] temp1=temp[0].split("=");
                  String[] scans=temp1[1].split("\\.");
                  MS2Num.put(scans[3],Integer.parseInt(scans[4]));
              }
            }
            MGFFile.close();
        }
         catch(FileNotFoundException e){
            System.out.println("no file found");
        }
        catch(IOException e){
            ;
        }

    }
}
