const fs = require("fs");
const readline = require("readline");

const file = "./input.txt";

async function processLineByLine() {
  const fileStream = fs.createReadStream(file);

  const rl = readline.createInterface({
    input: fileStream,
    crlfDelay: Infinity,
  });
  const lines = [];
  for await (const line of rl) {
    lines.push(line);
  }
  return lines;
}

class Path {
  constructor(dir, parent) {
    this.dir = dir;
    this.parent = parent;
    this.files = [];
  }
}

function sumPath(leaf) {
  return leaf.files.reduce((sum, f) => {
    if (typeof f === "bigint") {
      return sum + f;
    } else {
      return sum + sumPath(f);
    }
  }, BigInt(0));
}

const head = new Path("/");
const dirs = [head];

async function go(maxSize = BigInt(100000)) {
  const results = await processLineByLine();
  let pointer = head;
  for (let i = 1; i < results.length; i++) {
    const scan = results[i];
    if (scan === "$ ls") {
      continue;
    } else if (scan.startsWith("$ cd")) {
      const [, , dir] = scan.split(" ");
      if (dir === "..") {
        pointer = pointer.parent;
      } else {
        pointer = pointer.files.find((f) => f?.dir === dir);
      }
    } else {
      const [left, right] = scan.split(" ");
      if (/\d+/.test(left)) {
        pointer.files.push(BigInt(left));
      } else {
        const newDir = new Path(right, pointer);
        dirs.push(newDir);
        pointer.files.push(newDir);
      }
    }
  }
  const folderSizes = dirs.map((d) => [d.dir, sumPath(d)]);
  const answer = folderSizes.reduce((sum, [, n]) => {
    if (n <= maxSize) {
      return sum + n;
    }
    return sum;
  }, BigInt(0));

  console.log("part 1", answer.toString());

  const targetToDelete =
    BigInt(30000000) - (BigInt(70000000) - folderSizes[0][1]);

  const vals = folderSizes
    .filter(([, s]) => s >= targetToDelete)
    .map(([, v]) => v)
    .sort((a, b) => {
      if (a > b) {
        return 1;
      } else if (a < b) {
        return -1;
      } else {
        return 0;
      }
    });

  console.log("part 2", vals[0].toString());
}

go();
