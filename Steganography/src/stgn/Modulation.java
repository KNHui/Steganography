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
      System.out.println("���� �ؽ�Ʈ ���� �Է�: ");
      String tpath = reader.nextLine();

      System.out.println("Enter The path to the bmp file: ");
      String path = reader.nextLine();
      reader.close();
      
      File tfile = new File(tpath);
      byte[] by = new byte[(int)tfile.length()];
      
      try {
         
         FileReader textreader = new FileReader(tfile);
         int textcount = 0; // �ؽ�Ʈ ���� ��
         int textchar = 0; // ���� �ϳ��ϳ�
         
         while ((textchar = textreader.read()) != -1) {
            textcount++;
            System.out.print((char) textchar); 
            by[textcount-1]=(byte)textchar;
         }
         System.out.println();
         System.out.println("�ƽ�Ű�ڵ� : ");
         
         for(int i=0; i<by.length; i++) {
            System.out.print(by[i]+" ");
         }
         
         System.out.println();
         System.out.println("2������ ǥ�� : ");
         
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
         
         int[] endPos = new int[2]; // maxWhite�� ����
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

               if ((red == 255 || red == 254) && (green == 255 || green == 254) && (blue == 255 || blue == 254)) { // �� �κ��� ���
                  amountWhite++;

                  if (h == biHeight - 1 && w == biWidth - 1) { // �� �κп��� ������ �κ��� �� ���
                     if (maxhWhite <= amountWhite) {
                        maxhWhite = amountWhite;
                        endPos[0] = h;
                        endPos[1] = w;
                     }

                  }

               } else if (amountWhite != 0) { // ��κ��� �� �κ�
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
            System.out.println("������ ��� �κ��� ������� �ʽ��ϴ�.");
         }
         

         System.out.println("���� ��: " + textcount);
         System.out.println("max White pixel: " + maxhWhite);
         System.out.println("White pixel End point: (" + endPos[1] + ", " + endPos[0] + ")");
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
}