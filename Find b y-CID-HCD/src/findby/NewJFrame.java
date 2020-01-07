/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findby;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

/**
 *
 * @author syc
 */
public class NewJFrame extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
    }
    //transfer mod for pd
    public String transferStringPD(String peptide,String pepMod){
        int pepLength=peptide.length();
        HashMap<String,Character> mod=new LinkedHashMap<>();
        mod.put("Oxidation",'1');
        mod.put("Carbamidomethyl",'2');

        mod.put("Methylamine",'3');
        mod.put("Agmat",'4');
        mod.put("Methy",'5');
        //
        //改变，因为修饰形式已改变
        if(pepMod.length()>5){
        if(pepMod.indexOf("\"")==0) 
            pepMod = pepMod.substring(1,pepMod.length());   //去掉第一个 "
        if(pepMod.lastIndexOf("\"")==(pepMod.length()-1)) 
            pepMod = pepMod.substring(0,pepMod.length()-1);  //去掉最后一个 " 
        }
        //System.out.println(pepMod);
        
        char[] mods=new char[pepLength+4];
        mods[0]='0';
        mods[1]='.';
        for(int i=0;i<pepLength;i++)
            mods[2+i]='0';
        mods[2+(int)pepLength]='.';
        mods[3+(int)pepLength]='0';
        int pos=0;
        //System.out.println("tedy");
        if(pepMod.contains(")")){
            //System.out.println("ttt");
            String[] modString=null;
            if(pepMod.indexOf(";")!=-1){
                modString=pepMod.split("; ");
                //System.out.println("tee");
            }
            else
            {
                System.out.println("KK");
                modString=new String[1];
                modString[0]=pepMod;
            }
            String[] singleMod;
            for(int j=0;j<modString.length;j++){
                //first extract the mod in the bracket
                //second extract the position of mod
                singleMod=modString[j].split("\\(");
                //去掉最后一个)
                //contains 不用加\\;而split需要加\\
                if(singleMod[1].contains(")")){
                   System.out.println("hh");
                   singleMod[1]=singleMod[1].substring(0,singleMod[1].length()-1);
                }
                
                System.out.println(singleMod[1]);
                if(singleMod[0].equals("N-Term")){
                    if(mod.containsKey(singleMod[1]))
                        mods[0]=mod.get(singleMod[1]);
 
                }
                else if(singleMod[0].equals("C-Term")){
                    if(mod.containsKey(singleMod[1]))
                        mods[pepLength+3]=mod.get(singleMod[1]); 
                }
                else{
                    //位置放这里,因为末端修饰没有位置
                    pos=Integer.parseInt(singleMod[0].substring(1));
                    if(mod.containsKey(singleMod[1]))
                        mods[pos+1]=mod.get(singleMod[1]);
                }
            }
        }
        return new String(mods);
    }
//transfer the string to number format of the modification
   
    public String transferStringPBuild(String peptide,String pepMod){
        int pepLength=peptide.length();
        HashMap<String,Character> mod=new LinkedHashMap<>();
        /*mod.put("Oxidation[M]#0",'1');
        mod.put("Propargylamine[AnyC-term]#0",'2');
        mod.put("PPA_DMAz#0",'3');
        mod.put("N2H4#0",'4');
        mod.put("N2H4-Guanidinyl#0",'5');
        mod.put("Guanidinyl[AnyN-term]#0",'6');
        mod.put("Guanidinyl[K]#0",'7');*/
        mod.put("Oxidation[M]#0",'1');
        mod.put("Methylamine[D]#0",'2');
        mod.put("Methylamine[E]#0",'3');
        mod.put("Methylamine[C-term]#0",'4');
        mod.put("Agmatine[C-term]#0",'5');
        mod.put("Agmatine[E]#0",'6');
        mod.put("Agmatine[D]#0",'7');
         
        //
        //
        if(pepMod.indexOf(';')!=-1){
        if(pepMod.indexOf("\"")==0) 
            pepMod = pepMod.substring(1,pepMod.length());   //去掉第一个 "
        if(pepMod.lastIndexOf("\"")==(pepMod.length()-1)) 
            pepMod = pepMod.substring(0,pepMod.length()-1);  //去掉最后一个 " 
        }
        //System.out.println(pepMod);
        
        char[] mods=new char[pepLength+4];
        mods[0]='0';
        mods[1]='.';
        for(int i=0;i<pepLength;i++)
            mods[2+i]='0';
        mods[2+(int)pepLength]='.';
        mods[3+(int)pepLength]='0';
        
        if(pepMod.indexOf(';')!=-1){
            String[] modString=pepMod.split(";");
            String[] singleMod;
            for(int j=0;j<modString.length;j++){
                singleMod=modString[j].split(",");
                if(singleMod[0].equals("0")){
                    if(mod.containsKey(singleMod[1]))
                        mods[0]=mod.get(singleMod[1]);
 
                }
                else if(Integer.parseInt(singleMod[0])==pepLength+1){
                    if(mod.containsKey(singleMod[1]))
                        mods[pepLength+3]=mod.get(singleMod[1]); 
                }
                else{
                    if(mod.containsKey(singleMod[1]))
                        mods[Integer.parseInt(singleMod[0])+1]=mod.get(singleMod[1]);
                }
            }
        }
        return new String(mods);
    }
  
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addComponent(jButton1)
                .addContainerGap(169, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(129, 129, 129)
                .addComponent(jButton1)
                .addContainerGap(148, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    HashMap<String,String> findSpectrum(List<String> scan,String mgfName){
        HashMap<String,String> ScanSpectrum=new LinkedHashMap<>();
       
        //should be adjusted according to the file format
        List<String> allDtaFile=scan;
        //System.out.println(allDtaFile.get(0));
        try{
            BufferedReader MGFFile =new BufferedReader(new FileReader(mgfName));
            String dataRow;
            // remove the first line
            MGFFile.readLine();
            while ((dataRow=MGFFile.readLine())!=null){
                //System.out.println(dataRow);
                if(dataRow.indexOf("TITLE")!=-1)
                {
                    String[] temp0=dataRow.split(";");
                    //extract the scan number
                    String[] temp=temp0[2].split("\\\"");
                    //spectrum number
                    String[] temp1=temp0[1].split("\\\"");
                    //System.out.println(temp1[1]);
                    //System.out.println(temp[1]);
                    //System.out.println(temp1[1]); spectum and 
                    if(allDtaFile.indexOf(temp[1])!=-1){
                        ScanSpectrum.put(temp1[1],temp[1]);
                        //System.out.println(temp1[1]);
                        //System.out.println(temp[1]);
                }
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
        System.out.println("succs");
        return ScanSpectrum;
    }
    //input mgf
    private List<Spectrum> inputMGF(File inputfile){
         int space=1;
         String[] mzAndIntensity=new String[2];
    
        int peakstart=0;
        List<Double> msmsMz=new ArrayList<>();
        List<Double> msmsIntens=new ArrayList<>();
        double mz;
        List<Spectrum> spectra=new ArrayList<>();
        HashMap<Double,Double> mzInten=new LinkedHashMap<>();
        Spectrum oneSpec;
        try{
            BufferedReader inFile=new BufferedReader(new FileReader(inputfile));
            String dataRow;
            String dataRow1="",dataRow2="",dataRow3="";
            Pattern p=Pattern.compile("END");
            Matcher m;
            Pattern p1=Pattern.compile("BEGIN");
            
            Matcher m1;
            int charge=0;
            int scan=0;
            double pepmass=0;
            while ((dataRow=inFile.readLine())!=null){
                m=p.matcher(dataRow);
                if(m.find()){
                    peakstart=0;
                    if(scan==26035){
                        for(Double mz1:mzInten.keySet()){
                            System.out.println(mz1+","+mzInten.get(mz1));
                        }
                    }
                        
                    oneSpec=new Spectrum(scan,pepmass,charge,mzInten);
                    oneSpec.setTitle(dataRow1,dataRow3,dataRow2);
                    spectra.add(oneSpec);
                    
                 }
                //read the ms2 spectrum
                if(peakstart==1){
                      if(space==1)
                        mzAndIntensity=dataRow.split(" ");
                    else
                        mzAndIntensity=dataRow.split("\\t");
                    msmsMz.add(Double.parseDouble(mzAndIntensity[0]));
                    msmsIntens.add(Double.parseDouble(mzAndIntensity[1]));
                    mzInten.put(Double.parseDouble(mzAndIntensity[0]),Double.parseDouble(mzAndIntensity[1]));
                }
                m1=p1.matcher(dataRow);
                //judge the begin of dta
                if(m1.find()){
                    String[] dArray;
                    //title
                    inFile.readLine();
                    //pepmass
                    dataRow2=inFile.readLine();
                    dArray=dataRow2.split("=");
                    dArray=dArray[1].split(" ");
                    pepmass=Double.parseDouble(dArray[0]); 
                    //charge
                    dataRow3=inFile.readLine();
                    dArray=dataRow3.split("=");
                    charge=Integer.parseInt(dArray[1].substring(0,1));
                    //RT
                    inFile.readLine();
                    //scan
                    dataRow1=inFile.readLine();
                    dArray=dataRow1.split("=");
                    scan=Integer.parseInt(dArray[1]);
                            
                    //////////////////format two end
                    peakstart=1;
                    msmsMz=new ArrayList<>();
                    msmsIntens=new ArrayList<>();
                    mzInten=new LinkedHashMap<>();
                }
            }
            inFile.close();
              
        }
       catch(FileNotFoundException e){
           System.out.println("error1");
       }
       catch(IOException e){
           System.out.println("error2");
       }
        return spectra;
      }
     public boolean compareScan(int scan1, int scan2){
       if(Math.abs(scan1-scan2)<3)
           return true;
       else
           return false;
   }
   public boolean compareMz(double mz1, double mz2,int charge1,int charge2){
       if(Math.abs(mz1-mz2)<0.05&&charge1==charge2)
           return true;
       else
           return false;
   }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
          // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("please select file name");
        int result=chooser.showOpenDialog(this);
        String fileNameAll,fileName,fileNameHCD,fileNameCID,fileNameETD;
        if(result==JFileChooser.APPROVE_OPTION)
            fileNameAll = chooser.getSelectedFile().getAbsolutePath();
        else
            return;
        try{
            //need to use another scan summary file -hcd, to find the scan ans spectrum, use the spectrum to extract the mgf
            //if in the new mgf, there is no such spectrum, then keep 0
            BufferedReader inFile=new BufferedReader(new FileReader(new File(fileNameAll)));
            fileName=inFile.readLine();
            fileNameHCD=inFile.readLine();
            fileNameCID=inFile.readLine();
            //fileNameETD=inFile.readLine();
            inFile.close();
            System.out.println(fileNameHCD);
            List<Spectrum> hcdSpectra=inputMGF(new File(fileNameHCD));
            //List<Spectrum> etdSpectra=inputMGF(new File(fileNameeETD));
            List<Spectrum> cidSpectra=inputMGF(new File(fileNameCID));
            //fore each hcd spectrum, if scan number is adjacent and mz is similar, then put the spectrum to hcd, etd should adjust a little
            ///
            //HashMap<Integer,Integer> HCD_ETD=new LinkedHashMap<>();
            HashMap<Integer,Integer> HCD_CID=new LinkedHashMap<>();
            for(int i=0;i<hcdSpectra.size();i++){
                //HCD_ETD.put(hcdSpectra.get(i).scan,0);
                HCD_CID.put(hcdSpectra.get(i).scan,0);
            }
            for(int i=0;i<hcdSpectra.size();i++){
                Spectrum hcd0=hcdSpectra.get(i);
                /*for(int j=0;j<etdSpectra.size();j++){
                    if(compareScan(hcd0.scan,etdSpectra.get(j).scan)&&compareMz(hcd0.mz,etdSpectra.get(j).mz,hcd0.charge,etdSpectra.get(j).charge)){
                        hcd0.scanETD=etdSpectra.get(j).scan;
                        hcd0.addETD(etdSpectra.get(j).mzIntens);
                        HCD_ETD.put(hcd0.scan,etdSpectra.get(j).scan);
                        //System.out.println(hcd0.scan);
                        //System.out.println(etdSpectra.get(j).scan);

                    }
                }*/
                for(int j=0;j<cidSpectra.size();j++){
                    if(compareScan(hcd0.scan,cidSpectra.get(j).scan)&&compareMz(hcd0.mz,cidSpectra.get(j).mz,hcd0.charge,cidSpectra.get(j).charge)){
                        hcd0.addCID(cidSpectra.get(j).mzIntens);
                        hcd0.scanCID=cidSpectra.get(j).scan;
                        HCD_CID.put(hcd0.scan,cidSpectra.get(j).scan);
                        //System.out.println(hcd0.scan);
                        //System.out.println(cidSpectra.get(j).scan);
                    }
                }
            }
            //after find by, all these should be compared 
            BufferedReader pBuildFile =new BufferedReader(new FileReader(new File(fileName)));
            FileWriter outFileL = new FileWriter(new File(fileName+".HCDCID0"));
            PrintWriter outL = new PrintWriter(outFileL);

            String dataRow;
            String outRow;
            String numberMod;
            pBuildFile.readLine();
            //pBuildFile.readLine();
            List<String> light=new ArrayList<>();
  
         
           
           
           
           
           /////////////////
            //outRow:spectrum1,spectrum2,pep,mod,mass
            outL.println("dta name"+"\t"+"peptide"+"\t"+"mod"+"\t"+"charge");
          int nm=0;
          List<String> scans=new ArrayList<>();
            while ((dataRow=pBuildFile.readLine())!=null)
            {
                nm=nm+1;
                String[] temp=dataRow.split("\\t");
                String dta=temp[26];
                System.out.println(dta);
                scans.add(dta);
                String charge=temp[11];
                String peptide=temp[4].toUpperCase();
                String mod=temp[5];
                //System.out.println(mod);
               
                //transfer mod to number format, temp1[2]:peptide sequence, temp1[4]: modification string format
                //modification should be added later
                numberMod=transferStringPD(peptide,mod);
                outRow=dta+"\t"+peptide+"\t"+numberMod+"\t"+charge;
                //System.out.println(numberMod);
   
                    //pep seq+number mod+ms1 scan number
                light.add(outRow);

            }
     
         
            
            
            pBuildFile.close();
          
            
            ////////////////////////below is for label free!!!!!! calculate the ions
            //input for fixedModification
            List<Character> fixedModificationAminoAcid=new ArrayList<>();
            fixedModificationAminoAcid.add('C');
            List<Double> fixedModificationMassChange=new ArrayList<>();
            fixedModificationMassChange.add(57.021464);
            //fixedModificationMassChange.add(71.037114);
            //////////////
            //fixedModificationAminoAcid.add('K');
            //fixedModificationMassChange.add(28.0313);//dimethyl
            /////////////不要fixed mod,因为varmod中包含这个修饰
            HashMap<Character,Double> fixedMod=new HashMap<>();
            /*for(int i=0;i<fixedModificationAminoAcid.size();i++)
            {
                fixedMod.put(fixedModificationAminoAcid.get(i),fixedModificationMassChange.get(i));
            }*/
            
            HashMap<Character,Double> varModL=new HashMap<>();
         

           //need to adjust the modification based on the data acquisition
           //需要加入修饰!!
            varModL.put('1',15.994915);//oxidation
           varModL.put('2',57.021464);//carbaami
           varModL.put('3',13.0316);//methylaminie
           varModL.put('5',13.0316);//methy
           varModL.put('4',112.111);//agmat

           //
           //
           double thres=0.02;
           //double thres=0.002;

       
                  
        
      
            System.out.println("show data");
           /////////////////////////////////////
          UnlabelCalc rel=new UnlabelCalc(new File(fileName),light,2,hcdSpectra);
          rel.setCID(HCD_CID);
           rel.setThreshold(thres);
           rel.setMod(varModL, fixedMod);
           rel.calculateAllLight();
           rel.writeData(new File(fileName+".HCD_CID.txt"));
           List<String> lightResult=rel.allResult;
           System.out.println(lightResult.size());
 
        }
        catch(FileNotFoundException e){
            System.out.println("File not found");
        }
        catch(IOException e){
            e.printStackTrace(System.out);       
        }
                                        
   
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
class Spectrum{
       int scanCID=0;
       int scanETD=0;
       String title;
       String charg;
       String pepmass;
       int scan;
       double mz;
       int charge;
       HashMap<Double,Double> mzIntens;
        double nitrogenMolWeight=14.003074;
        double hydrogenMolWeight=1.0078250;
       Spectrum(int scan0, double mz0,int charge0,HashMap<Double,Double> mzInten){
           scan=scan0;
           mz=mz0;
           charge=charge0;
           mzIntens=mzInten;
       }
       public void setTitle(String tit,String cha,String pepm){
           title=tit;
           charg=cha;
           pepmass=pepm;
       }
       public void addETD(HashMap<Double,Double> mzInten){
           for(double mz:mzInten.keySet())
               mzIntens.put(mz,mzInten.get(mz)+nitrogenMolWeight+hydrogenMolWeight);
          
       }
       public void addCID(HashMap<Double,Double> mzInten){
           for(double mz:mzInten.keySet())
               mzIntens.put(mz,mzInten.get(mz));
       }
   }