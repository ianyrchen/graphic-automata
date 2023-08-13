// used to record the boundary of Title and Author
ArrayList<ImageCoor> TextBoundaryCoordinates = new ArrayList<ImageCoor>();

void drawHeadersByRules()
{
  fill(headerColor);
  String fontStr;
  
  if(random(1) > 0.5)
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

      boolean vert = random(1) > 0.5;
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

      boolean vert = random(1) > 0.5;
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


void drawTitleByRules()
{
  fill(titleColor);
  
  String fontStr = "";
  
  if(isSerif == true && random(1) > 0.5)
    fontStr = generateFont("sansserif");
  else
    fontStr = generateFont("serif");
  //println(fontStr);
  PFont fontOfStr;
  
  float fontBig = random(33, 48);

  boolean vert = random(1) > 0.5;
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
void setValidTextPostionLowerBoundByRules(boolean capitalized, float longestWordLength, float stringLength, boolean isTitle)
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
      txtX = random(1, width * 0.8);   // Y is fixed but still need an init value
      txtY = random(textSize, height * 0.8);
      occupied = true;
    }
    
    //if(k<50)
      ////println("setValidTextPostionLowerBoundByRules(): txtX =" + txtX +" txtY =" +txtY );

    if(isTitle)
    {
      if(txtX + stringLength * 0.6  < width)
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
      txtY = random(textSize, height * 0.8); 
    txtX = random(1, width * 0.8);   // Y is fixed 
    ////println("generate a new txtX= "+ txtX + " txtY = " + txtY);
     
    ////println("Before last try the lower bound: txtX= "+ txtX + " txtY = " + txtY);
    if(k == 1999)
    {
      if(!isLastTry)  //still not find good values, set them to near origin
      {
        //println("HAVE TO Change Y value to get lower bound in setValidTextPostionLowerBoundByRules");
        txtY = random(1, height * 0.8);  // have to change Y value 
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


void setValidTextPostionVerticalOriginByRules(boolean capitalized, float longestWordLength, float stringLength, boolean isTitle)
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
        txtX = (coordinate.x + 2.5*textSize) < width ? coordinate.x : width - 2.5*textSize;  // try to spread out
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
      if(txtY + stringLength * 0.6  < height)
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
void vertHeaderByRules(String s, boolean isTitle)
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
    text(s.toUpperCase(), 0, 0, textWidth(s)*0.6, 2.5*textSize);
    ImageCoor titleCoor = new ImageCoor(txtX - 2.5*textSize, txtY, txtX,  txtY + textWidth(s)*0.6);
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

void vertParagraghByRules(String s)
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
void horizontalHeaderByRules(String s, boolean isTitle)
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
    text(s.toUpperCase(), txtX, txtY,  textWidth(s)*0.6, 2.5*textSize);
    ImageCoor titleCoor = new ImageCoor(txtX, txtY, txtX + textWidth(s)*0.6, txtY+2.5*textSize);
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

void horizontalParagraphByRules(String s)
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

void vertTitleByRules(PFont fontOfStr)
{
  //println("\n==>Entering vertTitleByRules ");
  //println("width = "+width); //println("height = "+height);
  String[] words = input[2].split("\\s+");
  int factor = 1;
  int rand1 = (int)random(gridNumX - words.length * factor - factor);
  ////println(rand1);
  int spaceY = (int)gridNumY / 4;
  boolean withShape = random(1) > 0.5;
  
  withShape = false;
  if(!withShape){
      textSize = gridY*1.5 ;
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


void horizontalTitleByRules(PFont fontOfStr)
{
  //println("\n==> Entering horizontalTitleByRules()");
  String[] words = input[2].split("\\s+");
  boolean withShape = random(1) > 0.5;
  withShape = false;
  if(!withShape){
      textSize = gridY*1.5 ;
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
