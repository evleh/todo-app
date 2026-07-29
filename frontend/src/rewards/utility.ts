
/**
 * Returns random integer in a range. Max-value inclusive. 
 * @param min 
 * @param max 
 * @returns 
 */
export function randomInt(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

