import type p5 from 'p5';
import type { SketchApi } from './rewards';

export const cloudSketch = (p: p5, {onComplete, getSize}: SketchApi) => {
  let x1 = 180, x2 = 142, x3 = 170, x4 = 215;
  let y = 50; 

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
    p.clear(); 
    p.stroke(255, 255, 255);
    p.ellipse(x1, y + 15, 115, 50); // x, y, width, height
    p.circle(x2, y + 8, 50); // x, y, diameter
    p.circle(x3, y,  60);
    p.circle(x4, y + 8, 50);
  };
};