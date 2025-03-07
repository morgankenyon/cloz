# cloz

A simple webassembly interpreter written in clojure.

## How to Run

* clone repo
* Run `lein run <*.wasm>`
  * Pass in any wasm file located in the `./resource` folder

## Current Web Assembly Instruction Support

- [x] `end` - 11/0x0B
- [ ] `i32.eqz` - 69/0x45
- [ ] `i32.eq` - 70/0x46
- [ ] `i32.ne` - 71/0x47
- [x] `i32.lt_s` - 72/0x48
- [ ] `i32.lt_u` - 73/0x49
- [x] `i32.gt_s` - 74/0x40
- [ ] `i32.gt_u` - 75/0x4A
- [x] `i32.le_s` - 76/0x4B
- [ ] `i32.le_u` - 77/0x4C
- [x] `i32.ge_s` - 78/0x4D
- [ ] `i32.ge_u` - 79/0x4F
- [x] `i32.add` - 106/0x6A
- [x] `i32.sub` - 107/0x6B
- [x] `i32.mul` - 108/0x6C
- [x] `i32.div_s` - 109/0x6D
- [ ] `i32.div_u` - 110/0x6E
- [x] `i32.rem_s` - 111/0x6F
- [ ] `i32.rem_u` - 112/0x70
