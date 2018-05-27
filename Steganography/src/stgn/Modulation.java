package stgn;

import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;

public class Modulation {
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		System.out.print("���� �ؽ�Ʈ ���� �Է�: ");
		String tpath = reader.nextLine();

		System.out.print("Enter The path to the bmp file: ");
		String path = reader.nextLine();
		
		System.out.print("Enter the path to the bmp file to save: ");
		String savepath = reader.nextLine();
		reader.close();

		String binary = "";

		File tfile = new File(tpath);
		byte[] dec = new byte[(int) tfile.length()];
		String[] bin = new String[(int) tfile.length()];
		try {

			FileReader textreader = new FileReader(tfile);
			int textCount = 0; // �ؽ�Ʈ ���� ��
			int textchar = 0; // ���� �ϳ��ϳ�
			int temp = 0;
			while ((textchar = textreader.read()) != -1) {
				textCount++;
				System.out.print((char) textchar);
				dec[textCount - 1] = (byte) textchar;
				temp = Integer.parseInt(Integer.toBinaryString(dec[textCount - 1]));
				bin[textCount - 1] = String.format("%07d", temp);
			}

			String[] binary_array = new String[textCount * 7];
			for (int i = 0; i < bin.length; i++) {
				binary += bin[i];
			}
			for (int i = 0; i < binary.length(); i++) { // ��Ʈ���� �ѱ��ھ� ���� �迭�� ����
				binary_array[i] = Character.toString(binary.charAt(i));
			}

			System.out.println();
			System.out.print("�ƽ�Ű�ڵ� : ");

			for (int i = 0; i < dec.length; i++) {
				System.out.print(dec[i] + " ");
			}

			System.out.println();
			System.out.print("2������ ǥ�� : ");
			for (int i = 0; i < dec.length; i++) {
				System.out.print(bin[i] + " ");
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

			int[] endPos = new int[2]; // maxWhite�� ����
			int maxhWhite = 0, amountWhite = 0;

			int rgb;
			int red = 0, green = 0, blue = 0;
			int FF = 0, FE = 0;

			inputOuter: 
			for (int h = 0; h < biHeight; h++) {
				for (int w = 0; w < biWidth; w++) {
					rgb = img.getRGB(w, h);
					red = (rgb >> 16) & 0x000000FF;
					green = (rgb >> 8) & 0x000000FF;
					blue = (rgb) & 0x000000FF;

					if ((red == 255 || red == 254) && (green == 255 || green == 254) && (blue == 255 || blue == 254)) {
						amountWhite++;
						if (red == 255)
							FF++;
						else
							FE++;
						if (green == 255)
							FF++;
						else
							FE++;
						if (blue == 255)
							FF++;
						else
							FE++;

						if (h == biHeight - 1 && w == biWidth - 1) { // ������ �������� �� �κ��� ���
							if (maxhWhite <= amountWhite) {
								maxhWhite = amountWhite;
								endPos[0] = h;
								endPos[1] = w;
							}
						}
					} else if (amountWhite != 0) { // ��κ��� �� �κ�
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
						if (textCount <= maxhWhite)
							break inputOuter;

						amountWhite = 0;
						FF = 0;
						FE = 0;
					}
				}
			}

			if (maxhWhite * 3 < one + zero) {
				System.out.println("������ ��� �κ��� ������� �ʽ��ϴ�.");
				System.exit(1);
			}
			
			int tempCount = one + zero;
			int index = 0;
			Color white;
			
			outputOuter:
			if (one >= zero || FF >= FE) {
				for (int h = endPos[0]; h >= 0; h--) {
					for (int w = endPos[1]; w >= 0; w--) {
						for (int j = 0; j < 3; j++) {
							if(tempCount <= 0) 
								break outputOuter;
							if(index == binary_array.length + 1) {
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
							} 
							else {
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
			} else if (one < zero || FF >= FE) {

			} else if (one >= zero || FF < FE) {

			} else { // one < zero || FF < FE

			}
			
			File outputfile = new File(savepath);
			ImageIO.write(img, "bmp", outputfile);	
			
			System.out.println("FF ����: " + FF);
			System.out.println("FE ����: " + FE);
			System.out.println("2���� ����: " + bin.length);
			System.out.println("���ڱ���: " + textCount);
			System.out.println("1����: " + one);
			System.out.println("0����: " + zero);
			System.out.println("max White pixel: " + maxhWhite);
			System.out.println("White pixel End point: (" + endPos[1] + ", " + endPos[0] + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}