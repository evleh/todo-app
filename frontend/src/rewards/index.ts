import type p5 from 'p5';
import { cloudSketch } from './cloud';
import { starSketch } from './star';

export type SketchFn = (p: p5, sketchApi: SketchApi) => void;

export interface SketchApi {
    onComplete: () => void; 
    getSize: () => {width: number; height: number}; 
}

export interface AnimationMeta {
  id: string;      // stable key — matches the backend's animationId in Phase 2
  name: string;    // display name shown in profile and modal header
  sketch: SketchFn;
  region: string; 
}

export const ANIMATIONS: Record<string, AnimationMeta> = {
  cloud: { id: 'cloud', name: 'Floating Cloud', sketch: cloudSketch, region: 'header' },
  star: { id: 'star', name: 'Floating Star', sketch: starSketch, region: 'header' },
};

export const ALL_ANIMATIONS = Object.values(ANIMATIONS);

export function randomAnimation(): AnimationMeta {
  //return ALL_ANIMATIONS[Math.floor(Math.random() * ALL_ANIMATIONS.length)]; 
  return ALL_ANIMATIONS[1]; // for now, only show the star, the cloud needs a face 
}

// Used in Phase 2 when the backend returns an earnedAnimationId
export function findAnimation(id: string): AnimationMeta | undefined {
  return ANIMATIONS[id];
}