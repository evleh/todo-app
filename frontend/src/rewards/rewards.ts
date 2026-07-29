import type p5 from 'p5';
import { cloudSketch } from './cloud';
import { starSketch } from './star';

export type SketchFn = (p: p5, sketchApi: SketchApi) => void;

export interface SketchApi {
    onComplete: () => void; 
    getSize: () => {width: number; height: number}; 
}

export interface RewardMeta {   
  sketch: SketchFn;
  region: string; 
}

export const ALL_REWARDS: RewardMeta[] = [
  { sketch: cloudSketch, region: 'header' },
  { sketch: starSketch, region: 'header' },
];

export function randomReward(): RewardMeta {
  //return ALL_REWARDS[Math.floor(Math.random() * ALL_REWARDS.length)]; 
  return ALL_REWARDS[1]; // for now, only show the star, the cloud needs a face 
}
