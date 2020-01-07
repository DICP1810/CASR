
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package findby;


import java.io.*;
import java.util.*;
import java.util.Map.Entry; 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author admin08
 */
//
//for label free quantification, no heavy ions anymore
public class UnlabelCalc {
    List<String> allResult=new ArrayList<>();
    List<String> pepVarMods=new ArrayList<>();
    List<String> pepSeq=new ArrayList<>();
    List<String> pepCharge=new ArrayList<>();

    // style 1 stands for summary file created by pbuild;style 0 stands for maxquant
    int Style;
    //whether consider the overlap between one ion isotope with other monoisotopic ion
    Boolean considerIso=false;
    public PeptideFragmentation lightPep;
    public ReadScanSummary expPeptide;
    public List<List<Double>> IonCounts=new ArrayList<>(); 
    public List<Double> IonTotalIntensity=new ArrayList<>(); 
    public List<List<String>> ionNames=new ArrayList<>(); 
    public List<List<Double>> ionMz=new ArrayList<>(); 
    public List<String> scans=new ArrayList<>();
    public List<Double> totalSMT=new ArrayList<>();
    //
    HashMap<Character,Double> VarMod=new LinkedHashMap<>();
    HashMap<Character,Double> FixMod=new LinkedHashMap<>();
    File scanSummaryFile;
    File mgfFile;
    //the precision of instrument for MSMS ion
    double threshold=0.5;
    double doubleIonThres=200;
    double mzThreshold=3000;
    //for label free or silac-mascot
    public int option=1;
    
    public UnlabelCalc(File mgffile,List<String> scansummary0,int style){
        expPeptide=new ReadScanSummary(scansummary0,style);
        expPeptide.findExpMSMSMascot(mgffile);
        //expPeptide.findMS2Number(mgffile);
    }
    public void setThreshold(double thres){
        threshold=thres;
    }
    public void writeData(File scanSummaryFile){
        writeAllIonMascot(scanSummaryFile); 
    }
    //also need to change this function at the output for label free or silac
    public void writeAllIonMascot(File outputFile){
         try{
       
             String scans="";   
             String sames="";
             String scanms1="";
             String[] datas;
             FileWriter outFile = new FileWriter(outputFile);
             
                PrintWriter out = new PrintWriter(outFile);
                int line=IonCounts.size();
                
                List<Map.Entry<String,Double>> entries=new ArrayList<>();
                //if(option==2)
                    //out.println("same"+"\t"+"pepmass"+"\t"+"RT"+"\t"+"scan file"+"\t"+"light heavy"+"\t"+"Proteins"+"\t"+"Sequence"+"\t"+"Unique"+"\t"+"Scan Number"+"\t"+"Ion Name"+"\t"+"Ion Number"+"\t"+"Total Ion Intensity"+"\t"+"SMT"+"\t"+"ion name"+"\t"+"ion count"+"\t"+"ion mz");
                //if(option==1)
                    //out.println("Proteins"+"\t"+"Sequence"+"\t"+"Unique"+"\t"+"Scan Number"+"\t"+"Ion Name"+"\t"+"Ion Number"+"\t"+"Total Ion Intensity"+"\t"+"SMT"+"\t"+"ion name"+"\t"+"ion count"+"\t"+"mz");
                out.println("Sequence"+"\t"+"scanFile"+"\t"+"Ion Name"+"\t"+"Ion Number"+"\t"+"ion name"+"\t"+"ion count"+"\t"+"mz");
                //out.println("pepmass"+"\t"+"Proteins"+"\t"+"Sequence"+"\t"+"Unique"+"\t"+"Scan Number"+"\t"+"Ion Name"+"\t"+"Ion Number"+"\t"+"Total Ion Intensity"+"\t"+"SMT"+"\t"+"ion name"+"\t"+"ion count");
                
                for(int i=0;i<line;i++){
                    HashMap<String,Double> ions=new LinkedHashMap<>();
                    HashMap<String,Double> ionsMz=new LinkedHashMap<>();
                    //System.out.println(IonCounts.get(i).size());
                    //System.out.println(ionNames.get(i));
                    for(int j=0;j<IonCounts.get(i).size();j++)
                        ions.put(ionNames.get(i).get(j),IonCounts.get(i).get(j));
                    entries = new ArrayList<Map.Entry<String,Double>>(ions.entrySet());
                    Collections.sort(entries, new Comparator<Map.Entry<String,Double>>(){
                            public int compare(Map.Entry<String,Double> e1, Map.Entry<String,Double> e2) {
                                return e2.getValue().compareTo(e1.getValue());
                            }
                        });
                    ////////
                    for(int j=0;j<IonCounts.get(i).size();j++)
                        ionsMz.put(ionNames.get(i).get(j),ionMz.get(i).get(j));
                    
                    ////////
                    if(ionNames.get(i).size()>0){
                       String name=entries.get(0).getKey();
                       ///adjust to the injection time
                       datas=expPeptide.scanSummary.get("scanFile").get(i).split("\\.");
                       //the first scan is ms1, so minus 1
                       //ms2num=expPeptide.MS2Num.get(datas[3])-1;
                       /////////////////////////////////
                       //remove the ms2num?? what is it?
                       String count=Double.toString(entries.get(0).getValue());
                       String mz=Double.toString(ionsMz.get(entries.get(0).getKey()));
                       
                       for(int j=1;j<ionNames.get(i).size();j++){
                           name=name+","+entries.get(j).getKey();
                           //remove the ms2num( very strange);
                           count=count+","+Double.toString(entries.get(j).getValue());
                           mz=mz+","+Double.toString(ionsMz.get(entries.get(j).getKey()));
                       }
                       if(i==0){
                           /*System.out.println(name);
                           System.out.println(count);
                           System.out.println(mz);*/
                       }
                       //should the number of ion be smaller (1)?
                       if(ionNames.get(i).size()>=1){
                       //if(ionNames.get(i).size()>=3){
                           scans=expPeptide.scanSummary.get("scanFile").get(i);
                           datas=expPeptide.scanSummary.get("scanFile").get(i).split("\\.");
                        
                               
                           out.println(expPeptide.scanSummary.get("Sequence").get(i)+"\t"+expPeptide.scanSummary.get("scanFile").get(i)+"\t"+ionNames.get(i)+"\t"+Integer.toString(ionNames.get(i).size())+"\t"+name+"\t"+count+"\t"+mz);
                           allResult.add(expPeptide.scanSummary.get("Sequence").get(i)+"\t"+expPeptide.scanSummary.get("scanFile").get(i)+"\t"+ionNames.get(i)+"\t"+Integer.toString(ionNames.get(i).size())+"\t"+name+"\t"+count+"\t"+mz);
                       }
                       else{
                           //System.out.println("tag4");
                           scans=expPeptide.scanSummary.get("scanFile").get(i);
                           //System.out.println(scans);
                       }
                   }
                }
                    
                out.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
    }
   
            
    public void setMod(HashMap<Character,Double> varmod, HashMap<Character,Double> fixmod){
        VarMod=varmod;
        FixMod=fixmod;
        lightPep=new PeptideFragmentation(VarMod,FixMod);
    }
    ////transfer string for mascot
    public List<String> transferStringMascot(){
        List<String> pepMod=expPeptide.scanSummary.get("Modified Sequence");
        int n=pepMod.size();
        List<Integer> pepLength=new ArrayList<>();
        for(int i=0;i<n;i++)
            pepLength.add(expPeptide.scanSummary.get("Sequence").get(i).length());
         for(int i=0;i<n;i++){
             if(pepMod.get(i).length()<5){
                    char[] mods=new char[pepLength.get(i)+4];
                    mods[0]='0';
                    mods[1]='.';
                    for(int j=0;j<pepLength.get(i);j++){
                        mods[2+j]='0';
                    }
                    mods[2+(int)pepLength.get(i)]='.';
                    mods[3+(int)pepLength.get(i)]='0';
                    pepMod.set(i,new String(mods));
             }
             
         }
        return pepMod;
    }
    
   public void calculateSMT(){
        //should be adjusted according to the headline of input file
        if(expPeptide.scanSummary.get("Modified Sequence")==null||expPeptide.scanSummary.get("Sequence")==null||expPeptide.scanSummary.get("Scan Number")==null||expPeptide.scanSummary.get("Charge")==null)
            System.out.println("no data column");
        List<String> pepScan=expPeptide.scanSummary.get("Scan Number");
        //System.out.println(pepScan.size());
        int n=pepScan.size();
        double total=0;
        HashMap<Double,Double> temp=new LinkedHashMap<>();
        for(int i=0;i<n;i++){
            temp=expPeptide.expMSMS.get(pepScan.get(i));
            total=0;
            for(double mz:temp.keySet())
                total+=temp.get(mz);
            totalSMT.add(total);
        }
       
    }
    public void calculateAllLight(){
//should be adjusted according to the headline of input file
        if(expPeptide.scanSummary.get("Modified Sequence")==null||expPeptide.scanSummary.get("Sequence")==null||expPeptide.scanSummary.get("scanFile")==null||expPeptide.scanSummary.get("Charge")==null)
            System.out.println("no data column");
        
        pepVarMods=new ArrayList<>();
        pepVarMods=expPeptide.scanSummary.get("Modified Sequence");
        //System.out.println(pepVarMods.get(0));
        //pepVarMods=transferStringMascot();
        int n=pepVarMods.size();
        pepSeq=expPeptide.scanSummary.get("Sequence");
        //should be adjusted according to the headline of input file
        List<String> pepScan=expPeptide.scanSummary.get("scanFile");
        pepCharge=expPeptide.scanSummary.get("Charge");
         //System.out.println(pepScan.get(0));
          //System.out.println(pepCharge.get(0));
        String pepDFile;
        int charge;
        
        //some problems exists in reading charge because the previous column of it has ,, now change the column of charge to be after modified sequence in the csv file
        for(int i=0;i<n;i++){
            if(pepVarMods.get(i).length()>4){
                //System.out.println(pepCharge.get(i));
                //charge=Integer.parseInt(pepCharge.get(i));
                //temporarily set charge of 2
                charge=Integer.parseInt(pepCharge.get(i));
                //to avoid big negative values
                if(charge<0)
                    charge=1;
                pepDFile=pepScan.get(i);
                //System.out.println(pepDFile);
                //get quantable light and heavy ion intensitied
                //in case unknown amino acid exists
                if(pepSeq.get(i).indexOf('X')==-1){
                    //System.out.println(i);
                    if(i==0){
                        /*System.out.println(pepSeq.get(i));
                        System.out.println(pepVarMods.get(i));
                        System.out.println(pepDFile);
                        System.out.println(expPeptide.expMSMS.get(pepDFile));*/
                    }
                    //System.out.println(pepSeq.get(i));
                    //System.out.println(pepVarMods.get(i));
                     //System.out.println(charge);
                      //System.out.println(pepDFile);
                    calculateLight(pepSeq.get(i),pepVarMods.get(i),charge,pepDFile,pepDFile);
                }
                else{
                    IonCounts.add(new ArrayList<Double> ());
                    IonTotalIntensity.add(0.0);
                    ionNames.add(new ArrayList<String>());
                    ionMz.add(new ArrayList<Double>());
                    scans.add(pepDFile);
                }
                    
                //System.out.println(i);
            }
        }
        //System.out.println("tag3");
        //System.out.println(scans.size());
        System.out.println("success");
    }
    //calculate theoretical ms/ms for light and heavy peptide,compare with expmsms,then determine which ion is quantifiable
    public void calculateLight(String peptidesequence, String peptidevarmod,int chargeprecursor,String dtafilename,String pepDFile){
        
        HashMap<Double,Double> expmsms=expPeptide.expMSMS.get(pepDFile);
        if(expmsms==null)
            System.out.println(pepDFile);
        lightPep.calculateMSMS(peptidesequence, peptidevarmod,chargeprecursor);
        //System.out.println(lightPep.peptideMass);
        
        List<Double> light=new ArrayList<>(lightPep.getTheoMSMS().values());
        List<String> lightName=new ArrayList<>(lightPep.getTheoMSMS().keySet());
        //System.out.println(lightName);
        //System.out.println(light);
        //first find available ions, then determine whether quantifiable
        List<Double> lightIonCount=new ArrayList<>();
        //find the paired ions of light and heavy labeled peptides
        //System.out.println(expmsms.size());
        List<Integer> findIndex=new ArrayList<>(findLight(lightIonCount,light,expmsms,dtafilename));
        List<Double> newLight=new ArrayList<>();
        List<String> newNames=new ArrayList<>();
        List<Integer> newCharge=new ArrayList<>();
        for(int i=0;i<findIndex.size();i++){
            newLight.add(light.get(findIndex.get(i)));
            newNames.add(lightName.get(findIndex.get(i)));
            newCharge.add(lightPep.getChargeMS().get(findIndex.get(i)));
        }
        boolean[] isEqual=new boolean[findIndex.size()];
        for(int i=0;i<isEqual.length;i++)
            isEqual[i]=false;
        
        
        List<String> ionName=new ArrayList<>();
        List<Double> ionMass=new ArrayList<>();
        List<Double> quantLightIonCount=new ArrayList<>();
        double lightTotal=0;
        for(int i=0;i<findIndex.size();i++){
            if(!isEqual[i]){
                ionName.add(newNames.get(i));
                ionMass.add(newLight.get(i));
                quantLightIonCount.add(lightIonCount.get(i));
                lightTotal=lightTotal+lightIonCount.get(i);
            }
        }
       
        IonCounts.add(quantLightIonCount);
        //to check the distribution of all ratios between fragment ions of light and heavy labeled peptides
        IonTotalIntensity.add(lightTotal);
        ionNames.add(ionName);
        ionMz.add(ionMass);
        scans.add(dtafilename);
        
        
       
    }
    //find the ion intensity for light and heavy peptide
    public List<Integer> findLight(List<Double> lightIonCount,List<Double> light,HashMap<Double,Double> expmsms,String dtafilename){
        
        int charge;
        int expL=expmsms.size();
        int theoL=light.size();
  
        double temp;
        List<Integer> findIndex=new ArrayList<>();
        List<Double> expMass=new ArrayList<>(expmsms.keySet());
        List<Boolean> isFind=new ArrayList<>();
        for(int j=0;j<expL;j++)
            isFind.add(false);

        for(int i=0;i<theoL;i++){
                charge=lightPep.getChargeMS().get(i);
                if(charge==2&&light.get(i)<doubleIonThres)
                    ;
                else{
                    for(int j=0;j<expL;j++){
                      if(!isFind.get(j)){  
                        temp=expMass.get(j);
                        //temporary to limit only the small ions
                        if(Math.abs(temp-light.get(i))<threshold&&light.get(i)<mzThreshold){
                            lightIonCount.add(expmsms.get(temp));
                            findIndex.add(i);
                            isFind.set(j,true);
                            break;
                        }
                      }  
                    }
                }
        }
        return findIndex;
    }
        //for two neutrons difference,the m,m+1,m+2 of ion with smaller m/z shouldnot overlap with the ion with larger m/z.
    public boolean compareL(double diff,int charge){
        boolean isE=false;
        //actually the setMassDiff should be replaced by 2.0 because normally mass+1, mass+2 has more abundance.
        if(considerIso){
            if(diff>=2.0*1.008665/charge-threshold&&diff<=2.0*1.008665/charge+threshold)
                isE=true;
            if(diff>=1.008665/charge-threshold&&diff<=1.008665/charge+threshold)
                isE=true;
        }
        if(diff<=threshold)
            isE=true;
        return isE;
    }
    public void compareLight(boolean[] isEqual,List<Integer> chargeState,List<Double> light){
        int length=light.size();
        int index;
        double diff;
        for(int i=0;i<length-1;i++){
            for(int j=i+1;j<length;j++){
                if(light.get(i)<light.get(j)){
                    index=i;
                    diff=light.get(j)-light.get(i);
                }
                else{
                    index=j;
                    diff=light.get(i)-light.get(j);
                }
                if(compareL(diff,chargeState.get(index))){
                    isEqual[i]=true;
                    isEqual[j]=true;
                }
            }
        }
    }
}
