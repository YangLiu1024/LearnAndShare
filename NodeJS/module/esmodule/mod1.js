export const mod1Function = () => console.log('Mod1 is alive!')
export const mod1Function2 = () => console.log('Mod1 is rolling, baby!')

const sym = Symbol.for('test')

function sum(x, y) {
    return x + y;
}
//export default sym;


export default sum;

const multi = (x, y) => x * y
export {
    multi,
};