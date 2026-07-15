<template>
    <div ref="containerRef"
        style="position: absolute; inset: 0; z-index: 0; overflow: hidden; pointer-events: none;"
    ></div>
</template>

<script setup lang="ts">
import p5 from 'p5';
import type { AnimationMeta } from '../animations';
import { ref, watch, onBeforeUnmount } from 'vue';


const props = defineProps<{
    active: boolean;
    animation: AnimationMeta | null
}>();

const emit = defineEmits<{ complete: [] }>();

const containerRef = ref<HTMLDivElement | null>(null);

let p5Instance: p5 | null = null;

let animationCounter: number = 0; 

function start() {
    cleanup();
    if (props.animation && containerRef.value) {
        animationCounter++; 
        const currentCounter = animationCounter; 
        const container = containerRef.value;
        p5Instance = new p5(
            (p) => props.animation!.sketch(p, {
                onComplete: () => {
                    // check if onComplete function belongs to the animation it was called by (arrow function is in the same closure as currentCounter)
                    if(animationCounter === currentCounter){
                        cleanup();
                        emit('complete');
                    }   
                },
                getSize: () => ({ width: container.clientWidth, height: container.clientHeight }),
            }),
            container,
        );
    }
}

function cleanup() {
    p5Instance?.remove();
    p5Instance = null;
}

watch(() => props.active, (val) => { val ? start() : cleanup(); });
onBeforeUnmount(cleanup);
</script>

<style scoped></style>