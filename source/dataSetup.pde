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
color backgroundColor;
color titleColor;
color titleBackColor;
color headerColor;
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

void setupColors()
{
  //println("===>Entering setupColors()");
  
  randomFont = random(1) > 0.5;
  randomFont = false;  //Ian: need to take this out
  
 // colorMode(HSB, 360, 100, 100);
colorMode(RGB, 255, 255, 255);
  getDominantColorOfImage();
  
  hueBackground = int(dominantImgHue + random(20, 340))%360;
  saturationBackground = int(dominantImgSaturation + random(10, 90))%100;
  brightnessBackground = int(dominantImgBrightness + random(10, 90))%100;
  
  backgroundColor = color(hueBackground, saturationBackground, brightnessBackground);
  titleColor = color(int(hueBackground + random(150, 210))%360, int(saturationBackground + random(30, 70))%100, int(brightnessBackground + random(30, 70))%100);
  headerColor = color(int((hueBackground + random(150, 210)) % 360), int(saturationBackground+ random(30, 70))%100, int((brightnessBackground + random(30, 70))%100));
}


// Get X, Y and text from input file
void setNums()
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


void setupHashset(){
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
void displayWelcomePage()
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

void displayNormalMenuPage()
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



void displayGoBackToHome(int h, int startingX)
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

void displayGoToMainMenu(int h)
{
  afterImageMsgFont = loadFont("HPSimplified-Bold-20.vlw");
  textFont(afterImageMsgFont);
  noFill();
  stroke(255);
  strokeWeight(2);

  rect(40, h-40, 410, 40, 2);
  text(gotoMainMenuString, 50, h-10);
}


void displayGotoDictionaryString()
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

void displayDictionaryPage()
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

void displayErrorPage()
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
