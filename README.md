# cloz

A simple webassembly interpreter written in clojure.

## How to Run

* clone repo
* Run `lein run <*.wasm>`
  * Pass in any wasm file located in the `./resource` folder

## Current Web Assembly Instruction Support

- [x] `end` - 11/0x0B
- [x] `i32.add` - 106/0x6A
- [x] `i32.sub` - 107/0x6B
- [x] `i32.mul` - 108/0x6C
- [x] `i32.div_s` - 109/0x6D
- [ ] `i32.div_u` - 110/0x6E
- [x] `i32.rem_s` - 111/0x6F
- [ ] `i32.rem_u` - 112/0x70
