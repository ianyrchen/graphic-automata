import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import java.util.*; 
import java.util.HashMap; 
import java.lang.*; 
import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class app extends PApplet {







SoundFile song;

public void settings(){
  setNums();
  size(PApplet.parseInt(xLen), PApplet.parseInt(yLen));
}

public void setup(){
  displayWelcomePage();
  song = new SoundFile(this, "click.wav");
}

public void draw()
{

}

boolean playing = true;
public void keyPressed(){
  myString="";
  song.play();
  
  if(key == '0'){
    //println("0 is pressed");
    drawElements(false);
    //afterSavingRawImage();
  }
  else if(key == '1'){   
    //println("1 is pressed");
    setCurrentRule(true, Rules.RULEOFTHIRDS);
    generateImageByRules();
  }
  else if(key == '2'){
    //println("2 is pressed");
    setCurrentRule(true, Rules.GOLDENMEAN);
    generateImageByRules();
  }
  else if(key == '3'){
    //println("3 is pressed");
    setCurrentRule(true, Rules.GOLDENTRIANGLE);
    generateImageByRules();
  }
  else if(key == '4'){
    //println("4 is pressed");
    drawElements(true);
    //afterSavingRawImage();
  }
  else if(key == '5'){
    displayDictionaryPage();
  }
  else if(key == 'm' || key == 'M'){
    displayNormalMenuPage();
  }
  else if(key == 'h' || key == 'H'){
    displayWelcomePage();
  }
  else {
    displayErrorPage();
  }
}


public void drawElements(boolean fragmented){
  drawImages(fragmented); 
  drawText();
  save(".\\data\\GeneratedImages\\generatedImage" + NumOfImagesGenerated +".jpg");
  NumOfImagesGenerated++;
}


public void drawText(){
  drawTitle();  
  drawHeaders();
}

public void drawTextByRules(){
  drawTitleByRules();  
  drawHeadersByRules();
}


public void interactiveArt(){
  randomHundredObjects(); // for decotation purpose
}


// gridX, betweenX, betweenY and gridY are from input file
public void drawGrid(){
  // Ian: doens't define stroke, so no line will be drawn
  stroke(240);
  for(float i = gridX; i < width; i+=(gridX + betweenX))
  {
    //stroke(255); // white color
    line(i, 0, i, height);
    line(i + betweenX, 0, i + betweenX, height);
  }
  
  for(float i = gridY; i < height; i+=(gridY + betweenY))
  {
    line(0, i, width, i);
    line(0, i + betweenY, width, i + betweenY);
  }
}

public void afterSavingRawImage()
{
  //colorMode(HSB, 360, 100, 100);
  colorMode(RGB, 255, 255, 255);
  PImage imgForMass;
  imgForMass = loadImage(".\\data\\GeneratedRawImages\\generatedRaw" + (NumOfImagesGenerated-1) + ".jpg"); 
  //println("load image generatedRaw" + (NumOfImagesGenerated-1));
  imgForMass.loadPixels();
  imageMode(CENTER);
  image(imgForMass, width/2, height/2);
      
  float [] sumOfRowGrayscale = new float [height];
  float [] sumOfColumnGrayscale = new float [width];
  getColorImgGrayscale(imgForMass, sumOfRowGrayscale, sumOfColumnGrayscale);

  int centerOfMassY = getCenterOfMassValue(sumOfRowGrayscale);
  int centerOfMassX = getCenterOfMassValue(sumOfColumnGrayscale);
    
  fill(0);
  //ellipse(centerOfMassX, centerOfMassY, 25, 25);
  
  // calculate the distance between center of mass and the physical center
  float distance = sqrt((width/2 - centerOfMassX)* (width/2 - centerOfMassX) + (height/2 - centerOfMassY) * (height/2 - centerOfMassY));
  //println("distance is " + distance);
  // save the generated images into a diretory
  save(".\\data\\GeneratedAcceptableImages\\generatedAcceptable" + (NumOfImagesGenerated-1) + ".jpg");   
}
float totalSumGrayscale = 0.0f;

// Get the sum of grayscale for each row and each column seperately
public void getColorImgGrayscale(PImage imgForMass, float[] sumOfRowGrayscale, float[] sumOfColumnGrayscale)
{
  totalSumGrayscale = 0.0f;
  
  for (int y = 0; y < height; y++) 
  {
    float sumOfGray = 0.0f;
    
    for (int x = 0; x < width; x++) 
    {
      int loc = x + y * width;
      int pColor = imgForMass.pixels[loc];
      float gray =  0.299f*red(pColor) + 0.587f*green(pColor) + 0.114f*blue(pColor);
      sumOfGray += gray;
    }
    sumOfRowGrayscale[y] = sumOfGray;
    ////println("sumOfRowGrayscale["+y+"]= "+sumOfGray);
    totalSumGrayscale += sumOfGray;
  }
  
  //set the value for Column array
  for (int x2 = 0; x2 < width; x2++) 
  {
    float sumOfGray2 = 0.0f;
    for (int y2 = 0; y2 < height; y2++) 
    {
      int loc2 = x2 + y2 * width;
      int pColor2 = imgForMass.pixels[loc2];
      float gray2 =  0.299f*red(pColor2) + 0.587f*green(pColor2) + 0.114f*blue(pColor2);
      sumOfGray2 += gray2;
    }
    sumOfColumnGrayscale[x2] += sumOfGray2;
    ////println("sumOfRowGrayscale["+x2+"]= "+sumOfGray2);
  }
}



public int getCenterOfMassValue(float[] sumOfGrayscale)
{
  float difference = totalSumGrayscale;
  float topSum = 0.0f; float bottomSum = totalSumGrayscale;
  int marker = 0; // recording the divide line of top and bottom part of the array
  
  for(int i = 0; i < sumOfGrayscale.length; i++)
  {
    if(difference > Math.abs(bottomSum - topSum))
    {
      difference = Math.abs(bottomSum - topSum);
      marker = i;
    }
    
    topSum += sumOfGrayscale[i];
    bottomSum -= sumOfGrayscale[i];
  }

  ////println("marker = "+marker);
  return marker;
}
String input[];
float xLen;
float yLen;
float gridX;
float betweenX;
float gridNumX;
float gridY;
float betweenY;
float gridNumY;
float redBackground;
float greenBackground;
float blueBackground;
int hueBackground;
int saturationBackground;
int brightnessBackground;
int backgroundColor;
int titleColor;
int titleBackColor;
int headerColor;
boolean randomFont;
//ArrayList<ColorInfo> presetColors = new ArrayList<ColorInfo>();
HashSet<Character> lowerCaseDescent = new HashSet<Character>();
HashSet<Character> lowerCaseNorm = new HashSet<Character>();

String appTitleString = "GRAPHIC AUTOMATA";
String welcomeString = "Welcome to Graphic Automata!";
String selectString = "Please select your generation method for your art \nby pressing different keys within the sketch area:";
String[] ruleStrings = new String[] {"Press '0' to not follow any rule", "Press '1' to follow Rule of Thirds", "Press '2' to follow Golden Mean", "Press '3' to follow Golden Triangle", 
                                     "Press '4' to generate fragmented image"};
/*String rule0String = "Press 0 to not follow any rule";
String rule1String = "Press 1 to follow Rule of Thirds";
String rule2String = "Press 2 to follow Golden Mean";
String rule3String = "Press 3 to follow Golden Triangle";
String rule4String = "Press 4 to follow Fancy Image (no rule)"; */
String gotoRefString = "Press '5' to go to the dictionary"; 
String gotoHomepageString = "Press 'H' to go back to the homepage";
String afterImageString = "After image generation, press 'M' to go back \nto this page";
String gotoMainMenuString = "Press 'M' to go to the main menu";
String centerMassStr = "Center of Mass: the average position of all the parts of \nthe system, weighted according to their masses. It is \nimportant to maintain balance within the design, which is \nwhy this app generates designs with centers of mass close \nto the actual physical center.\n";
String ruleOfThirdsStr = "\nRule of Thirds: The width and height of the sketch are \ndivided into 3 equal parts to generate a 3x3 grid. Ordering \nelements based on the grid results in a more aesthetically \npleasing design.\n";
String goldenMeanStr = "\nGolden Means: Use the Golden Ratio (1: 1.618) to divide the \nwidth and height into 3 parts, generating a 3x3 grid. \nSimilar to the Rule of Thirds, the Golden Mean grid is also \nanother design format.\n";
String goldenTriangleStr = "\nGolden Triangle: A diagonal line is draw across the sketch \nand 2 extra lines are drawn perpendicular to the diagonal \nline, also based on the Golden Ratio.\n";
String colorContrastStr = "\nColor Contrast: Good designs include color contrast, which \nattracts the eye. Contrast also adds visual interest to a \ncomposition.\n";
String myString = "";
PFont dictionaryFont;
PFont menuFont;
PFont appTitleFont;
PFont afterImageMsgFont;

public void setupColors()
{
  //println("===>Entering setupColors()");
  
  randomFont = random(1) > 0.5f;
  randomFont = false;  //Ian: need to take this out
  
 // colorMode(HSB, 360, 100, 100);
colorMode(RGB, 255, 255, 255);
  getDominantColorOfImage();
  
  hueBackground = PApplet.parseInt(dominantImgHue + random(20, 340))%360;
  saturationBackground = PApplet.parseInt(dominantImgSaturation + random(10, 90))%100;
  brightnessBackground = PApplet.parseInt(dominantImgBrightness + random(10, 90))%100;
  
  backgroundColor = color(hueBackground, saturationBackground, brightnessBackground);
  titleColor = color(PApplet.parseInt(hueBackground + random(150, 210))%360, PApplet.parseInt(saturationBackground + random(30, 70))%100, PApplet.parseInt(brightnessBackground + random(30, 70))%100);
  headerColor = color(PApplet.parseInt((hueBackground + random(150, 210)) % 360), PApplet.parseInt(saturationBackground+ random(30, 70))%100, PApplet.parseInt((brightnessBackground + random(30, 70))%100));
}


// Get X, Y and text from input file
public void setNums()
{
  input = loadStrings("input.txt");
  Scanner s = new Scanner(input[0]);
  gridX = Float.parseFloat(s.next());
  //println(input[0]);
  betweenX = Float.parseFloat(s.next());
  gridNumX = Float.parseFloat(s.next());
  s = new Scanner(input[1]);
  gridY = Float.parseFloat(s.next());
  betweenY = Float.parseFloat(s.next());
  gridNumY = Float.parseFloat(s.next());
  xLen = (gridX + betweenX)*(gridNumX - 1)+gridX;
  yLen = xLen * SCALE;
  
  // The hashset is used to randomnize the shape behind the text
  setupHashset();
}


public void setupHashset(){
  lowerCaseDescent.add('g');
  lowerCaseDescent.add('j');
  lowerCaseDescent.add('p');
  lowerCaseDescent.add('q');
  lowerCaseDescent.add('y');
  lowerCaseNorm.add('a');
  lowerCaseNorm.add('c');
  lowerCaseNorm.add('e');
  lowerCaseNorm.add('i');
  lowerCaseNorm.add('m');
  lowerCaseNorm.add('n');
  lowerCaseNorm.add('o');
  lowerCaseNorm.add('r');
  lowerCaseNorm.add('s');
  lowerCaseNorm.add('u');
  lowerCaseNorm.add('v');
  lowerCaseNorm.add('w');
  lowerCaseNorm.add('x');
  lowerCaseNorm.add('z');
}

//////////////////////////////
//Below is for Menu display
//////////////////////////////
public void displayWelcomePage()
{
  colorMode(RGB, 255, 255, 255);
  PImage img = loadImage("backgroundImage.jpg");
  img.resize(width, height);
  background(img);
   
  menuFont = loadFont("LucidaSans-20.vlw");
  appTitleFont = loadFont("AgencyFB-Bold-60.vlw");
  
  //Draw rectangle and App's title
  noFill();
  stroke(255);
  strokeWeight(4);
  rect(25, 120, 450, 65, 4);
  
  fill(255);
  strokeWeight(1);
  textSize(40);
  textFont(appTitleFont);
  text(appTitleString, 50, 175);
  
  textFont(menuFont);
  textSize(24);
  text(welcomeString, 70, 250);
  
  displayGoToMainMenu(650);
  displayGotoDictionaryString();
}

public void displayNormalMenuPage()
{
  colorMode(RGB, 255, 255, 255);
  
  PImage img = loadImage("backgroundImage.jpg");
  img.resize(width, height);
  background(img);
   
  menuFont = loadFont("LucidaSans-20.vlw");
  //appTitleFont = loadFont("AgencyFB-Bold-60.vlw");
  
  //Draw rectangle
  textFont(menuFont);
  fill(255);
  text(selectString, 10, 130);
  
  textSize(18);
  for(int i = 0; i < ruleStrings.length; i++)
  {
    noFill();
    stroke(255);
    strokeWeight(2);
    rect(40, 50*(i+4), 410, 40, 2);
    fill(255);
    text(ruleStrings[i], 50, 50*(i+4)+25);
  }
  
  text(afterImageString, 40, 500);
  displayGoBackToHome(680, 40);
}



public void displayGoBackToHome(int h, int startingX)
{
  afterImageMsgFont = loadFont("HPSimplified-Bold-20.vlw");
  textFont(afterImageMsgFont);
  noFill();
  stroke(255);
  strokeWeight(2);

  rect(startingX, h-40, 410, 40, 2);
  fill(255);
  text(gotoHomepageString, startingX + 10, h-10);
}

public void displayGoToMainMenu(int h)
{
  afterImageMsgFont = loadFont("HPSimplified-Bold-20.vlw");
  textFont(afterImageMsgFont);
  noFill();
  stroke(255);
  strokeWeight(2);

  rect(40, h-40, 410, 40, 2);
  text(gotoMainMenuString, 50, h-10);
}


public void displayGotoDictionaryString()
{
  //menuFont = loadFont("LucidaSans-20.vlw");
  menuFont = loadFont("HPSimplified-Bold-20.vlw");
  noFill();
  stroke(255);
  strokeWeight(2);
  rect(40, 660, 410, 40, 2);
  textFont(menuFont);
  text(gotoRefString, 50, 685);
}

public void displayDictionaryPage()
{
  colorMode(RGB, 255, 255, 255);
  dictionaryFont = loadFont("LucidaSans-14.vlw");
  appTitleFont = loadFont("AgencyFB-Bold-60.vlw");
  background(0,0,140);
  
  //Draw rectangle
  noFill();
  stroke(255);
  strokeWeight(4);
  rect(100, 50, 300, 68, 4);
  
  fill(255);
  strokeWeight(1);
  textSize(40);
  textFont(appTitleFont);
  text("Dictionary", 150, 105);
  
  //fill(0);
  textFont(menuFont);
  textSize(15);
  String refString = centerMassStr + colorContrastStr + ruleOfThirdsStr + goldenMeanStr + goldenTriangleStr;
  text(refString, 30, 170);

  displayGoBackToHome(650, 30);
}

public void displayErrorPage()
{
  background(50);
  String invalidString = "Invalid input. Please try again.\n\n\n";
  //println("invalid key is pressed and myString = " + invalidString);
  afterImageMsgFont = loadFont("HPSimplified-Bold-25.vlw");
  textFont(afterImageMsgFont);
  text(invalidString, 50, 150);
  fill(255, 255, 255);
  displayGoBackToHome(650, 30);
}
PImage loadedImg;
class DominantColor
{
  public float dominantImgHue;
  public float dominantImgSaturation;
  public float dominantImgBrightness;
}

public float dominantImgHue;
public float dominantImgSaturation;
public float dominantImgBrightness;


// stored HSB value in a string in the format of "H, S, B" as the key of the HashMap, Integer is the count of this key
HashMap<String, Integer> hmap = new HashMap<String, Integer>();

public static Map.Entry<String, Integer> getMaxEntry(Map<String, Integer> map){        
    Map.Entry<String, Integer> maxEntry = null;
    Integer max = Collections.max(map.values());

    for(Map.Entry<String, Integer> entry : map.entrySet()) {
        Integer value = entry.getValue();
        if(null != value && max == value) {
            maxEntry = entry;
        }
    }
    return maxEntry;
}

public void getDominantColorOfImage()
{
  hmap.clear();
  //DominantColor dColor = new DominantColor();
  //println("==> Entering getDominantColorOfImage()");
  for(int k=0; k<ImgNumbers.size(); k++)
  {
    loadedImg = loadImage(ImgNumbers.get(k)+".jpg");
  
    // loop through pixels and check the HSB to assign the pixel into correct bucket
    loadedImg.loadPixels();
    for(Integer i = 0; i < loadedImg.pixels.length; i++)
    {
      int pixelColor = loadedImg.pixels[i];
      int lowerbounB = GetLowerBoundBSValue(brightness(pixelColor));
      //if(lowerbounB == 0) // don't check saturation and hue, add to map
      {
      //  hmap.put("0,0,0", hmap.getOrDefault("0,0,0", 0) + 1); //increment the value
      }
     // else
      {
        int lowerbounS = GetLowerBoundBSValue(saturation(pixelColor));
        int lowerbounH = GetLowerBoundHueValue(hue(pixelColor));
        String str = String.valueOf(lowerbounH) + "," + String.valueOf(lowerbounS) + "," + String.valueOf(lowerbounB);
        hmap.put(str, hmap.getOrDefault(str, 0) + 1); //increment the value
      }
    }
  }
  // find the first key with the max count
  String maxK = getMaxEntry(hmap).getKey();
  //println("key is: " + maxK + ", Count =" + hmap.get(maxK));
  
  dominantImgHue = PApplet.parseFloat(maxK.split(",")[0]);
  dominantImgSaturation = PApplet.parseFloat(maxK.split(",")[1]);
  dominantImgBrightness = PApplet.parseFloat(maxK.split(",")[2]);
  //println("dominantImgHue = " + dominantImgHue);
  //println("dominantImgSaturation = " + dominantImgSaturation);
  //println("dominantImgBrightness = " + dominantImgBrightness);
}

public int GetLowerBoundBSValue(float bOrSValue)
{
  int result = 0;
  if(bOrSValue >=0 && bOrSValue < 25)
  {
    result = 0;
  }
  else if(bOrSValue >=25 && bOrSValue < 50)
  {
    result = 25;
  }
  else if(bOrSValue >=50 && bOrSValue < 75)
  {
    result = 50;
  }
  else if(bOrSValue >=75 && bOrSValue <= 100)
  {
    result = 75;
  }
  
  return result;
}

public int GetLowerBoundHueValue(float bOrSValue)
{
  int result = 0;
  if(bOrSValue >=0 && bOrSValue < 30)
  {
    result = 0;
  }
  else if(bOrSValue >=30 && bOrSValue < 60)
  {
    result = 30;
  }
  else if(bOrSValue >=60 && bOrSValue < 90)
  {
    result = 60;
  }
  else if(bOrSValue >=90 && bOrSValue < 120)
  {
    result = 90;
  }
  else if(bOrSValue >= 120 && bOrSValue < 150)
  {
    result = 120;
  }
  else if(bOrSValue >= 150 && bOrSValue < 180)
  {
    result = 150;
  }
  else if(bOrSValue >= 180 && bOrSValue < 210)
  {
    result = 180;
  }
  else if(bOrSValue >= 210 && bOrSValue < 240)
  {
    result = 210;
  }
  else if(bOrSValue >= 240 && bOrSValue < 270)
  {
    result = 240;
  }
  else if(bOrSValue >= 270 && bOrSValue < 300)
  {
    result = 270;
  }
  else if(bOrSValue >= 300 && bOrSValue < 330)
  {
    result = 300;
  }
  else if(bOrSValue >= 330 && bOrSValue <= 360)
  {
    result = 330;
  }

  return result;
}
String[] serifFonts = {
"Algerian",
"Californian FB",
"Calisto MT",
"Cambria",
"Centaur",
"Century Schoolbook",
"Constantia",
"Elephant",
"Garamond",
"Georgia",
"High Tower Text",
"Jokerman",
"Lucida Bright",
"MS Serif",
"OCR A Extended",
"Book Antiqua",
"Perpetua",
"Playbill",
"Sylfaen",
"Times New Roman",
"Wide Latin",
};

String[] sanserifFonts = {
"Agency FB",
"Arial",
"Calibri",
"Century Gothic",
"Corbel",
"Haettenschweiler",
"Helvetica",
"Impact",
"Lucida Sans",
"Lucida Sans Unicode",
"MS Sans Serif",
"Segoe UI",
"Tahoma",
"Trebuchet MS",
"Verdana",
};

String[] monoFonts = {
"Consolas",
"Courier",
"Courier New",
"Lucida Console",
"Lucida Sans Typewriter",
"MS Gothic",
"SimHei",
"SimSun"
};

String[] scriptFonts = {
"Mistral",
"Papyrus",
"Segoe Script",
"Forte",
"Monotype Corsiva",
"Old English Text MT",
"Comic Sans MS",
"Freestyle Script",
"Lucida Handwriting"
};
// Store rectangular image's coordinates
class ImageCoor 
{
  float x1, y1, x2, y2;
  public ImageCoor(float x1, float y1, float x2, float y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }
}


public void drawImages(boolean fragmented) 
{
  noStroke();
  //06172021 - simply display the image at a random location
  PImage img;
  getImageNumbers();
  img = loadImage(ImgNumbers.get(0)+".jpg");
  //colorMode(HSB, 360, 100, 100);
  colorMode(RGB, 255, 255, 255);
  setupColors();
  background(backgroundColor);
    
  int imageSize = (int)random(width/3, width);
  img.resize(imageSize, 0);
  image(img, random(0, width/2), random(0, height/2));

  if(fragmented)
  {
    drawRandomRectangles(); // draw random-sized ractangles 
    fragmentFloodFill(); 
  }
}


public void floodPic(int[][] matrixPic, int curX, int curY, float xInterval, float yInterval, float minX, float minY, float maxX, float maxY, PImage img, int counter) 
{
  if (curX >= matrixPic[0].length || curX < 0) 
  {
    return;
  }
  
  if (curY >= matrixPic.length || curY < 0) 
  {
    return;
  }
  
  if (matrixPic[curY][curX] == 1) 
  {
    return;
  }
  
  if (counter <= 0)
  {
    return;
  }
  
  ArrayList<Integer> a1 = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
  Collections.shuffle(a1);
  matrixPic[curY][curX] = 1;
  PImage img3 = createImage((int)xInterval, (int)yInterval, RGB);
  
  for (int ii = (int)xInterval * curX; ii < (int)xInterval * (curX + 1); ii++) 
  {
    for (int j = (int)yInterval * curY; j < (int)yInterval * (curY + 1); j++) 
    {
      img3.pixels[(j - (int)(yInterval * curY))*img3.width+(ii - (int)(xInterval * curX))] = img.pixels[j*img.width+ii];
    }
  }
  
  image(img3, minX + xInterval * curX, minY + yInterval * curY, xInterval, yInterval);
  
  for (int i = 0; i < 4; i++) 
  {
    int tmpRand = a1.get(i);
    counter--;
    if (counter <= 0) 
    {
      return;
    }
    
    if (tmpRand == 0) 
    {
      floodPic(matrixPic, curX, curY + 1, xInterval, yInterval, minX, minY, maxX, maxY, img, counter);
    } 
    else if (tmpRand == 1) 
    {
      floodPic(matrixPic, curX, curY - 1, xInterval, yInterval, minX, minY, maxX, maxY, img, counter);
    } 
    else if (tmpRand == 2) 
    {
      floodPic(matrixPic, curX + 1, curY, xInterval, yInterval, minX, minY, maxX, maxY, img, counter);
    } 
    else if (tmpRand == 3) 
    {
      floodPic(matrixPic, curX - 1, curY, xInterval, yInterval, minX, minY, maxX, maxY, img, counter);
    }
  }
}


public void takeRest(int[][] matrixPic, float xInterval, float yInterval, float minX, float minY, float maxX, float maxY, PImage img)
{
  int offsetX = (int)(random(-5, 5));
  int offsetY = (int)(random(-5, 5));
  for(int i = 0; i < matrixPic.length; i++)
  {
    for(int ii2 = 0; ii2 < matrixPic[0].length; ii2++)
    {
      if(matrixPic[i][ii2] == 0)
      {
        PImage img3 = createImage((int)xInterval, (int)yInterval, RGB);
       // //println((int)xInterval * ii2 + " " + ((int)xInterval * (ii2 + 1)));
        ////println((int)yInterval * i + " " + ((int)yInterval * (i + 1)));
        for (int ii = (int)xInterval * ii2; ii < (int)xInterval * (ii2 + 1); ii++) 
        {
          for (int j = (int)yInterval * i; j < (int)yInterval * (i + 1); j++) 
          {
            ////println((j - (int)(yInterval * i)) +  " " + (ii - (int)(xInterval * ii2)));
            ////println(ii + " " + xInterval + " " + ii2);
            img3.pixels[(j - (int)(yInterval * i))*img3.width+(ii - (int)(xInterval * ii2))] = img.pixels[j*img.width+ii];
          }
        }
        image(img3, minX + xInterval * ii2 + xInterval * offsetX, minY + yInterval * i + offsetY * yInterval, xInterval, yInterval);
      }
    }
  }
}



public void fragmentFloodFill() 
{
  int numImages = 4;
  int smallerBy = 1;
  float randCutOff = PApplet.parseInt(random(5, 10));
  float deviation = 1;
  float initX = gridNumX * 2/ 3;
  float initY = gridNumY * 2 / 3;
  ArrayList<String> imageNames = new ArrayList<String>();
  
  for (int i = 1; i <= numImages; i++) 
  {
    imageNames.add(parseInt(i) + ".jpg");
  }
  
  Collections.shuffle(imageNames);
  
  int i2 = 0;
  for (int i = 0; i < 1; i++) 
  {
    if (i2 >= numImages)
      i2 = 0;
    String imgN = imageNames.get(i2);
    int imgRandX = (int)random(initX - deviation, initX) + 1;
    int imgRandY = (int)random(initY - deviation, initY) + 1;
    int imgRandX2 = (int)random(0, gridNumX - randCutOff);
    int imgRandY2 = (int)random(0, gridNumY - randCutOff);
    float imgW = imgRandX * gridX + (imgRandX) * betweenX;
    float imgH = imgRandY * gridY + (imgRandY) * betweenY;
    float imgX = imgRandX2 * (gridX + betweenX);
    float imgY = imgRandY2 * (gridY + betweenY);
    PImage img = loadImage(imgN);
    
    img.resize((int)imgW, (int)imgH);
    int[][] matrixPic = new int[imgRandY][imgRandX];
    floodPic(matrixPic, (int)random(imgRandX), (int)random(imgRandY), (gridX + betweenX), (gridY + betweenY), imgX, imgY, imgX + imgW, imgY + imgH, img, ((imgRandX - 1) * (imgRandY - 1) / 2));
   
    if(random(1) > 0.1f)
      takeRest(matrixPic, (gridX + betweenX), (gridY + betweenY), imgX, imgY, imgX + imgW, imgY + imgH, img);

    imgRandX = (int)random(initX - deviation, initX) + 1;
    imgRandY = (int)random(initY - deviation, initY) + 1;
    imgRandX2 = (int)random(0, gridNumX - randCutOff);
    imgRandY2 = (int)random(0, gridNumY - randCutOff);
    imgW = imgRandX * gridX + (imgRandX) * betweenX;
    imgH = imgRandY * gridY + (imgRandY) * betweenY;
    imgX = imgRandX2 * (gridX + betweenX);
    imgY = imgRandY2 * (gridY + betweenY);
    
    /*  PImage img4 = createImage(img.width, img.height / 2, RGB);
     
     
     for(int ii = 0; ii < img.width; ii++)
     {
     for (int j = 0; j < img.height / 2; j++) {
     img4.pixels[j*img.width+ii] = img.pixels[(j + img.height / 2)*img.width+ii];
     }
     }
     image(img4, imgX, imgY, imgW, imgH/2);
     //image(img, imgX, imgY, imgW, imgH);*/
  }
  
  initX -= smallerBy;
  initY -= smallerBy;
  i2++;
  tint(255, 255);
}


public void fragmentImages(int drawTime) 
{
  int numImages = 4;
  int smallerBy = 1;
  float randCutOff = 2;
  float deviation = 1;
  float initX = gridNumX * 2/ 3;
  float initY = gridNumY * 2 / 3;
  ArrayList<String> imageNames = new ArrayList<String>();
  for (int i = 1; i <= numImages; i++) 
  {
    imageNames.add(parseInt(i) + ".jpg");
  }
  Collections.shuffle(imageNames);
  int i2 = 0;
  for (int i = 0; i < drawTime; i++) 
  {
    if (i2 >= numImages)
      i2 = 0;
    String imgN = imageNames.get(i2);
    int imgRandX = (int)random(initX - deviation, initX) + 1;
    int imgRandY = (int)random(initY - deviation, initY) + 1;
    int imgRandX2 = (int)random(0, gridNumX - randCutOff);
    int imgRandY2 = (int)random(0, gridNumY - randCutOff);
    float imgW = imgRandX * gridX + (imgRandX) * betweenX;
    float imgH = imgRandY * gridY + (imgRandY) * betweenY;
    float imgX = imgRandX2 * (gridX + betweenX);
    float imgY = imgRandY2 * (gridY + betweenY);
    PImage img = loadImage(imgN);

    boolean isFrag = random(1)>0.8f;
    if (random(1) < 0.5f)
      tint(255, random(90, 120));
    if (isFrag)
    {
      PImage img3 = createImage(img.width, img.height / 2, RGB);
      for (int ii = 0; ii < img.width; ii++)
      {
        for (int j = 0; j < img.height / 2; j++)
        {
          img3.pixels[j*img.width+ii] = img.pixels[j*img.width+ii];
        }
      }
      image(img3, imgX, imgY, imgW, imgH/2);

      imgRandX = (int)random(initX - deviation, initX) + 1;
      imgRandY = (int)random(initY - deviation, initY) + 1;
      imgRandX2 = (int)random(0, gridNumX - randCutOff);
      imgRandY2 = (int)random(0, gridNumY - randCutOff);
      imgW = imgRandX * gridX + (imgRandX) * betweenX;
      imgH = imgRandY * gridY + (imgRandY) * betweenY;
      imgX = imgRandX2 * (gridX + betweenX);
      imgY = imgRandY2 * (gridY + betweenY);
      PImage img4 = createImage(img.width, img.height / 2, RGB);

      for (int ii = 0; ii < img.width; ii++)
      {
        for (int j = 0; j < img.height / 2; j++) 
        {
          img4.pixels[j*img.width+ii] = img.pixels[(j + img.height / 2)*img.width+ii];
        }
      }
      image(img4, imgX, imgY, imgW, imgH/2);
      //image(img, imgX, imgY, imgW, imgH);
    } 
    else 
    {
      image(img, imgX, imgY, imgW, imgH);
    }

    initX -= smallerBy;
    initY -= smallerBy;
    i2++;
    tint(255, 255);
  }
}


public void drawCheckAllCoor(String imgN, float xCor1, float yCor1, float width1, float height1, ArrayList<ImageCoor> arr)
{
  for (int i = 0; i < gridNumX; i++) 
  {
    for (int i2 = 0; i2 < gridNumY; i2++) 
    {
    }
  }
}

public boolean  doOverlap(float x1, float y1, float x2, float y2, float px1, float py1, float px2, float py2) 
{
   // If one rectangle is on left side of other
   if (x1 >= px2 || px1 >= x2) 
   {
      return false;
   }
 
   // If one rectangle is above other
   if (y1 >= py2 || py1 >= y2) 
   {
     return false;
   }
 
   return true;
}

public boolean checkCollide(ArrayList<ImageCoor> arr, float x1, float y1, float x2, float y2) 
{
  for (int i = 0; i < arr.size(); i++) {
    ImageCoor curImg = arr.get(i);

    // If one rectangle is on left side of other OR if one rectangle is above other
    ////println("\ncurImg.x1 =" + curImg.x1 +" curImg.x2 =" + curImg.x2 +" curImg.y1 =" + curImg.y1 +" curImg.y2 =" + curImg.y2);
    ////println("x1 =" + x1 +" x2 =" + x2 +" y1 =" + y1 +" y2 =" + y2);
    if (doOverlap(curImg.x1, curImg.y1, curImg.x2, curImg.y2, x1, y1, x2, y2))
      return true;
  }
  return false;
}

// draw random-sized random colored rectangles
public void drawRandomRectangles() 
{
  //println("==> drawRandomRectangles()");
  int numImages = 2;
  int smallerBy = 1;
  float randCutOff = 2;
  float deviation = 4;
  float initX = gridNumX;
  float initY = gridNumY;
  
  for (int i = 0; i < numImages; i++) {
    int imgRandX = (int)random(initX - deviation, initX) + 1;
    int imgRandY = (int)random(initY - deviation, initY) + 1;
    int imgRandX2 = (int)random(0, gridNumX - randCutOff);
    int imgRandY2 = (int)random(0, gridNumY - randCutOff);
    float imgW = imgRandX * gridX + (imgRandX) * betweenX;
    float imgH = imgRandY * gridY + (imgRandY) * betweenY;
    float imgX = imgRandX2 * (gridX + betweenX);
    float imgY = imgRandY2 * (gridY + betweenY);
    
    pushMatrix();
    colorMode(RGB, 255, 255, 255);
    fill(color(random(255), random(255), random(255)));
    rect(imgX, imgY, imgW, imgH);
    popMatrix();
    
    initX -= smallerBy;
    initY -= smallerBy;
  }
}

// Ian: not used: get 1.jpg, 2.jpg, 3.jpg and 4.jpg
public void drawRandomImages(int drawTime)
{
  //println("random");
  int numImages = 11;
  int smallerBy = 1;
  float randCutOff = 2;
  float deviation = 4;
  float initX = gridNumX;
  float initY = gridNumY;
  ArrayList<String> imageNames = new ArrayList<String>();
  for (int i = 1; i <= numImages; i++) 
  {
    imageNames.add(parseInt(i) + ".jpg");
  }
  Collections.shuffle(imageNames);
  
  int i2 = 0;
  for (int i = 0; i < drawTime; i++) 
  {
    if (i2 >= numImages)
      i2 = 0;
    String imgN = imageNames.get(i2);
    int imgRandX = (int)random(initX - deviation, initX) + 1;
    int imgRandY = (int)random(initY - deviation, initY) + 1;
    int imgRandX2 = (int)random(0, gridNumX - randCutOff);
    int imgRandY2 = (int)random(0, gridNumY - randCutOff);
    float imgW = imgRandX * gridX + (imgRandX) * betweenX;
    float imgH = imgRandY * gridY + (imgRandY) * betweenY;
    float imgX = imgRandX2 * (gridX + betweenX);
    float imgY = imgRandY2 * (gridY + betweenY);
    PImage img = loadImage(imgN);
    image(img, imgX, imgY, imgW, imgH);
    initX -= smallerBy;
    initY -= smallerBy;
    i2++;
  }
}

//Ian: not used. To draw 1.jpg-4.jpg many times
public void drawNoCollideImages(int drawTimeInput) 
{
  //println("drawNoCollideImages()");
  int numImages = 11;
  int drawTimes = drawTimeInput;
  float initX = 4;
  float initY = 4;
  ArrayList<String> imageNames = new ArrayList<String>();
  ArrayList<ImageCoor> imageCoordinates = new ArrayList<ImageCoor>();
  
  for (int i = 1; i <= numImages; i++) 
  {
    imageNames.add(parseInt(i) + ".jpg");
  }
  
  Collections.shuffle(imageNames);
  
  for (int i = 0; i < drawTimes; i++) 
  {
    String imgN = imageNames.get(i % numImages);

    float imgRandX = initX;
    float imgRandY = initY;
    float imgRandX2 = (int)random(0, gridNumX - 1);
    float imgRandY2 = (int)random(0, gridNumY - 1);
    float imgW = initX * (gridX + betweenX);
    float imgH = initY * (gridY + betweenY);
    float imgX = imgRandX2 * (gridX + betweenX);
    float imgY = imgRandY2 * (gridY + betweenY);
    int maxChange = 0;
    
    while (checkCollide(imageCoordinates, imgRandX2, imgRandY2, imgRandX2 + imgRandX, imgRandY2 + imgRandY))
    {
      ////println(i + " " + imgX + " " + imgY + " " + imgW + " " + imgH);
      imgRandX2 = (int)random(0, gridNumX - 1) + 1;
      imgRandY2 = (int)random(0, gridNumY - 1) + 1;
      imgX = imgRandX2 * (gridX + betweenX);
      imgY = imgRandY2 * (gridY + betweenY);
      maxChange++;
      if (maxChange % 100 == 0) 
      {
        imgW -= (gridX + betweenX);
        imgH -= (gridY + betweenY);
      }
      if (imgW < gridX + betweenX)
        imgW = gridX + betweenX;
      if (imgH < gridX + betweenX)
        imgH = gridY + betweenY;
      if (maxChange == 1000) 
      {
        break;
      }
    }
    
    ////println(i + " " + imgRandX + " " + imgRandY + " " + imgRandX2 + " " + imgRandY2);
    // //println("Met");
    //tint(255, 126);
    PImage img = loadImage(imgN);
    if (maxChange >= 100)
      drawCheckAllCoor(imgN, imgRandX2, imgRandY2, imgRandX2 + imgRandX, imgRandY2 + imgRandY, imageCoordinates);
    else 
    {
      image(img, imgX, imgY, imgW, imgH);
      ImageCoor curImage = new ImageCoor(imgRandX2, imgRandY2, imgRandX2 + imgRandX, imgRandY2 + imgRandY);
      imageCoordinates.add(curImage);
    }
    initY -= (int)random(2);
    initX -= (int)random(2);
  }
}
static int NumOfImagesGenerated = 0;
GridCoordinates CurrentCoordinates;
Rules CurrentRule;
int TOTALIMAGES = 8;
int CHOSENIMAGES = 1; // choose at most 1 images from 7 images for now
ArrayList<ImageCoor> ImgBoundaryCoordinates = new ArrayList<ImageCoor>();
ArrayList<Integer> ImgNumbers = new ArrayList<Integer>();

public void getImageNumbers()
{
  //int numOfImgs = CHOSENIMAGES;
  int numOfImgs = min((int)random(1,TOTALIMAGES), CHOSENIMAGES);
  int num;
  for(int i = 0; i < numOfImgs; i++)
  {
    num = (int)random(1,TOTALIMAGES);
    while (ImgNumbers.contains(num))
    {
      num = (int)random(1,TOTALIMAGES);
    }
    ImgNumbers.add(num);
  }
}

public void setCurrentRule(boolean usePassedRule, Rules rule)
{
  if(!usePassedRule)
  {
    float r = random(1);
  
    CurrentRule = Rules.RULEOFTHIRDS;
    if(r >= 2.0f/3.0f)
      CurrentRule = Rules.RULEOFTHIRDS;
    if(r <= 1.0f/3.0f)
      CurrentRule = Rules.GOLDENMEAN;
    if(r > 1.0f/3.0f && r < 2.0f/3.0f)
      CurrentRule = Rules.GOLDENTRIANGLE;
  }
  else
    CurrentRule = rule;
}

public void generateImageByRules()
{   
  //CurrentRule = Rules.GOLDENTRIANGLE; // Ian tmp
  //CurrentRule = Rules.RULEOFTHIRDS;
  CurrentCoordinates = generatePivotalPointsByRules(CurrentRule);
  
  // loop through the pivotal  points

//Ian tmp
  int i = (int)random(0, CurrentCoordinates.GetCoordinates().size());
  //for(int i = 0; i < CurrentCoordinates.GetCoordinates().size(); i++)
  {
    resetCoordinatesOccupancy();  //Ian correct?
    
    getImageNumbers(); //randomly decide # of images to load and the images' sequence#
    Coordinate coordinate = CurrentCoordinates.GetCoordinateByIndex(i);
    PImage[] imgs = new PImage[ImgNumbers.size()];
    float [] xValue = new float[ImgNumbers.size()]; 
    float [] yValue = new float[ImgNumbers.size()]; 
    
    for(int k = 0; k < ImgNumbers.size(); k++)
    //for(int k = 0; k < min(2,ImgNumbers.size()) ; k++)
    {
      imgs[k] = loadImage(ImgNumbers.get(k)+".jpg"); 
      float imageSize = random(width/2, width);
      imgs[k].resize((int)imageSize, 0);
      
      if(k==0)     // first image will occupy a pivotal point
      {
        coordinate.SetIsOccupiedByImg(true);      
        
        boolean withinBound = false;
        while(!withinBound)
        {
          if((coordinate.x - imageSize/2) > 0 && (coordinate.y-imageSize/2)> 0 && (coordinate.x+imageSize/2)<width && (coordinate.y+imageSize/2) <height)
          {
            withinBound = true;
          }
          else
          {
            imageSize -= 10;
            imgs[k].resize((int)imageSize, 0);
          }
        }
        
        xValue[k] = coordinate.x;
        yValue[k] = coordinate.y;
        
        ImageCoor imgCoor = new ImageCoor(coordinate.x - imageSize/2, coordinate.y-imageSize/2, coordinate.x+imageSize/2, coordinate.y+imageSize/2);
        ImgBoundaryCoordinates.add(imgCoor);
      }
      else  // display along the lines
      { 
        boolean isCollide = true;
        while(isCollide && imageSize > 100)
        {
          imageSize -= 5;
          if(CurrentRule == Rules.GOLDENTRIANGLE)
          {
            int ln = (int) random(0, 2);
            Line l = LinesForGoldenTriangle.get(ln);
            xValue[k] = random(width/5, width/2);
            yValue[k] = l.getYValueByX(xValue[k]);
          }
          else  // the other 2 rules
          {
            float xy = random(0, 1);

            if(xy >=0.5f) // y value is not changed: horizontal line
            {
              xValue[k] = random(width/5, width/2);
              yValue[k] = coordinate.y;
            }
            else  // x value is not changed: vertical line
            {
              xValue[k] = coordinate.x;
              yValue[k] = random(height/6, height/3);
            }
          }
          
          boolean withinBound = false;
          while(!withinBound && imageSize > 100)
          {
            if((xValue[k]+imageSize/2)<width && (yValue[k] + imageSize/2) <height && (xValue[k]-imageSize/2) > 0 && (yValue[k]-imageSize/2) -imageSize/2 > 0)
              withinBound = true;
            else
            {
              imageSize -= 5;
            }
          }
          isCollide = checkCollide(ImgBoundaryCoordinates, xValue[k]-imageSize/2, yValue[k]-imageSize/2, xValue[k]+imageSize/2, yValue[k] + imageSize/2);
          //println("imageSize = "+imageSize + " isCollide =" +isCollide);
        }
    
        imgs[k].resize((int)imageSize, 0);
        ImageCoor imgCoor = new ImageCoor(xValue[k]-imageSize/2, yValue[k]-imageSize/2, xValue[k]+imageSize/2, yValue[k] + imageSize/2);
        ImgBoundaryCoordinates.add(imgCoor);
      }

    }
    
    setupColors();
    drawGridByRule(CurrentRule);
    
    imageMode(CENTER);
    for(int k = 0; k < ImgNumbers.size(); k++)
    //for(int k = 0; k < min(2,ImgNumbers.size()); k++)
    {
      image(imgs[k], xValue[k], yValue[k]);
    }
    
    
    //Ian tmp test code
    //colorMode(HSB, 360, 100, 100);
    colorMode(RGB, 255, 255, 255);
    int testColor = color(dominantImgHue, dominantImgSaturation, dominantImgBrightness);
    //fill(testColor);
    //ellipse(width/2, height/2, 100, 100);
  
    drawTextByRules();

    // save the generated images into a diretory
    save(".\\data\\GeneratedImages\\generatedwImage" + NumOfImagesGenerated +".jpg");
    NumOfImagesGenerated++; 
    
    stroke(255);
  }
  
  //afterSavingRawImage();
}

public void resetCoordinatesOccupancy()
{
  TextBoundaryCoordinates = new ArrayList<ImageCoor>(); //reset this array as well
  ImgBoundaryCoordinates = new ArrayList<ImageCoor>(); //reset this array as well
  ImgNumbers.clear();
  
  for(int i = 0; i < CurrentCoordinates.GetCoordinates().size(); i++)
  {
    //reset coordinate's occupancy
    CurrentCoordinates.GetCoordinateByIndex(i).SetIsOccupiedByText(false);
    CurrentCoordinates.GetCoordinateByIndex(i).SetIsOccupiedByImg(false);
  }
}
enum Rules {
  RULEOFTHIRDS,
  GOLDENMEAN,
  GOLDENTRIANGLE
}

ArrayList<Line> LinesForGoldenTriangle = new ArrayList<Line>();

class Coordinate
{
  float x, y;
  private boolean isOccupiedByText;
  private boolean isOccupiedByImg;
  
  public Coordinate(float x, float y) {
    this.x = x;
    this.y = y;
    isOccupiedByText = false;
  }
  
  public boolean GetIsOccupiedByText()
  {
    return isOccupiedByText;
  }
  
  public void SetIsOccupiedByText(boolean occupied)
  {
    isOccupiedByText = occupied;
  }
  
  public boolean GetIsOccupiedByImg()
  {
    return isOccupiedByImg;
  }
  
  public void SetIsOccupiedByImg(boolean occupied)
  {
    isOccupiedByImg = occupied;
  }
}

// used to keep track of current grid's coordinates
class GridCoordinates
{
  private ArrayList<Coordinate> coordinates;
  
  public GridCoordinates() {
    coordinates = new ArrayList<Coordinate>();
  }
  
  public void AddCoordinate(float x, float y)
  {
    Coordinate co = new Coordinate(x,y);
    coordinates.add(co);
  }
  
  public ArrayList<Coordinate> GetCoordinates()
  {
    return coordinates;
  }
  
  public Coordinate GetCoordinateByIndex(int i)
  {
    return coordinates.get(i);
  }
  
}

class Line
{
  private float x1;
  private float y1;
  private float x2;
  private float y2;
  
  public Line(float px1, float py1, float px2, float py2)
  {
    x1 = px1;
    y1 = py1;
    x2 = px2;
    y2 = py2;
  }
  
  public float getYValueByX(float x)
  {
    float y = 0;
    if(x2 != x1)  
      y = ((y2-y1)/(x2-x1)) *(x-x1) +y1;
      
    return y;
  }
  
  public float getXValueByY(float y)
  {
    float x = 0;
    
    if(y2 != y1)  
      x = ((x2-x1)/(y2-y1)) *(y-y1) + x1;
      
    return x;
  } 
}


// Generate the focal points' coordinates based on Rule of Thirds,  Golden Mean and Golden Triangle
public GridCoordinates generatePivotalPointsByRules(Rules rule){
  stroke(240);
  //stroke(255); // white color
  GridCoordinates newCoordinates = new GridCoordinates();
  float cellWidth = 0;
  float cellHeight = 0;
    
  if(rule == Rules.RULEOFTHIRDS)
  {
    cellWidth = width/3;
    cellHeight = height/3;
  
    for(int i = 1; i < 3; i++)
    {    
      for(int j = 1; j < 3; j++)
      {
        newCoordinates.AddCoordinate(cellWidth*i, cellHeight*j);
      }
    }
  }
  
  if(rule == Rules.GOLDENMEAN)
  {
    float xOffset = width/(1+SCALE);
    float yOffset = height/(1+SCALE);
    
    // Add 4 pivotal points
    newCoordinates.AddCoordinate(xOffset, yOffset);
    newCoordinates.AddCoordinate(xOffset, height - yOffset);
    newCoordinates.AddCoordinate(width - xOffset, yOffset);
    newCoordinates.AddCoordinate(width - xOffset, height - yOffset);
  }

  if(rule == Rules.GOLDENTRIANGLE)  
  {  
    //Two focal points coordinates
    float squareSum = width*width + height*height;
    float x1 = width*height*height/squareSum;
    float y1 = height*height*height/squareSum;
    float x2 = width*width*width/squareSum;
    float y2 = width*width*height/squareSum;
    
    newCoordinates.AddCoordinate(x1, y1); // the point with bigger y value and bigger x value
    ////println("\n\nGOLDEN TRIANGLE point x1="+x1+" y1="+y1 +"----------------");
    
    // there are 3 lines for Golden Triangle rule
    Line newL = new Line(x1, y1, 0, height); 
    LinesForGoldenTriangle.add(newL);
    newCoordinates.AddCoordinate(x2, y2);
    newL = new Line(x2, y2, width, 0); 
    LinesForGoldenTriangle.add(newL);
    newL = new Line(x2, y2, x1, y1); 
    LinesForGoldenTriangle.add(newL);
  }
  
  return newCoordinates;  
}

public void drawGridByRule(Rules rule)
{
  background(backgroundColor);
  stroke(240);
  //stroke(255); // white color
    
  if(rule == Rules.RULEOFTHIRDS)
  {
    float cellWidth = width/3;
    float cellHeight = height/3;

    for(int i = 1; i < 3; i++)
    {
      line(cellWidth*i, 0, cellWidth*i, height); //draw vertical lines
    }
  
    for(int j = 1; j < 3; j++) //draw horizontal lines
    {
      line(0, cellHeight*j, width, cellHeight*j);
    }
  }
  
  if(rule == Rules.GOLDENMEAN)
  {
    float xOffset = width/(1+SCALE);
    float yOffset = height/(1+SCALE);
    
    // draw grid
    line(xOffset, 0, xOffset, height);
    line(width - xOffset, 0, width - xOffset, height);
    line(0, yOffset, width, yOffset);
    line(0, height-yOffset, width, height-yOffset);
  }

  if(rule == Rules.GOLDENTRIANGLE)  
  {  
    
    line(0, 0, width, height); //diagnal line
    
    //Two focal points coordinates
    float squareSum = width*width + height*height;
    float x1 = width*height*height/squareSum;
    float y1 = height*height*height/squareSum;
    float x2 = width*width*width/squareSum;
    float y2 = width*width*height/squareSum;
    
    // Draw penperdicular lines
    line(0, height, x1, y1);
    line(width, 0, x2, y2);
  }
}
int[] col_arr = 
{
  color(0, 0, 0),
  color(255, 0, 0),
  color(255, 255, 0),
  color(0, 255, 0),
  color(0, 255, 255),
  color(0, 0, 255),
  color(255, 0, 255),
  color(255, 255, 255)
};
ArrayList<Object> objects = new ArrayList<Object>();
float radius = 25; // default size of object
Boolean random_col_bool = false;
float random_dist = 5; // initialize dist for random movement
Boolean col_background_bool = false;
int col_cycle = 0;
int col_global = color(col_arr[col_cycle]); // initialize color of objects
String[] shape_arr = {"circle", "square", "star"};


public void randomHundredObjects(){
  int object_count = PApplet.parseInt(random(1, 20)); // create up to 20 objects
  float object_x = random(0, 1000);
  float object_y = random(0, 800);
  
  int object_col_cycle = PApplet.parseInt(random(col_arr.length));
  int object_shape_cycle = PApplet.parseInt(random(shape_arr.length));
  float object_radius = random(0, radius);
  
  for(int i = 0; i < object_count; i++)
  {
    objects.add(new Object(object_x, object_y, object_radius, col_arr[object_col_cycle], shape_arr[object_shape_cycle]));
    
    // reset random values
    object_x = random(0, 1000);
    object_y = random(0, 800);
    object_col_cycle = PApplet.parseInt(random(col_arr.length));
    object_shape_cycle = PApplet.parseInt(random(shape_arr.length));
    object_radius = random(0, radius);     
    
  }
  
  for (Object object : objects)
  {
     object.random_move();
  }
}

class Object 
{
  float x = 0, y = 0; // initialize location of object
  // initialize adjustable fields
  float r = 20; 
  String shape = "";
  int col;  
  float random_color_var = random(0, 255);
  
  PShape star; // for custom shape
  
  public Object(float x, float y, float r, int col, String shape) 
  {
    // set values locally to individual objects
    this.x = x;
    this.y = y;
    this.r = r;
    this.col = col;
    this.shape = shape;
  }

  // Object function: move. Allow movement of objects based on mouse control
  public void move() 
  {
    if (dist(mouseX, mouseY, x, y) < r) 
    { // mouse is inside object   
      if (mousePressed) 
      { // if left mouse clicked, move
        x = mouseX;
        y = mouseY;
      } 
    }
    
    if(random_col_bool == false)
    {
      fill(col); // static color fill
    }
    else 
    {
      // random starting color, color cycles through default rainbow gradiant 
      fill(color(random_color_var, 255, 255)); 
      random_color_var++;
      if (random_color_var > 255) 
      {
        random_color_var = 0;
      }
    }
    
    // set shape of object 
    if (shape == "circle")
    {    
      ellipse(x, y, r, r); // object set to cicle
    }    
    else if (shape == "square") 
    {
      rect(x, y, r, r); // object set to square
    }
    else if (shape == "star") 
    {
      // object set to star
      star = createShape();
      star.beginShape();
      star.vertex(x, y-r*7/10);
      star.vertex(x+r*2/10, y-r*3/10);
      star.vertex(x+r*7/10, y-r*2/10);
      star.vertex(x+r*3/10, y+r/10);
      star.vertex(x+r*4/10, y+r*6/10);
      star.vertex(x, y+r*3/10);
      star.vertex(x-r*4/10, y+r*6/10);
      star.vertex(x-r*3/10, y+r/10);
      star.vertex(x-r*7/10, y-r*2/10);
      star.vertex(x-r*2/10, y-r*3/10);
      star.endShape(CLOSE);
      shape(star); // display star
    }
  }  
  
  // Object function: random_move. Boolean toggled to allow objects to move randomly on screen
  public void random_move()
  { 
    x += random(-random_dist, random_dist);
    y += random(-random_dist, random_dist);
        
    if(random_col_bool == false){
      fill(col); // static color fill
    }
    else 
    {
      // random starting color, color cycles through rainbow gradiant 
      fill(color(random_color_var, 255, 255));
      random_color_var++;
      if (random_color_var > 255)
      {
        random_color_var = 0;
      }
    }
    
    // set shape of object 
    if (shape == "circle") 
    {    
      ellipse(x, y, r, r); // object set to cicle
    }
    else if (shape == "square") 
    {
      rect(x, y, r, r); // object set to square
    }
    
    else if (shape == "star") 
    {
      // object set to star
      star = createShape();
      star.beginShape();
      star.vertex(x, y-r*7/10);
      star.vertex(x+r*2/10, y-r*3/10);
      star.vertex(x+r*7/10, y-r*2/10);
      star.vertex(x+r*3/10, y+r/10);
      star.vertex(x+r*4/10, y+r*6/10);
      star.vertex(x, y+r*3/10);
      star.vertex(x-r*4/10, y+r*6/10);
      star.vertex(x-r*3/10, y+r/10);
      star.vertex(x-r*7/10, y-r*2/10);
      star.vertex(x-r*2/10, y-r*3/10);
      star.endShape(CLOSE);
      
      shape(star); // display star
    }
  }
  
  // Object function: col_update. 
  // Updates color of objects on screen by holding any key and clicking
  public void col_update() 
  {
    if (dist(mouseX, mouseY, x, y) < r) 
    { // mouse is inside object   
      if (mousePressed && keyPressed) 
      { // if any key pressed and left mouse clicked
        col = col_global; // update object color to current global color
      }
    }
  }
  
}
boolean isSerif = true;
float SCALE = (1+sqrt(5))/2;  //Golden ratio
float textSize = 0;

// variables to track the space that has been occupied
float xMin = 0;
float xMax = width;
float yMin = 0;
float yMax = height;

float txtX = 0;
float txtY = 0;
float upperBoundX = width;
float upperBoundY = height;

public String generateFont(String fontType)
{
  ////println("Method called: generateFont()");
  String aStr;
  HashSet<String> mySet = new HashSet<String>(Arrays.asList(PFont.list()));
  
  if(fontType.equals("serif"))
  {
    aStr = serifFonts[(int)random(serifFonts.length)];
    float check = random(1);
    //check = random(0.3);
    
    if(check > 0.3f)
    {
      ////println("Nothing");
    }
    else if(check > 0.2f)
    {
      ////println("check bold");
      if(mySet.contains(aStr + " Bold"))
        aStr = aStr + " Bold";
    }
    else if(check > 0.1f)
    {
      ////println("check light");
      if(mySet.contains(aStr + " Light"))
        aStr = aStr + " Light";
    }
    else
    {
      ////println("check italic");
      if(mySet.contains(aStr + " Italic"))
        aStr = aStr + " Italic";
    }
    return aStr;
  }
  else if(fontType.equals("sansserif"))
  {
    isSerif = false;
    
    aStr = sanserifFonts[(int)random(sanserifFonts.length)];
    float check = random(1);
    //check = random(0.3);
    
    if(check > 0.3f)
    {
      ////println("Nothing");
    }
    else if(check > 0.2f)
    {
      ////println("check bold");
      if(mySet.contains(aStr + " Bold"))
        aStr = aStr + " Bold";
    }
    else if(check > 0.1f)
    {
      ////println("check light");
      if(mySet.contains(aStr + " Light"))
        aStr = aStr + " Light";
    }
    else
    {
      ////println("check italic");
      if(mySet.contains(aStr + " Italic"))
        aStr = aStr + " Italic";
    }
    return aStr;
  }
  return null;
}


public void drawHeaders()
{
  fill(headerColor);
  String fontStr;
  
  if(random(1) > 0.5f)
    fontStr = generateFont("serif");
  else
    fontStr = generateFont("sansserif");
    
  if(!randomFont)  
    textFont(createFont("Catamaran-Light.ttf", 1));
  //println("fontStr = " + fontStr);
  
  // start from 4th row in the input file
  for(int i = 3; i < input.length; i++)
  {
    //String[] words = input[i].split("\\s+");
    //float textSize;

    if(i == 3) //4th row is a sub-title
    {
      textSize = gridY / SCALE; // size becomes 1/1.618 of previous items
      textSize(textSize);  

      boolean vert = random(1) > 0.5f;
      //vert = true;
      if(vert)
      {
        vertHeader(input[i], true, false);
      }
      else
      {
        horizontalHeader(input[i], true, false);
      }
    }
    else // other rows are description paragrahs
    {
      textSize = gridY / (SCALE * 2);
      
      if(!randomFont)
        textFont(createFont("Catamaran-Medium.ttf", 1));
      
      textSize(textSize);

      boolean vert = random(1) > 0.5f;
      //vert=true;
      if(vert)
      {
        vertParagragh(i, false);
      }
      else
      {
        horizontalHeader(input[i], false, false);
      }     
    } 
  }
}


public void drawTitle()
{
  /*float minRGB = Math.min(redBackground, Math.min(greenBackground, blueBackground));
  float maxRGB = Math.max(redBackground, Math.max(greenBackground, blueBackground));
  fill(maxRGB + minRGB - redBackground, maxRGB + minRGB - greenBackground, maxRGB + minRGB - blueBackground);
  */
  fill(titleColor);
  
  String fontStr = "";
  
  if(isSerif == true && random(1) > 0.5f)
    fontStr = generateFont("sansserif");
  else
    fontStr = generateFont("serif");
  //println(fontStr);
  PFont fontOfStr;
  
  float fontBig = random(33, 48);
  
  {
    boolean vert = random(1) > 0.5f;
    //vert = true;
    if(vert)
    {
      fontOfStr = createFont(fontStr, gridX);
      if(!randomFont)
        fontOfStr = createFont("Catamaran-Bold.ttf", gridX);
      textFont(fontOfStr);
      vertTitle(fontOfStr);
    }
    else
    {
      fontOfStr = createFont(fontStr, gridY);
      if(!randomFont)
        fontOfStr = createFont("Catamaran-Bold.ttf", gridY);
      textFont(fontOfStr);
      horizontalTitle(fontOfStr);
    }
  }
  ////println(fontStr);
}

public float getLongestWordLength(String[] words)
{
  float wordLength = 0;
  for(int i=0; i < words.length; i++)
  {
    if(textWidth(words[i]) > wordLength)
       wordLength = textWidth(words[i]);
  }
  
  return wordLength;
}

// This is for horizontal
// not capitalized = is a paragraph
// wraparound = biggest title
public void setValidTextPostionLowerBound(boolean capitalized, float longestWordLength, float stringLength, boolean isTitle)
{
  //println("\n Entering setValidTextPostionLowerBound()");  
  float lengthToTest = 0;
  
  if(!capitalized)
    lengthToTest = longestWordLength * 3;
  else
    lengthToTest = stringLength;
  
  // try 1000 times
  for(int k = 0; k< 2000; k++)
  {
    txtX = random(width * 0.8f);
    txtY = random(height *0.8f);

    if(capitalized)
    {
      if(isTitle && (txtX + stringLength * 0.6f  < width) && (txtY + 3 * textSize )< height)
          break;
      else
      {
        if((txtX + lengthToTest < xMin || (txtX > xMax && txtX + lengthToTest < width)) 
          && (txtY + textSize < yMin || (txtY > yMax && txtY + 4*textSize < height)))
        {
          break;
        }
      }
    } 
    else  // give less randomness if it's a paragraph
    {
      if(txtY < yMin)
      {
        if((width - txtX) * (yMin - txtY) >= 16*610)
          break;
      }
      if(txtY > yMax)
      {
        if((width -txtX) * (height - txtY) >= 16*610)
          break;
      }
        
      if(txtY > yMin && txtY < yMax && txtX < xMin)
      {
        txtX = random(5);
        if((height - txtY) * (xMin - txtX) >= 16*610)
          break;
      }
       if(txtY > yMin && txtY < yMax && txtX > xMax)
      {
        txtX = random(5) + xMax;
        if((width-txtX) * (height - txtY) >= 16*610)
          break;
      }
    }
    
    ////println("Before last try the lower bound: txtX= "+ txtX + " txtY = " + txtY);
    if(k == 999)  //still not find good values, set them to near the bottom
    {
      //println("LAST RESORT to get lower bound in setValidTextPostionLowerBound");
      txtX = 1;
      txtY = height-50;
      break;
    }
  }
  
  //println("Set the lower bound: txtX= "+ txtX + " txtY = " + txtY);
}


public void setValidTextPostionVerticalOrigin(boolean capitalized, float longestWordLength, float stringLength)
{
  //println("\n Entering setValidTextPostionVerticalOrigin");
  float lengthToTest = 0;
  
  if(!capitalized)
    lengthToTest = longestWordLength * 3;
  else
    lengthToTest = stringLength;
  
  // try 1000 times
  for(int k = 0; k< 2000; k++)
  {
    txtX = random(3*textSize, width);
    txtY = random(longestWordLength, height/2);

    if(capitalized)
    {
      if((txtX + lengthToTest < xMin || (txtX > xMax && txtX + lengthToTest < width)) 
        && (txtY + textSize < yMin || (txtY > yMax && txtY + 4*textSize < height)))
      {
        break;
      }
    } 
    else  // give less randomness if it's a paragraph
    {
      if(txtX < xMin)
      {
        if(txtX * (height - txtY) >= 16*610)
          break;
      }
      if(txtX > xMax)
      {
        if((txtX - xMax) * (height - txtY) >= 16*610)
          break;
      }
        
      if(txtX > xMin && txtX < xMax && txtY < yMin)
      {
        txtY = random(5);
        if(txtX * (yMin - txtY) >= 16*610)
          break;
      }
      if(txtX > xMin && txtX < xMax && txtY > yMax)
      {
        txtY = random(5) + yMax;
        if(txtX * (height - txtY) >= 16*610)
          break;
      }
    }
    
    ////println("Before last try the lower bound: txtX= "+ txtX + " txtY = " + txtY);
    if(k == 999)  //still not find good values, set them to near origin
    {
      //println("LAST RESORT to get lower bound in setValidTextPostionVerticalOrigin");
      
      txtY = 1;
      txtX = width;
        
      break;
    }
  }
  
  //println("Set the vertical lower bound: txtX= "+ txtX + " txtY = " + txtY);
}

public void setUpperBoundValues()
{
  if(txtY < yMin)
    {
      if(txtX < xMin)
      {
        upperBoundX = xMin;
        upperBoundY = height;
      }
      if(txtX > xMin && txtX < xMax)
      {
        upperBoundY = yMin;
        upperBoundX = width;
      }
      if(txtX > xMax)
      {
        upperBoundY = height;
        upperBoundX = width;
      }
    }
    if(txtY > yMax)
    {
      upperBoundY = height;
      upperBoundX = width;
    }
    
    if(txtY > yMin && txtY < yMax && txtX < xMin)
    {
      upperBoundY = height;
      upperBoundX = xMin;
    }
    if(txtY > yMin && txtY < yMax && txtX > xMax)
    {
      upperBoundY = height;
      upperBoundX = width;
    }
}

public void setValidTextPostionUpperBound(boolean capitalized)
{
  //println("\n Entering setValidTextPostionUpperBound()");  
  
  if(capitalized)
  {
    setUpperBoundValues();
  }
  else // for paragraph: upperbound is the width and length of the ractangle, NOT the coordinates of an endpoint
  {
    for(int k=0; k < 1000; k++)
    {         
      if(txtY < yMin)
      {
        upperBoundX = random(width - txtX);
        upperBoundY = random(yMin - txtY);

      }
      if(txtY > yMax)
      {
        upperBoundX = random(width - txtX);
        upperBoundY = random(height - txtY);
      }
        
      if(txtY > yMin && txtY < yMax && txtX < xMin)
      {
        upperBoundX = random(xMin - txtX);
        upperBoundY = random(height - txtY);
      }
      if(txtY > yMin && txtY < yMax && txtX > xMax)
      {
        upperBoundX = random(width - txtX);
        upperBoundY = random(height - txtY);
      }
      
      if(upperBoundX * upperBoundY >= 16*610)
        break;
        
      if (k == 999)
      {
        upperBoundX = width;
        upperBoundY = height;
        //println("LAST RESORT to get upper bound in setValidTextPostionUpperBoun");
        break;
      }
    }
  }
  //println("Set the upper bound: upperBoundX= "+ upperBoundX + " upperBoundY = " + upperBoundY);
}

public void setValidTextPostionVerticalUpperBound(boolean capitalized)
{
  //println("\n Entering setValidTextPostionVerticalUpperBound()");  
  
  if(capitalized)
  {
    setUpperBoundValues();
  }
  else // for paragraph: upperbound is the width and length of the ractangle, NOT the coordinates of an endpoint
  {
    for(int k=0; k < 2000; k++)
    {         
      if(txtX < xMin)
      {
        upperBoundX = random(txtX);
        upperBoundY = random(height - txtY);

      }
      if(txtX > xMax)
      {
        upperBoundX = random(txtX - xMax);
        upperBoundY = random(height - txtY);
      }
        
      if(txtX > xMin && txtX < xMax && txtY < yMin)
      {
        upperBoundX = random(txtX);
        upperBoundY = random(yMin - txtY);
      }
      if(txtX > xMin && txtX < xMax && txtY > yMax)
      {
        upperBoundX = random(txtX);
        upperBoundY = random(height - txtY);
      }
      
      if(upperBoundX * upperBoundY >= 16*610)
        break;
        
      if (k == 999)
      {
        upperBoundX = width;
        upperBoundY = height;
        //println("LAST RESORT to get upper bound in setValidTextPostionVerticalUpperBound()");
        break;
      }
    }
  }
  //println("Set the upper bound: upperBoundX= "+ upperBoundX + " upperBoundY = " + upperBoundY);
}

//isTitle is biggest title
public void vertHeader(String s, boolean capitalized, boolean isTitle)
{
  if(capitalized || isTitle) s = s.toUpperCase();    
  
  //println("\nEntering vertHeader, s = " + s + "; textSize = " +textSize);
  //println("width = "+width); //println("height = "+height);
  String[] words = s.split("\\s+");
  
  setValidTextPostionVerticalOrigin(capitalized, getLongestWordLength(words), textWidth(s));
  setValidTextPostionVerticalUpperBound(capitalized);

  ////println("textSize = " + textSize);
  //println("Before pushMatrix txtX = "  + txtX + ", txtY = " + txtY);
  pushMatrix();
  translate(txtX, txtY);
  rotate(PI/2);
  ////println("after pushMatrix txtX = "  + txtX + ", txtY = " + txtY);
  
  if(capitalized)
  {
    if(isTitle)
      text(s.toUpperCase(), 0, 0, textWidth(s)*0.6f, 2.5f*textSize);
    else
      //text(s.toUpperCase(), 0, 0, textWidth(s), 4*textSize);
      text(s.toUpperCase(), 0, 0);
  }
  else
    text(s, 0, 0, width-txtX, height-txtY);

  popMatrix();
    ////println("After popMatrix txtX = "  + txtX + ", txtY = " + txtY);
    
  xMax = txtX + textSize;
  yMin = txtY;

  if(!isTitle)
  {
    if(txtY + textWidth(s) > height) // wrap around
    {
      xMin = txtX - 2.5f*textSize;; 
      yMax = txtY + getLongestWordLength(words);
    }
    else
    {
      xMin = txtX;
      yMax = txtY + textWidth(s);
    }
  }
  else
  {
    xMin = txtX - 2*textSize;
    yMax = txtY + 3*getLongestWordLength(words);
  }
  
  //println("textWidth(input[i2]= "+textWidth(s));
  //println("xMin = "  + xMin + " xMax = " + xMax);
  //println("yMin = "  + yMin + " yMax = " + yMax);
  //ellipse(xMin, yMin, 5, 5);
  //ellipse(xMax, yMax, 5, 5);
}

public void vertParagragh(int i2, boolean capitalized)
{
  String s =  input[i2];
  if(capitalized) s = s.toUpperCase();
  
  //println("\nEntering vertParagragh");
  //println("width = "+width); //println("height = "+height);

  String[] words = s.split("\\s+");

  setValidTextPostionVerticalOrigin(false, getLongestWordLength(words), textWidth(s));
  setValidTextPostionVerticalUpperBound(false);

  //println("Before pushMatrix txtX = "  + txtX + ", txtY = " + txtY);
  pushMatrix();
  translate(txtX, txtY);
  rotate(PI/2);
  if(capitalized)
    text(s.toUpperCase(), 0, 0, upperBoundX, upperBoundY);
  else
    text(s, 0, 0, upperBoundX, upperBoundY);
    //text(s, 0, 0,  width-txtX, height-txtY);
  popMatrix();
  
  xMax = txtX;
  yMin = txtY;
  if(txtY + textWidth(s) > height) // wrap around
  {
    xMin = max(0, txtX - 10*textSize); 
    yMax = txtY + 5*getLongestWordLength(words);
  }
  else
  {
    xMin = txtX - 2.5f*textSize;
    yMax = txtY + textWidth(s);
  }
  
  //println("textWidth(input[i2]= "+textWidth(s));
  //println("xMin = "  + xMin + " xMax = " + xMax);
  //println("yMin = "  + yMin + " yMax = " + yMax);
  //ellipse(xMin, yMin, 5, 5);
  //ellipse(xMax, yMax, 5, 5);  
}

public void horizontalHeader(String s, boolean capitalized, boolean isTitle)
{
  if(capitalized || isTitle) s = s.toUpperCase();
  //println("\nEntering horizontalHeader, s = " + s);
  //println("width = "+width); //println("height = "+height);

  String[] words = s.split("\\s+");

  setValidTextPostionLowerBound(capitalized, getLongestWordLength(words), textWidth(s), isTitle);
  setValidTextPostionUpperBound(capitalized);

  if(capitalized)
  {
     if(isTitle)  //for big title
      text(s.toUpperCase(), txtX, txtY,  textWidth(s)*0.6f, 2.5f*textSize);
    else
      text(s.toUpperCase(), txtX, txtY,  upperBoundX, upperBoundY);
  }
  else  //paragraph
    text(s, txtX, txtY, upperBoundX, upperBoundY);

  xMin = txtX;
  yMin = txtY;
  if(isTitle) // biggest title
  {
      xMax = txtX + 3*getLongestWordLength(words);
      yMax = txtY + 2*textSize;
  }
  else
  {
    if(txtX + textWidth(s) > upperBoundX) // paragraph wrap around
    {
      xMax = txtX + 5*getLongestWordLength(words);
      yMax = txtY + 6*textSize;
    }
    else
    {
      //println("not wrap around");
      xMax = txtX + textWidth(s);
      yMax = txtY + textSize;
    }
  }
  
  //println("textWidth(input[i2]= "+textWidth(s));
  //println("xMin = "  + xMin + " xMax = " + xMax);
  //println("yMin = "  + yMin + " yMax = " + yMax);
  //ellipse(xMin, yMin, 5, 5);
  //ellipse(xMax, yMax, 5, 5);
}

public void vertTitle(PFont fontOfStr)
{
  //println("\n==>Entering vertTitle ");
  //println("width = "+width); //println("height = "+height);
  String[] words = input[2].split("\\s+");
  int factor = 1;
  int rand1 = (int)random(gridNumX - words.length * factor - factor);
  ////println(rand1);
  int spaceY = (int)gridNumY / 4;
  boolean withShape = random(1) > 0.5f;
  
  withShape = false;
  if(!withShape){
      textSize = gridY*1.5f ;
      vertHeader(input[2], true, true);
  }
  else
  {
    int numVertex = (int) random(3, 6);
    //println("textsize =" +textSize);
    for(int i = 0; i < words.length; i++)
    {
      float rand2 = (int)random(gridNumY-spaceY);
      txtX = ((int)(gridNumX-rand1)) * (gridX + betweenX);
      if(txtX > width) 
        txtX = width - random(80, 100);
      txtY = rand2 * (gridY + betweenY);
      if (txtY > height)
        txtY = height - random(80, 100);
      //println("txtX = " + txtX + " txtY = " +txtY);
    
      pushMatrix();
      translate(txtX, txtY);
      rotate(PI/2);
      textWithShapes(fontOfStr, words[i].toUpperCase(), 0, 0, numVertex);
      popMatrix();
      rand1 += (int)random(gridNumX - rand1 - 1 -(words.length - i) * factor)+ factor;
    } 
  }
}


public void horizontalTitle(PFont fontOfStr)
{
  //println("\n==> Entering horizontalTitle()");
  String[] words = input[2].split("\\s+");
  boolean withShape = random(1) > 0.5f;
  withShape = false;
  if(!withShape){
      textSize = gridY*1.5f ;
      horizontalHeader(input[2], true, true);
  }
  else
  {
    int rand2 = (int)random(gridNumY - words.length  - 1) + 1;
    int spaceX = (int)gridNumX / 5;

    int numVertex = (int) random(4, 11);
    for(int i = 0; i < words.length; i++)
    {
      float rand1 = (int)random(gridNumX-spaceX);
      txtX = rand1 * (gridX + betweenX);
      txtY = rand2 * (gridY + betweenY);
    
      if(txtX > width) 
        txtX = width - random(80, 100);
      if (txtY > height)
        txtY = height - random(80, 100);
        
      textWithShapes(fontOfStr, words[i].toUpperCase(), txtX, txtY, numVertex);

      rand2 += (int)random(gridNumY - rand2 - (words.length - i)) + 1;
    }
  }
}

public void textWithShapes(PFont fontOfStr, String str, float xStart, float yStart, int numVertex)
{
  //println("\n==> Entering textWithShape(): xStart = " +xStart + " yStart = " +yStart);
  float offSetX = 0;
  float offSetY = 0;
  textFont(fontOfStr);
  
  for(int i = 0; i < str.length(); i++)
  {
    float startDegree = random(0, 2 * PI);
    float interval = random(10, 50);
    interval = 70;
    
    if(random(1) > 0.9f)
    {
      offSetX = random(5, 100);
      offSetY = random(5, 100);
    }
    offSetX = 0;
    offSetY = 0;
    String curStr = Character.toString(str.charAt(i));
    float diameter;
    float centerX;
    float centerY;
    
    if(lowerCaseDescent.contains(str.charAt(i)))
    {
      diameter = fontOfStr.ascent() * fontOfStr.getSize();
      centerX = textWidth(curStr) / 2.0f + xStart;
      // yPos - f.ascent()*fontSize
      centerY =yStart + fontOfStr.descent() * fontOfStr.getSize() - diameter / 2.0f;
      line(0, yStart + fontOfStr.descent() * fontOfStr.getSize(), width, yStart + fontOfStr.descent() * fontOfStr.getSize());
    }
    else if(lowerCaseNorm.contains(str.charAt(i)))
    {
      diameter = (fontOfStr.ascent() - fontOfStr.descent()) * fontOfStr.getSize();
      centerX = textWidth(curStr) / 2.0f + xStart;
      centerY = yStart - diameter / 2.0f;
    }
    else
    {
      diameter = fontOfStr.ascent()* fontOfStr.getSize();
      centerX = textWidth(curStr) / 2.0f + xStart;
      centerY = yStart - diameter / 2.0f;
    }
    
    //println("centerX = " + centerX + " centerY =  "+ centerY);
    //ellipse(centerX, centerY, diameter, diameter);
    //rect(centerX + offSet - diameter / 2.0, centerY + offSet - diameter / 2.0, diameter, diameter);
    float radius = diameter / 2.0f;
    float minRGB = Math.min(redBackground, Math.min(greenBackground, blueBackground));
    float maxRGB = Math.max(redBackground, Math.max(greenBackground, blueBackground));
    float tmpFactR = (redBackground / 255 + (maxRGB + minRGB - redBackground) / 255) / 2;
    float tmpFactG = (greenBackground / 255 + (maxRGB + minRGB - greenBackground) / 255) / 2;
    float tmpFactB = (blueBackground / 255 + (maxRGB + minRGB - blueBackground) / 255) / 2;
    
    noStroke();
    //fill(maxRGB + minRGB - redBackground * tmpFactR, maxRGB + minRGB - greenBackground * tmpFactG, maxRGB + minRGB - blueBackground * tmpFactB);
    fill(titleBackColor);
    //fill(129, 177, 213);
    
    if(!curStr.equals(" "))
      drawShapeBasedOnRadius(numVertex, startDegree, diameter, centerX + offSetX, centerY + offSetY);
    stroke(10);
    fill(titleColor);
    //fill(61, 96, 167);
    text(curStr, xStart, yStart);
    xStart += diameter + interval;
  }
}


public void drawShapeBasedOnRadius(int numVertex, float startDegree, float radius, float xStart, float yStart)
{
      PShape polygon = createShape();
      polygon.beginShape();
      float degree = startDegree;
      float degreeIncrement = 2 * PI / numVertex;
      
      for(int i = 0; i < numVertex; i++)
      {
        float randomSpike = 1;
        if(random(1) > 0.7f)
          randomSpike = random(1, 1.5f);
        float xPoint = (float)Math.cos(degree) * radius * randomSpike + xStart;
        float yPoint = (float)Math.sin(degree) * radius * randomSpike + yStart;
        polygon.vertex(xPoint, yPoint);
        degree += degreeIncrement;
      }
      polygon.endShape(CLOSE);
      shape(polygon); 
}
// used to record the boundary of Title and Author
ArrayList<ImageCoor> TextBoundaryCoordinates = new ArrayList<ImageCoor>();

public void drawHeadersByRules()
{
  fill(headerColor);
  String fontStr;
  
  if(random(1) > 0.5f)
    fontStr = generateFont("serif");
  else
    fontStr = generateFont("sansserif");
    
  if(!randomFont)  
    textFont(createFont("Catamaran-Light.ttf", 1));
  //println("fontStr = " + fontStr);
  
  // start from 4th row in the input file
  for(int i = 3; i < input.length; i++)
  {
    //String[] words = input[i].split("\\s+");

    if(i == 3) //4th row is a sub-title
    {
      textSize = gridY / SCALE; // size becomes 1/1.618 of previous items
      textSize(textSize);  

      boolean vert = random(1) > 0.5f;
      //vert = false; //Ian tmp
      if(vert)
      {
        vertHeaderByRules(input[i], false);  //Ian tmp
      }
      else
      {
        horizontalHeaderByRules(input[i], false);
      }
    }
    else // other rows are description paragrahs and just make sure no collision
    {
      textSize = gridY / (SCALE * 2);
      
      if(!randomFont)
        textFont(createFont("Catamaran-Medium.ttf", 1));
      
      textSize(textSize);

      boolean vert = random(1) > 0.5f;
      //vert=true;  //Ian tmp
      if(vert)
      {
        vertParagraghByRules(input[i]); //Ian tmp
      }
      else
      {
        horizontalParagraphByRules(input[i]); //Ian tmp
      }     
    } 
  }
}


public void drawTitleByRules()
{
  fill(titleColor);
  
  String fontStr = "";
  
  if(isSerif == true && random(1) > 0.5f)
    fontStr = generateFont("sansserif");
  else
    fontStr = generateFont("serif");
  //println(fontStr);
  PFont fontOfStr;
  
  float fontBig = random(33, 48);

  boolean vert = random(1) > 0.5f;
    //vert = false;  //Ian tmp
  if(vert)
  {
    fontOfStr = createFont(fontStr, gridX);
    if(!randomFont)
      fontOfStr = createFont("Catamaran-Bold.ttf", gridX);
    textFont(fontOfStr);
    vertTitleByRules(fontOfStr);
  }
  else
  {
    fontOfStr = createFont(fontStr, gridY);
    if(!randomFont)
      fontOfStr = createFont("Catamaran-Bold.ttf", gridY);
    textFont(fontOfStr);
    horizontalTitleByRules(fontOfStr);
  }
}


// This is for horizontal
// not capitalized = is a paragraph
public void setValidTextPostionLowerBoundByRules(boolean capitalized, float longestWordLength, float stringLength, boolean isTitle)
{
  //println("\n Entering setValidTextPostionLowerBoundByRules(): longestWordLength =" +longestWordLength + " stringLength="+ stringLength + " textSize=" +textSize);  
  float lengthToTest = 0;
  
  if(!capitalized)
    lengthToTest = longestWordLength * 8;
  else if(isTitle)
    lengthToTest = stringLength/2;
  else
    lengthToTest = stringLength;
  
  //println("lengthToTest = "+lengthToTest);
  boolean occupied = false;
  //for golden triangle, paragrah cannot be put around pivatol points
  while(!occupied && ((CurrentRule == Rules.GOLDENTRIANGLE && capitalized) || CurrentRule != Rules.GOLDENTRIANGLE ))
  {
    int i = (int)random(CurrentCoordinates.GetCoordinates().size());
    Coordinate coordinate = CurrentCoordinates.GetCoordinateByIndex(i);
    //println("i= "+i);
    //println("coordinate.x= "+ coordinate.x+ " coordinate.y = " +coordinate.y + " isOcucupiedByText = " +coordinate.isOccupiedByText);
    
    if(coordinate.GetIsOccupiedByText() == false)
    {
      coordinate.SetIsOccupiedByText(true);
      occupied = true;
      if(isTitle)
        txtY = coordinate.y - textSize;  // try to spread out
      else
        txtY = coordinate.y;
      
      // try to evenly spread the text around pivatol point
      txtX = (coordinate.x - lengthToTest/2) > 0 ? (coordinate.x - lengthToTest/2) : 0;
      break;
    }
    else
      continue;
  }
  
  // try 2000 times
  // if using pivatoal points doesn't work, random generate some values
 // //println("xMin =" + xMin +" xMax =" +xMax + " yMin= " +yMin + " yMax="+yMax);
  boolean isLastTry = false;
  for(int k = 0; k< 2000; k++)
  {
    if(occupied == false)  // not use pivatol point so init txtX and txtY
    {
      //println("occupied ="+ occupied);
      txtX = random(1, width * 0.8f);   // Y is fixed but still need an init value
      txtY = random(textSize, height * 0.8f);
      occupied = true;
    }
    
    //if(k<50)
      ////println("setValidTextPostionLowerBoundByRules(): txtX =" + txtX +" txtY =" +txtY );

    if(isTitle)
    {
      if(txtX + stringLength * 0.6f  < width)
          break;
    } 
    else if(capitalized) // Author
    {
      boolean isCollide = checkCollide(TextBoundaryCoordinates, txtX, txtY, txtX + lengthToTest, txtY + textSize);
      if(txtX + lengthToTest < width && !isCollide) 
      {
        upperBoundX = txtX + lengthToTest;
        upperBoundY = txtY + textSize;
        //println("Author no collide! txtX =" + txtX +" txtY =" +txtY + " upperBoundX=" +upperBoundX + " upperBoundY=" +upperBoundY);
        break;
      }
    }
    else  // give less randomness if it's a paragraph
    {
      boolean isCollide = checkCollide(TextBoundaryCoordinates, txtX, txtY, txtX + lengthToTest, txtY + 6*textSize);
      if(!isCollide)
      {
        if(txtX + lengthToTest < width && txtY + 6*textSize < height)
        {
          upperBoundX = txtX + lengthToTest;
          upperBoundY = txtY + 6*textSize;
          //println("Paragragn no collide! txtX =" + txtX +" txtY =" +txtY + " upperBoundX=" +upperBoundX + " upperBoundY=" +upperBoundY);
          break;
        }
      }
    }
    
    if(isLastTry)
      txtY = random(textSize, height * 0.8f); 
    txtX = random(1, width * 0.8f);   // Y is fixed 
    ////println("generate a new txtX= "+ txtX + " txtY = " + txtY);
     
    ////println("Before last try the lower bound: txtX= "+ txtX + " txtY = " + txtY);
    if(k == 1999)
    {
      if(!isLastTry)  //still not find good values, set them to near origin
      {
        //println("HAVE TO Change Y value to get lower bound in setValidTextPostionLowerBoundByRules");
        txtY = random(1, height * 0.8f);  // have to change Y value 
        k = 0; //reset k to start looping again
        isLastTry = true;
        continue;
      }
      else
      {
        //println("LAST RESORT to get lower bound in setValidTextPostionLowerBoundByRules");
        txtX = 1;
        txtY = height -50;
        break;
      }
    }
  }
  
  //println("Set the lower bound: txtX= "+ txtX + " txtY = " + txtY);
}


public void setValidTextPostionVerticalOriginByRules(boolean capitalized, float longestWordLength, float stringLength, boolean isTitle)
{
  //println("\n Entering setValidTextPostionVerticalOriginByRules: longestWordLength =" +longestWordLength + " stringLength="+ stringLength + " textSize=" +textSize);  
  float lengthToTest = 0;
  if(!capitalized)
    lengthToTest = longestWordLength * 8;
  else if(isTitle)
    lengthToTest = stringLength/2;
  else
    lengthToTest = stringLength;
    
  //println("lengthToTest = "+lengthToTest);
  boolean occupied = false;
  //for golden triangle, paragrah cannot be put around pivatol points
  while(!occupied && ((CurrentRule == Rules.GOLDENTRIANGLE && capitalized) || CurrentRule != Rules.GOLDENTRIANGLE ))
  {
    int i = (int)random(CurrentCoordinates.GetCoordinates().size());
    //println("i = " +i + " size = " +CurrentCoordinates.GetCoordinates().size());
    Coordinate coordinate = CurrentCoordinates.GetCoordinateByIndex(i);
    
    if(coordinate.GetIsOccupiedByText() == false)
    {
      coordinate.SetIsOccupiedByText(true);
      occupied = true;
      if(isTitle)
        txtX = (coordinate.x + 2.5f*textSize) < width ? coordinate.x : width - 2.5f*textSize;  // try to spread out
      else
        txtX = (coordinate.x + textSize) < width ? coordinate.x : width - textSize;
      
      // try to evenly spread the text around pivatol point
      txtY = (coordinate.y - lengthToTest/2) > 0 ? (coordinate.y - lengthToTest/2) : 0;
      break;
    }
    else
      continue;
  }
  
  // try 2000 times
  // if using pivatoal points doesn't work, random generate some values
  boolean isLastTry = false;
  for(int k = 0; k< 2000; k++)
  {
    if(occupied == false)  // not use pivatol point so init txtX and txtY
    {
      //println("occupied ="+ occupied);
      txtX = random(1, width-6*textSize);   // Y is fixed but still need an init value
      txtY = random(1, height - lengthToTest);
      occupied = true;
    }
    
    //if(k<50)
     // //println("setValidTextPostionVerticalOriginByRules(): txtX =" + txtX +" txtY =" +txtY );

    if(isTitle)
    {
      if(txtY + stringLength * 0.6f  < height)
          break;
    } 
    else if(capitalized) // Author
    {
      boolean isCollide = checkCollide(TextBoundaryCoordinates, txtX - textSize, txtY, txtX, txtY + lengthToTest);
      if(txtY + lengthToTest < height && !isCollide && txtX + textSize < width) 
      {
        upperBoundX = txtX + textSize;
        upperBoundY = txtY + lengthToTest;
        //txtX = txtX - textSize;
        //println("Author no collide! txtX =" + txtX +" txtY =" +txtY + " upperBoundX=" +upperBoundX + " upperBoundY=" +upperBoundY);
        break;
      }
    }
    else  // give less randomness if it's a paragraph
    {
      boolean isCollide = checkCollide(TextBoundaryCoordinates,  txtX- 6*textSize, txtY, txtX, txtY + lengthToTest);
      if(!isCollide)
      {
        if(txtY + lengthToTest < height && txtX - 6*textSize > 0)
        {
          upperBoundX = txtX;
          upperBoundY = txtY + lengthToTest;
          //txtX = txtX - 6*textSize;
          //println("Paragragn no collide! txtX =" + txtX +" txtY =" +txtY + " upperBoundX=" +upperBoundX + " upperBoundY=" +upperBoundY);
          break;
        }
      }
    }
    
    if(isLastTry)
    {
      if(!capitalized)  //Author
        txtX = random(1, width-textSize);
      else
        txtX = random(1, width-6*textSize);   // X is fixed 
    }
    txtY = random(1, height - lengthToTest); 
    
    ////println("generate a new txtY= "+ txtY + " txtX = " + txtX);
     
    if(k == 1999)
    {
      if(!isLastTry)  //still not find good values, set them to near origin
      {
        //println("HAVE TO Change X value to get lower bound in setValidTextPostionVerticalOriginByRules");
        if(!capitalized)  //Author
          txtX = random(1, width-textSize);
        else
          txtX = random(1, width-6*textSize);  // have to change  value 
        k = 0; //reset k to start looping again
        isLastTry = true;
        continue;
      }
      else
      {
        //println("LAST RESORT to get lower bound in setValidTextPostionVerticalOriginByRules");
        txtX = width-6*textSize;
        txtY = height - lengthToTest;
        break;
      }
    }
  }
  
  //println("Set the vertical lower bound: txtX= "+ txtX + " txtY = " + txtY);
}

//Used by Title and Author
public void vertHeaderByRules(String s, boolean isTitle)
{
  s = s.toUpperCase();    
  
  //println("\nEntering vertHeader, s = " + s + "; textSize = " +textSize);
  //println("width = "+width); //println("height = "+height);
  String[] words = s.split("\\s+");
  
  setValidTextPostionVerticalOriginByRules(true, getLongestWordLength(words), textWidth(s), isTitle);
  
  ////println("textSize = " + textSize);
  //println("Before pushMatrix txtX = "  + txtX + ", txtY = " + txtY);
  if(isTitle && txtX < 190) // for vertical title, make sure the txtX > 190
    txtX = 190;
    
  pushMatrix();
  translate(txtX, txtY);
  rotate(PI/2);
  ////println("after pushMatrix txtX = "  + txtX + ", txtY = " + txtY);
  
  noStroke(); //Ian tmp
  line(-txtY, 0, height-txtY, 0);
  textAlign(BOTTOM);
    
  if(isTitle)
  {
    stroke(titleColor);
    //println("I am printing title: txtX = " +txtX+" txtY = " +txtY +" textWidth(s)*0.6=" +textWidth(s)*0.6+" 2.5*textSize =" +2.5*textSize);
    text(s.toUpperCase(), 0, 0, textWidth(s)*0.6f, 2.5f*textSize);
    ImageCoor titleCoor = new ImageCoor(txtX - 2.5f*textSize, txtY, txtX,  txtY + textWidth(s)*0.6f);
    TextBoundaryCoordinates.add(titleCoor);
  }
  else //Author
  {
    //text(s.toUpperCase(), 0, 0, textWidth(s), 4*textSize);
    //println("I am printing author: txtX = " +txtX+" txtY = " +txtY +" textWidth(s)=" +textWidth(s)+" textSize =" +textSize);
    stroke(headerColor);
    text(s.toUpperCase(), 0, 0);
    ImageCoor authorCoor = new ImageCoor(txtX, txtY, txtX+textSize, txtY + textWidth(s));
    TextBoundaryCoordinates.add(authorCoor);
  }

  popMatrix();
    ////println("After popMatrix txtX = "  + txtX + ", txtY = " + txtY);
    
  //println("textWidth = "+textWidth(s));
  for(int i = 0; i < TextBoundaryCoordinates.size(); i++)
  {
    ImageCoor co = TextBoundaryCoordinates.get(i);
    //println("co.x1 = "  + co.x1 + " co.x2 = " + co.x2);
    //println("co.y1 = "  + co.y1+ " co.y2 = " + co.y2);
    //ellipse(co.x1, co.y1, 5, 5);
    //ellipse(co.x2, co.y2, 5, 5);
  }
  
      //Ian tmp line
      /*
      if(isTitle)
      {
    stroke(0);
    line(txtX, 0, txtX, height);
    line(0, txtY, width, txtY);
      }*/
}

public void vertParagraghByRules(String s)
{
  //println("\nEntering vertParagraghByRules, s = " + s);
  //println("width = "+width); //println("height = "+height);

  String[] words = s.split("\\s+");

  setValidTextPostionVerticalOriginByRules(false, getLongestWordLength(words), textWidth(s), false);

  //println("Before pushMatrix txtX = "  + txtX + ", txtY = " + txtY);
  pushMatrix();
  translate(txtX, txtY);
  rotate(PI/2);
  
  noStroke(); //Ian tmp
  line(-txtY, 0, height-txtY, 0);
  textAlign(BOTTOM);
  
  //println("I am printing paragragh: txtX = " +txtX+" txtY = " +txtY +" upperBoundX=" +upperBoundX+" 6*textSize =" + 6*textSize);
  text(s, 0, 0, 8*getLongestWordLength(words), 6*textSize);

  popMatrix();
  
  xMax = txtX;
  yMin = txtY;
  xMin = max(0, txtX - 6*textSize); 
  yMax = txtY + 8*getLongestWordLength(words); 

  //println("textWidth "+textWidth(s));
  //println("xMin = "  + xMin + " xMax = " + xMax);
  //println("yMin = "  + yMin + " yMax = " + yMax);
  //ellipse(xMin, yMin, 5, 5);
  //ellipse(xMax, yMax, 5, 5);  
}

//Used by Title and Author
public void horizontalHeaderByRules(String s, boolean isTitle)
{
  s = s.toUpperCase();
  //println("\n==>Entering horizontalHeaderByRules, s = " + s);
  //println("width = "+width); //println("height = "+height);

  String[] words = s.split("\\s+");

  setValidTextPostionLowerBoundByRules(true, getLongestWordLength(words), textWidth(s), isTitle);

  noStroke();  // Ian tmp
  line(0, txtY, width, txtY);
  textAlign(BOTTOM);
  if(height-txtY < 160) // for horizontal title, make sure the txtY > 160
    txtY = height - 160;
    
  if(isTitle)  //for big title
  {
    stroke(titleColor);
    //println("I am printing title: txtX = " +txtX+" txtY = " +txtY +" textWidth(s)*0.6=" +textWidth(s)*0.6+" 2.5*textSize =" +2.5*textSize);
    text(s.toUpperCase(), txtX, txtY,  textWidth(s)*0.6f, 2.5f*textSize);
    ImageCoor titleCoor = new ImageCoor(txtX, txtY, txtX + textWidth(s)*0.6f, txtY+2.5f*textSize);
    TextBoundaryCoordinates.add(titleCoor);
  }
  else // Author
  {
    //println("I am printing author: txtX = " +txtX+" txtY = " +txtY +" textWidth(s)=" +textWidth(s)+" textSize =" +textSize);
    stroke(headerColor);
    text(s.toUpperCase(), txtX, txtY);
    //println("not wrap around");
    ImageCoor authorCoor = new ImageCoor(txtX, txtY - textSize, txtX + textWidth(s), txtY);
    TextBoundaryCoordinates.add(authorCoor);
  }

  //println("textWidth = "+textWidth(s));
  for(int i = 0; i < TextBoundaryCoordinates.size(); i++)
  {
    ImageCoor co = TextBoundaryCoordinates.get(i);
    //println("xMin = "  + co.x1 + " xMax = " + co.x2);
    //println("yMin = "  + co.y1+ " yMax = " + co.y2);
    //ellipse(co.x1, co.y1, 5, 5);
    //ellipse(co.x2, co.y2, 5, 5);
  }
}

public void horizontalParagraphByRules(String s)
{
  //println("\n==>Entering horizontalHeaderByRules, s = " + s);
  //println("width = "+width); //println("height = "+height);

  String[] words = s.split("\\s+");

  setValidTextPostionLowerBoundByRules(false, getLongestWordLength(words), textWidth(s), false);
  //setValidTextPostionUpperBoundByRules();

  noStroke();  // Ian tmp
  line(0, txtY, width, txtY);
  textAlign(BOTTOM);

  //println("I am printing paragragh: txtX = " +txtX+" txtY = " +txtY +" upperBoundX=" +upperBoundX+" 3*textSize =" + 6*textSize);
  stroke(headerColor);
  text(s, txtX, txtY, upperBoundX-txtX, 6*textSize);

  xMin = txtX;
  yMin = txtY;
  xMax = txtX + 8*getLongestWordLength(words);
  yMax = txtY + 6*textSize;

  
  //println("textWidth = "+textWidth(s));
  //println("xMin = "  + xMin + " xMax = " + xMax);
  //println("yMin = "  + yMin + " yMax = " + yMax);
  //ellipse(xMin, yMin, 5, 5);
  //ellipse(xMax, yMax, 5, 5);
}

public void vertTitleByRules(PFont fontOfStr)
{
  //println("\n==>Entering vertTitleByRules ");
  //println("width = "+width); //println("height = "+height);
  String[] words = input[2].split("\\s+");
  int factor = 1;
  int rand1 = (int)random(gridNumX - words.length * factor - factor);
  ////println(rand1);
  int spaceY = (int)gridNumY / 4;
  boolean withShape = random(1) > 0.5f;
  
  withShape = false;
  if(!withShape){
      textSize = gridY*1.5f ;
      vertHeaderByRules(input[2], true);
  }
  else
  {
    int numVertex = (int) random(3, 6);
    //println("textsize =" +textSize);
    for(int i = 0; i < words.length; i++)
    {
      float rand2 = (int)random(gridNumY-spaceY);
      txtX = ((int)(gridNumX-rand1)) * (gridX + betweenX);
      if(txtX > width) 
        txtX = width - random(80, 100);
      txtY = rand2 * (gridY + betweenY);
      if (txtY > height)
        txtY = height - random(80, 100);
      //println("txtX = " + txtX + " txtY = " +txtY);
    
      pushMatrix();
      translate(txtX, txtY);
      rotate(PI/2);
      stroke(titleColor);
      textWithShapes(fontOfStr, words[i].toUpperCase(), 0, 0, numVertex);
      popMatrix();
      rand1 += (int)random(gridNumX - rand1 - 1 -(words.length - i) * factor)+ factor;
    } 
  }
}


public void horizontalTitleByRules(PFont fontOfStr)
{
  //println("\n==> Entering horizontalTitleByRules()");
  String[] words = input[2].split("\\s+");
  boolean withShape = random(1) > 0.5f;
  withShape = false;
  if(!withShape){
      textSize = gridY*1.5f ;
      horizontalHeaderByRules(input[2], true);
  }
  else
  {
    int rand2 = (int)random(gridNumY - words.length  - 1) + 1;
    int spaceX = (int)gridNumX / 5;

    int numVertex = (int) random(4, 11);
    for(int i = 0; i < words.length; i++)
    {
      float rand1 = (int)random(gridNumX-spaceX);
      txtX = rand1 * (gridX + betweenX);
      txtY = rand2 * (gridY + betweenY);
    
      if(txtX > width) 
        txtX = width - random(80, 100);
      if (txtY > height)
        txtY = height - random(80, 100);
        
      textWithShapes(fontOfStr, words[i].toUpperCase(), txtX, txtY, numVertex);

      rand2 += (int)random(gridNumY - rand2 - (words.length - i)) + 1;
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "app" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
