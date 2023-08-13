static int NumOfImagesGenerated = 0;
GridCoordinates CurrentCoordinates;
Rules CurrentRule;
int TOTALIMAGES = 8;
int CHOSENIMAGES = 1; // choose at most 1 images from 7 images for now
ArrayList<ImageCoor> ImgBoundaryCoordinates = new ArrayList<ImageCoor>();
ArrayList<Integer> ImgNumbers = new ArrayList<Integer>();

void getImageNumbers()
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

void setCurrentRule(boolean usePassedRule, Rules rule)
{
  if(!usePassedRule)
  {
    float r = random(1);
  
    CurrentRule = Rules.RULEOFTHIRDS;
    if(r >= 2.0/3.0)
      CurrentRule = Rules.RULEOFTHIRDS;
    if(r <= 1.0/3.0)
      CurrentRule = Rules.GOLDENMEAN;
    if(r > 1.0/3.0 && r < 2.0/3.0)
      CurrentRule = Rules.GOLDENTRIANGLE;
  }
  else
    CurrentRule = rule;
}

void generateImageByRules()
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

            if(xy >=0.5) // y value is not changed: horizontal line
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
    color testColor = color(dominantImgHue, dominantImgSaturation, dominantImgBrightness);
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

void resetCoordinatesOccupancy()
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
