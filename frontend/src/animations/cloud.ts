import type p5 from 'p5';
import type { SketchApi } from '.';

export const cloudSketch = (p: p5, {onComplete, getSize}: SketchApi) => {
  let x1 = 180, x2 = 142, x3 = 170, x4 = 215;
  let y = 50; 
  let wraps = 0;
  const MAX_WRAPS = 4 *4;
  let done = false;  

  p.setup = () => {
    const {width, height} = getSize(); 
    p.createCanvas(width, height); 
  };

  p.windowResized = () => {
    const { width, height } = getSize();
    p.resizeCanvas(width, height);
  };

  p.draw = () => {
    if (done) return; 

    p.clear(); 
    p.stroke(255, 255, 255);
    p.ellipse(x1, y + 15, 115, 50); // x, y, width, height
    p.circle(x2, y + 8, 50); // x, y, diameter
    p.circle(x3, y,  60);
    p.circle(x4, y + 8, 50);

    
    x1 += 1; x1 += 1; x3 += 1; x4 += 1;
    for (const x of [x1, x2, x3, x4]) {
        if (x > p.width + 25) { 
            wraps++;
        }
    }
    if (x1 > p.width + 25) x1 = -25;
    if (x2 > p.width + 25) x2 = -25;
    if (x3 > p.width + 25) x3 = -25;
    if (x4 > p.width + 25) x4 = -25;

    if (wraps >= MAX_WRAPS){
        done = true; 
        onComplete(); 
    }
    
  };
};