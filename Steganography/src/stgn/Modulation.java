package stgn;

import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;

public class Modulation {
   public static void main(String[] args) {
      
      Scanner reader = new Scanner(System.in);
      System.out.println("숨길 텍스트 파일 입력: ");
      String tpath = reader.nextLine();

      System.out.println("Enter The path to the bmp file: ");
      String path = reader.nextLine();
      reader.close();
      
      File tfile = new File(tpath);
      byte[] by = new byte[(int)tfile.length()];
      
      try {
         
         FileReader textreader = new FileReader(tfile);
         int textcount = 0; // 텍스트 문자 수
         int textchar = 0; // 문자 하나하나
         
         while ((textchar = textreader.read()) != -1) {
            textcount++;
            System.out.print((char) textchar); 
            by[textcount-1]=(byte)textchar;
         }
         System.out.println();
         System.out.println("아스키코드 : ");
         
         for(int i=0; i<by.length; i++) {
            System.out.print(by[i]+" ");
         }
         
         System.out.println();
         System.out.println("2진수로 표현 : ");
         
         for(int i=0; i<by.length; i++) {
            System.out.print(Integer.toBinaryString(by[i])+" ");
         }

         while ((textchar = textreader.read()) != -1) {
             
             System.out.print(by+"");
          }
         System.out.println();
         textreader.close();

         BufferedImage img = ImageIO.read(new File(path));
         int biWidth = img.getWidth();
         int biHeight = img.getHeight();
         
         int[] endPos = new int[2]; // maxWhite의 끝점
         int maxhWhite = 0;
         int amountWhite = 0;

         int rgb;
         int red;
         int green;
         int blue;

         for (int h = 0; h < biHeight; h++) {
            for (int w = 0; w < biWidth; w++) {
               rgb = img.getRGB(w, h);
               red = (rgb >> 16) & 0x000000FF;
               green = (rgb >> 8) & 0x000000FF;
               blue = (rgb) & 0x000000FF;

               if ((red == 255 || red == 254) && (green == 255 || green == 254) && (blue == 255 || blue == 254)) { // 흰 부분일 경우
                  amountWhite++;

                  if (h == biHeight - 1 && w == biWidth - 1) { // 흰 부분에서 마지막 부분이 된 경우
                     if (maxhWhite <= amountWhite) {
                        maxhWhite = amountWhite;
                        endPos[0] = h;
                        endPos[1] = w;
                     }

                  }

               } else if (amountWhite != 0) { // 흰부분의 끝 부분
                  if (maxhWhite < amountWhite) {
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
                  amountWhite = 0;
               }
            }
         }
         
         if(maxhWhite<textcount) {
            System.out.println("사진에 흰색 부분이 충분하지 않습니다.");
         }
         

         System.out.println("문자 수: " + textcount);
         System.out.println("max White pixel: " + maxhWhite);
         System.out.println("White pixel End point: (" + endPos[1] + ", " + endPos[0] + ")");
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
}