package stgn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class Modulation extends JFrame{

   JTextField textfile = new JTextField(50);
   JTextField imagefile = new JTextField(50);
   JTextField newimagefile = new JTextField(50);   //암호화한 후 생성될 이미지
   JTextField decimagefile = new JTextField(50);   //복호화할 이미지 파일
   JTextField newtextfile = new JTextField(50);   //추출하여 새로 생성할 텍스트파일

   JTextArea ta = new JTextArea(7,20);
   JScrollPane sp = new JScrollPane(ta);
   JTextArea ta2 = new JTextArea(7,20);
   JScrollPane sp2 = new JScrollPane(ta2);
   int textCount= 0;


   Modulation(){
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("스테가노그래피 - 흰부분에 텍스트 숨기기");

      Container c =  this.getContentPane();

      c.setLayout(null);
      JLabel la = new JLabel("< 암호화 >");
      la.setSize(100, 20);
      la.setLocation(140, 15);
      la.setFont(new Font("돋움", Font.BOLD, 15));

      JLabel la2 = new JLabel("숨길 텍스트 파일 :");
      la2.setSize(200, 20);
      la2.setLocation(30, 50);

      textfile.setSize(110, 20);
      textfile.setLocation(180, 50);

      JLabel la3 = new JLabel("이미지 선택(.bmp) :");
      la3.setSize(200, 20);
      la3.setLocation(30, 75);

      imagefile.setSize(110, 20);
      imagefile.setLocation(180, 75);

      JLabel la4 = new JLabel("생성할 이미지 파일(.bmp) :");
      la4.setSize(200, 20);
      la4.setLocation(30, 100);

      newimagefile.setSize(110, 20);
      newimagefile.setLocation(180, 100);

      JButton bt = new JButton("암호화");
      bt.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            String binary = "";

            String tpath = textfile.getText();
            String path = imagefile.getText();
            String savepath = newimagefile.getText();
            File tfile = new File(tpath);

            byte[] dec = new byte[(int) tfile.length()];
            String[] bin = new String[(int) tfile.length()];
            try {

               FileReader textreader = new FileReader(tfile);
               
               int textchar = 0; // 문자 하나하나
               int temp = 0;
               while ((textchar = textreader.read()) != -1) {
                  textCount++;
                  System.out.print((char) textchar);
                  dec[textCount - 1] = (byte) textchar;
                  temp = Integer.parseInt(Integer.toBinaryString(dec[textCount - 1]));
                  bin[textCount - 1] = String.format("%07d", temp);
               }

               String[] binary_array = new String[textCount * 7]; // 7자리 2진수로 표현
               for (int i = 0; i < bin.length; i++) {
                  binary += bin[i];
               }
               for (int i = 0; i < binary.length(); i++) { // 스트링을 한글자씩 끊어 배열에 저장
                  binary_array[i] = Character.toString(binary.charAt(i));
               }

               System.out.println();
               System.out.print("아스키코드 : ");
               ta.append("▶아스키코드 : ");

               for (int i = 0; i < dec.length; i++) {
                  System.out.print(dec[i] + " ");
                  ta.append(dec[i] + " ");
               }

               System.out.println();
               System.out.print("2진수로 표현 : ");
               ta.append("\n");
               ta.append("▶2진수로 표현 : ");

               for (int i = 0; i < dec.length; i++) {
                  System.out.print(bin[i] + " ");
                  ta.append(bin[i] + " ");
               }

               int zero = 0;
               int one = 0;
               for (int i = 0; i < binary_array.length; i++) {
                  if (binary_array[i].equals("0"))
                     zero++;
                  else
                     one++;
               }

               while ((textchar = textreader.read()) != -1) {
                  System.out.print(dec + "");
               }
               System.out.println();
               textreader.close();

               BufferedImage img = ImageIO.read(new File(path));
               int biWidth = img.getWidth();
               int biHeight = img.getHeight();

               int[] endPos = new int[2]; // maxWhite의 끝점
               int maxhWhite = 0, amountWhite = 0;
               String binTextCount;
               binTextCount = Integer.toBinaryString(textCount);
               int bintcCount = binTextCount.length();

               int rgb;
               int red = 0, green = 0, blue = 0;
               int FF = 0, FE = 0;

               inputOuter: for (int h = 0; h < biHeight; h++) {
                  for (int w = 0; w < biWidth; w++) {
                     rgb = img.getRGB(w, h);
                     red = (rgb >> 16) & 0x000000FF;
                     green = (rgb >> 8) & 0x000000FF;
                     blue = (rgb) & 0x000000FF;

                     if ((red == 255 || red == 254 || red == 253) && (green == 255 || green == 254 || green  == 253) && (blue == 255 || blue == 254 || blue  == 253)) {
                        amountWhite++;                        
                        if (red == 255)
                           FF++;
                        else if (red == 254)
                           FE++;
                        
                        if (green == 255)
                           FF++;
                        else if (green  == 254)
                           FE++;      
                        
                        if (blue == 255)
                           FF++;
                        else if (blue  == 254)
                           FE++;

                        if (h == biHeight - 1 && w == biWidth - 1) { // 사진의 마지막이 흰 부분인 경우
                           if (maxhWhite <= amountWhite) {
                              maxhWhite = amountWhite;
                              endPos[0] = h;
                              endPos[1] = w;
                           }
                        }
                     } else if (amountWhite != 0) { // 흰부분의 끝 부분
                        if (maxhWhite < amountWhite && one + zero <= amountWhite * 3) {
                           if (w == 0) {
                              maxhWhite = amountWhite;
                              endPos[0] = h - 1;
                              endPos[1] = biWidth;
                           } else {
                              maxhWhite = amountWhite;
                              endPos[0] = h;
                              endPos[1] = w - 1;
                           }
                        }
                        if (textCount + bintcCount / 3 + 3 <= maxhWhite)
                           break inputOuter;

                        amountWhite = 0;
                        FF = 0;
                        FE = 0;
                     }
                  }
               }

               Color white;
               int tempCount;
               int hCount = 0;
               int wCount = 0;
               int index = 0;
               int oneInfo = 0, zeroInfo = 0;
               
               binary = "";
               binary += binTextCount;
               String[] binaryInfo_array = new String[binary.length()];               

               for (int i = 0; i < binary.length(); i++) { // 스트링을 한글자씩 끊어 배열에 저장
                  binaryInfo_array[i] = Character.toString(binary.charAt(i));
               }

               for (int i = 0; i < binaryInfo_array.length; i++) {
                  if (binaryInfo_array[i].equals("0"))
                     zeroInfo++;
                  else
                     oneInfo++;
               }

               tempCount = oneInfo + zeroInfo;
               if (one >= zero && FF >= FE) {
                  white = new Color(255, 255, 255);
                  rgb = white.getRGB();
                  img.setRGB(endPos[1], endPos[0], rgb);
                  wCount++;
               }
               else if (one < zero && FF >= FE) {
                  white = new Color(254, 255, 255);
                  rgb = white.getRGB();
                  img.setRGB(endPos[1], endPos[0], rgb);
                  wCount++;
               }
               else if (one >= zero && FF < FE) {
                  white = new Color(255, 254, 254);
                  rgb = white.getRGB();
                  img.setRGB(endPos[1], endPos[0], rgb);
                  wCount++;
               }
               else {
                  white = new Color(254, 254, 254);
                  rgb = white.getRGB();
                  img.setRGB(endPos[1], endPos[0], rgb);
                  wCount++;
               }
               boolean first = true;
               infoOuter: for (int h = endPos[0]; h >= 0; h--) {
            	 
                  for (int w = img.getWidth()-1; w >= 0; w--) {
                	  if(first == true)
                		  w = endPos[1] - wCount;
                	
                     int j;
                     for (j = 0; j < 3; j++) {
                        if (tempCount <= 0)
                           break infoOuter;
                        if (index == binaryInfo_array.length) {
                           switch (j) {
                           case 0:
                              red = 253;
                              green = 255;
                              blue = 255;
                              white = new Color(red, green, blue);
                              rgb = white.getRGB();
                              img.setRGB(w, h, rgb);
                              wCount++;
                              break infoOuter;
                           case 1:
                              green = 253;
                              blue = 255;
                              break infoOuter;
                           case 2:
                              blue = 253;
                              break infoOuter;
                           }
                        }

                        if (binaryInfo_array[index].equals("1")) {
                           switch (j) {
                           case 0:
                              red = 255;
                              tempCount--;
                              index++;
                              break;
                           case 1:
                              green = 255;
                              tempCount--;
                              index++;
                              break;
                           case 2:
                              blue = 255;
                              tempCount--;
                              index++;
                              break;
                           }

                        } else {
                           switch (j) {
                           case 0:
                              red = 254;
                              tempCount--;
                              index++;
                              break;
                           case 1:
                              green = 254;
                              tempCount--;
                              index++;
                              break;
                           case 2:
                              blue = 254;
                              tempCount--;
                              index++;
                              break;
                           }
                        }

                     }

                     white = new Color(red, green, blue);
                     rgb = white.getRGB();
                     img.setRGB(w, h, rgb);
                     wCount++;
                     if (index == binaryInfo_array.length && j == 2) {
                        white = new Color(253, 255, 255);
                        rgb = white.getRGB();
                        img.setRGB(w, h, rgb);
                        wCount++;
                        break infoOuter;
                     }
                     first = false;
                  }
               }

               if (maxhWhite * 3 < one + zero) {
                  System.out.println("사진에 흰색 부분이 충분하지 않습니다.");
                  System.exit(1);
               }

               tempCount = one + zero;
               index = 0;
               hCount = wCount / img.getWidth();
               
               outputOuter: if (one >= zero && FF >= FE) {
                  for (int h = endPos[0] - hCount; h >= 0; h--) {
                     for (int w = endPos[1] - wCount; w >= 0; w--) {
                        for (int j = 0; j < 3; j++) {
                           if (tempCount <= 0)
                              break outputOuter;
                           if (index == binary_array.length) {
                              switch (j) {
                              case 0:
                                 red = 255;
                                 green = 255;
                                 blue = 255;
                                 break outputOuter;
                              case 1:
                                 green = 255;
                                 blue = 255;
                                 break outputOuter;
                              case 2:
                                 blue = 255;
                                 break outputOuter;
                              }
                           }

                           if (binary_array[index].equals("1")) {
                              switch (j) {
                              case 0:
                                 red = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           } else {
                              switch (j) {
                              case 0:
                                 red = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           }
                        }
                        white = new Color(red, green, blue);
                        rgb = white.getRGB();
                        img.setRGB(w, h, rgb);
                     }
                  }
               } else if (one < zero && FF >= FE) {
                  for (int h = endPos[0] - hCount; h >= 0; h--) {
                     for (int w = endPos[1] - wCount; w >= 0; w--) {
                        for (int j = 0; j < 3; j++) {
                           if (tempCount <= 0)
                              break outputOuter;
                           if (index == binary_array.length) {
                              switch (j) {
                              case 0:
                                 red = 255;
                                 green = 255;
                                 blue = 255;
                                 break outputOuter;
                              case 1:
                                 green = 255;
                                 blue = 255;
                                 break outputOuter;
                              case 2:
                                 blue = 255;
                                 break outputOuter;
                              }
                           }

                           if (binary_array[index].equals("0")) {
                              switch (j) {
                              case 0:
                                 red = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           } else {
                              switch (j) {
                              case 0:
                                 red = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           }
                        }
                        white = new Color(red, green, blue);
                        rgb = white.getRGB();
                        img.setRGB(w, h, rgb);
                     }
                  }
               } else if (one >= zero && FF < FE) {
                  for (int h = endPos[0] - hCount; h >= 0; h--) {
                     for (int w = endPos[1] - wCount; w >= 0; w--) {
                        for (int j = 0; j < 3; j++) {
                           if (tempCount <= 0)
                              break outputOuter;
                           if (index == binary_array.length) {
                              switch (j) {
                              case 0:
                                 red = 254;
                                 green = 254;
                                 blue = 254;
                                 break outputOuter;
                              case 1:
                                 green = 254;
                                 blue = 254;
                                 break outputOuter;
                              case 2:
                                 blue = 254;
                                 break outputOuter;
                              }
                           }

                           if (binary_array[index].equals("1")) {
                              switch (j) {
                              case 0:
                                 red = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           } else {
                              switch (j) {
                              case 0:
                                 red = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           }
                        }
                        white = new Color(red, green, blue);
                        rgb = white.getRGB();
                        img.setRGB(w, h, rgb);
                     }
                  }
               } else { // one < zero && FF < FE
                  for (int h = endPos[0] - hCount; h >= 0; h--) {
                     for (int w = endPos[1] - wCount; w >= 0; w--) {
                        for (int j = 0; j < 3; j++) {
                           if (tempCount <= 0)
                              break outputOuter;
                           if (index == binary_array.length) {
                              switch (j) {
                              case 0:
                                 red = 254;
                                 green = 254;
                                 blue = 254;
                                 break outputOuter;
                              case 1:
                                 green = 254;
                                 blue = 254;
                                 break outputOuter;
                              case 2:
                                 blue = 254;
                                 break outputOuter;
                              }
                           }

                           if (binary_array[index].equals("0")) {
                              switch (j) {
                              case 0:
                                 red = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 254;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           } else {
                              switch (j) {
                              case 0:
                                 red = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 1:
                                 green = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              case 2:
                                 blue = 255;
                                 tempCount--;
                                 index++;
                                 break;
                              }
                           }
                        }
                        white = new Color(red, green, blue);
                        rgb = white.getRGB();
                        img.setRGB(w, h, rgb);
                     }
                  }
               }


               File outputfile = new File(savepath);
               ImageIO.write(img, "bmp", outputfile);   

               System.out.println("FF 갯수: " + FF);
               System.out.println("FE 갯수: " + FE);
               System.out.println("2진수 길이: " + bin.length);
               System.out.println("문자길이: " + textCount);
               System.out.println("1갯수: " + one);
               System.out.println("0갯수: " + zero);
               System.out.println("max White pixel: " + maxhWhite);
               System.out.println("White pixel End point: (" + endPos[1] + ", " + endPos[0] + ")");


               ta.setLineWrap(true);
               ta.append("\n");
               ta.append("▶FF 갯수: " + FF);
               ta.append("\n");
               ta.append("▶FE 갯수: " + FE);
               ta.append("\n");
               ta.append("▶2진수 길이: " + bin.length);
               ta.append("\n");
               ta.append("▶문자길이: " + textCount);
               ta.append("\n");
               ta.append("▶1갯수: " + one);
               ta.append("\n");
               ta.append("▶0갯수: " + zero);
               ta.append("\n");
               ta.append("▶max White pixel: " + maxhWhite);
               ta.append("\n");
               ta.append("▶White pixel End point: (" + endPos[1] + ", " + endPos[0] + ")");
               ta.append("\n");

            } catch (IOException e) {
               e.printStackTrace();
            }


         }

      });
      bt.setSize(70,70);
      bt.setLocation(300,50);

      ta.setSize(350,350);
      ta.setLocation(20,150);
      sp.setSize(350,350);
      sp.setLocation(20,150);

      JLabel la5 = new JLabel("< 복호화 >");
      la5.setSize(100,20);
      la5.setLocation(520,15);
      la5.setFont(new Font("돋움", Font.BOLD, 15));
      la5.setBackground(Color.YELLOW);

      JLabel la6 = new JLabel("복호화 할 이미지 선택(.bmp) :");
      la6.setSize(200,20);
      la6.setLocation(400,60);

      decimagefile.setSize(110,20);      //복호화할 이미지
      decimagefile.setLocation(570,60);

      JLabel la7 = new JLabel("추출된 텍스트 파일 생성(.txt) :");
      la7.setSize(200,20);
      la7.setLocation(400,90);

      newtextfile.setSize(110,20);
      newtextfile.setLocation(570,90);

      JButton bt2 = new JButton("복호화");
      bt2.addActionListener(new ActionListener() {      //복호화 코드
         @Override
         public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            BufferedImage deimg;
            try {
               deimg = ImageIO.read(new File(decimagefile.getText()));
               int debiWidth = deimg.getWidth();
               int debiHeight = deimg.getHeight();

               int[] deendPos = new int[2]; // maxWhite의 끝점
               int demaxhWhite = 0, deamountWhite = 0;

               int frgb;
               int fred = 0, fgreen = 0, fblue = 0;
               int tcount=textCount;   //이게 임의로 지정한 텍스트 길이임!! 나중에 이걸바까야됨바까~~~~~~~~~~!!~!

               inputOuter :
                  for (int h = 0; h < debiHeight; h++) {
                     for (int w = 0; w < debiWidth; w++) {
                        frgb = deimg.getRGB(w, h);
                        fred = (frgb >> 16) & 0x000000FF;
                        fgreen = (frgb >> 8) & 0x000000FF;
                        fblue = (frgb) & 0x000000FF;

                        if ((fred == 255 || fred == 254) && (fgreen == 255 || fgreen == 254) && (fblue == 255 || fblue == 254)) // 흰 부분일 경우
                        {
                           deamountWhite++;

                           if (h == debiHeight - 1 && w == debiWidth - 1) { // 흰 부분에서 마지막 부분이 된 경우
                              if (demaxhWhite <= deamountWhite) {
                                 demaxhWhite = deamountWhite;
                                 deendPos[0] = h;
                                 deendPos[1] = w;
                              }
                           }

                        } else if (deamountWhite != 0) // 흰부분의 끝 부분
                        {

                           if (demaxhWhite < deamountWhite && tcount <= deamountWhite * 3) {      //가장 큰 흰부분의 끝부부을 찾음
                              if (w == 0) {
                                 demaxhWhite = deamountWhite;
                                 deendPos[0] = h - 1;
                                 deendPos[1] = debiWidth;
                              } else {
                                 demaxhWhite = deamountWhite;
                                 deendPos[0] = h;
                                 deendPos[1] = w - 1;
                              }
                           }
                           if (tcount <= demaxhWhite)
                              break inputOuter;

                           deamountWhite = 0;


                        }
                     }
                  }

               int dergb;
               int dered = 0, degreen = 0, deblue = 0;
               String debin = "" ;//= new String[0];
               int decount=0;


               outputOuter:
                  for (int h = deendPos[0]; h >= 0; h--) {

                     for (int w = deendPos[1]; w >= 0; w--) {
                        dergb = deimg.getRGB(w, h);
                        dered = (dergb >> 16) & 0x000000FF;
                        degreen = (dergb >> 8) & 0x000000FF;
                        deblue = (dergb) & 0x000000FF;

                        for (int j = 0; j <=2; j++) {
                           if(decount>=tcount*7+6)
                              break;
                           switch (j) {
                           case 0:   //red
                              if(decount>=tcount*7+6)
                                 break;
                              if(dered == 255) {
                                 debin+="1";
                                 decount++;
                              }
                              else{
                                 debin+="0";
                                 decount++;
                              }

                              break ;

                           case 1:   //green
                              if(decount>=tcount*7+6)
                                 break;
                              if(degreen == 255) {

                                 debin+="1";
                                 decount++;
                              }
                              else {

                                 debin+="0";
                                 decount++;
                              }
                              break ;

                           case 2:   //blue
                              if(decount>=tcount*7+6)
                                 break;
                              if(deblue == 255) {                  
                                 debin+="1";
                                 decount++;
                              }
                              else {
                                 debin+="0";
                                 decount++;
                              }

                              break ;
                           }

                        }

                     }
                  }

               System.out.println();
               System.out.println("max White pixel: " + demaxhWhite);
               System.out.println("White pixel End point: (" + deendPos[1] + ", " + deendPos[0] + ")");


               String ascii = String.valueOf(debin);
               String[] debinary = new String[tcount];   //2진수 표현
               int[] dedec = new int[debinary.length];   //아스키코드 10진수
               String text = "";

               for(int k=0;k<tcount;k++) {
                  System.out.println(k+" "+tcount);
                  if(k>=tcount-1)
                     break;

                  debinary[k]=(ascii.substring(k*7,k*7+7));

                  dedec[k] =Integer.parseInt(debinary[k],2);   //2진수를 10진수로 변환
               }

               ta2.append("▶문자 수 : "+tcount+"\n");

               System.out.println("2진수로 표현 : ");
               ta2.append("▶2진수로 표현 : ");
               for(int m = 0 ; m<debinary.length;m++) {
                  System.out.print(debinary[m]+" ");
                  ta2.append(debinary[m]+" ");
               }

               System.out.println();
               System.out.println("10진수로 표현 : ");
               ta2.append("\n" );
               ta2.append("▶10진수로 표현 : " );
               for(int m = 0 ; m<debinary.length;m++) {
                  System.out.print(dedec[m]+" ");
                  ta2.append(dedec[m]+" ");
               }

               System.out.println();
               ta2.append("\n");
               for(int m = 0 ; m<debinary.length;m++) {
                  text+=String.valueOf((char)dedec[m]);
               }

               System.out.println(text);

               String tfilename = newtextfile.getText();
               File ntfile = new File(tfilename);
               FileWriter fw = new FileWriter(ntfile, true);
               BufferedWriter out = new BufferedWriter(new FileWriter(tfilename));            
               out.newLine();

               ta2.append("▶추출된 텍스트 : \n" );
               ta2.append(text);
               ta2.append("\n\n***** '"+tfilename+"' 텍스트 파일 생성 완료!*****" );

               fw.write(text);
               fw.flush();
               fw.close();


            } catch (IOException e) {e.printStackTrace();}




         }});
      bt2.setSize(70,70);
      bt2.setLocation(690,50);

      ta2.setSize(350,350);
      ta2.setLocation(410,150);
      ta2.setLineWrap(true);
      sp2.setSize(350,350);
      sp2.setLocation(410,150);

      c.add(la);
      c.add(textfile);
      c.add(sp);
      c.add(la2);
      c.add(la3);
      c.add(la4);
      c.add(newimagefile);
      c.add(imagefile);
      c.add(bt);
      c.add(la5);
      c.add(la6);
      c.add(la7);
      c.add(decimagefile);
      c.add(newtextfile);
      c.add(bt2);
      c.add(sp2);


      setSize(800,600);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
   }

   public static void main(String[] args) {

      Modulation frame = new Modulation();


   }
}