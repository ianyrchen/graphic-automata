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

void getDominantColorOfImage()
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
      color pixelColor = loadedImg.pixels[i];
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
  
  dominantImgHue = float(maxK.split(",")[0]);
  dominantImgSaturation = float(maxK.split(",")[1]);
  dominantImgBrightness = float(maxK.split(",")[2]);
  //println("dominantImgHue = " + dominantImgHue);
  //println("dominantImgSaturation = " + dominantImgSaturation);
  //println("dominantImgBrightness = " + dominantImgBrightness);
}

int GetLowerBoundBSValue(float bOrSValue)
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

int GetLowerBoundHueValue(float bOrSValue)
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
