import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.lang.*;
import processing.sound.*;

SoundFile song;

void settings(){
  setNums();
  size(int(xLen), int(yLen));
}

void setup(){
  displayWelcomePage();
  song = new SoundFile(this, "click.wav");
}

void draw()
{

}

boolean playing = true;
void keyPressed(){
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


void drawElements(boolean fragmented){
  drawImages(fragmented); 
  drawText();
  save(".\\data\\GeneratedImages\\generatedImage" + NumOfImagesGenerated +".jpg");
  NumOfImagesGenerated++;
}


void drawText(){
  drawTitle();  
  drawHeaders();
}

void drawTextByRules(){
  drawTitleByRules();  
  drawHeadersByRules();
}


void interactiveArt(){
  randomHundredObjects(); // for decotation purpose
}


// gridX, betweenX, betweenY and gridY are from input file
void drawGrid(){
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

void afterSavingRawImage()
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
