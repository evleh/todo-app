import type p5 from 'p5';
import type { SketchApi } from './rewards';
import { randomInt } from './random';

export const starSketch = (p: p5, {onComplete, getSize}: SketchApi) => {

  p.setup = () => {
    const {width, height} = getSize(); 
    p.createCanvas(width, height); 
    p.noLoop(); // draw is called once without looping  
    setTimeout(onComplete, 3000); 
  };


  p.windowResized = () => {
    const { width, height } = getSize();
    p.resizeCanvas(width, height);
  };

  p.draw = () => {

    p.fill(255, 204, 0);
    
    // Start drawing the star-shape.
    p.beginShape();
    // star points
    p.vertex(randomInt(20,30), randomInt(20,50)); // upper left peak
    p.vertex(randomInt(45,50), 40); 
    p.vertex(randomInt(55,60), randomInt(5,15)); // middle peak
    p.vertex(75, 40);
    p.vertex(randomInt(90,100), randomInt(20,50)); // upper right  peak
    p.vertex(80, 50);
    p.vertex(randomInt(75,100), randomInt(75,100)); // lower right peak
    p.vertex(60, 60);
    p.vertex(randomInt(20,50), randomInt(75,100)); // lower left peak 
    p.vertex(45, randomInt(45,60));
    // Stop drawing the star shape.
    p.endShape(p.CLOSE);

    
    // eyes
    p.strokeWeight(3);
    p.point(55, randomInt(35,40));
    p.point(65, randomInt(35,40));

    // mouth
    p.strokeWeight(2);
    p.noFill();
    // x, y, w, h ,start, stop (start stop= angles between which to draw the arc)
    p.arc(randomInt(59,63), randomInt(50,55), randomInt(5,10), randomInt(5,10), 0, p.PI); 
  };
};