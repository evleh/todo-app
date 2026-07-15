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


function start() {
    cleanup();
    if (props.animation && containerRef.value) {
        const container = containerRef.value;
        p5Instance = new p5(
            (p) => props.animation!.sketch(p, {
                onComplete: handleComplete,
                getSize: () => ({ width: container.clientWidth, height: container.clientHeight }),
            }),
            container,
        );
    }
}

function handleComplete() {
    cleanup();
    emit('complete');
}

function cleanup() {
    p5Instance?.remove();
    p5Instance = null;
}

watch(() => props.active, (val) => { val ? start() : cleanup(); });
onBeforeUnmount(cleanup);
</script>

<style scoped></style>