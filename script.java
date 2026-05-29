// script.js
let selectedSize = 3;
let boardSize = 3;
let tiles = [];
let emptyIndex = 0;
let moveCount = 0;
let timer = 0;
let timerId = null;
let gameStarted = false;

const startScreen = document.getElementById("start-screen");
const gameScreen = document.getElementById("game-screen");
const resultScreen = document.getElementById("result-screen");

const board = document.getElementById("board");
const moveCountText = document.getElementById("move-count");
const timerText = document.getElementById("timer");

const finalTimeText = document.getElementById("final-time");
const finalMovesText = document.getElementById("final-moves");
const bestRecordText = document.getElementById("best-record");

const startBtn = document.getElementById("start-btn");
const shuffleBtn = document.getElementById("shuffle-btn");
const homeBtn = document.getElementById("home-btn");
const restartBtn = document.getElementById("restart-btn");
const resultHomeBtn = document.getElementById("result-home-btn");

const difficultyButtons = document.querySelectorAll(".difficulty-btn");

difficultyButtons.forEach(function(button) {
  button.addEventListener("click", function() {
    difficultyButtons.forEach(function(btn) {
      btn.classList.remove("selected");
    });

    button.classList.add("selected");
    selectedSize = Number(button.dataset.size);
  });
});

startBtn.addEventListener("click", function() {
  boardSize = selectedSize;
  showScreen(gameScreen);
  startGame();
});

shuffleBtn.addEventListener("click", function() {
  startGame();
});

homeBtn.addEventListener("click", function() {
  stopTimer();
  showScreen(startScreen);
});

restartBtn.addEventListener("click", function() {
  showScreen(gameScreen);
  startGame();
});

resultHomeBtn.addEventListener("click", function() {
  showScreen(startScreen);
});

function showScreen(screen) {
  startScreen.classList.remove("active");
  gameScreen.classList.remove("active");
  resultScreen.classList.remove("active");

  screen.classList.add("active");
}

function startGame() {
  stopTimer();

  moveCount = 0;
  timer = 0;
  gameStarted = false;

  moveCountText.textContent = moveCount;
  timerText.textContent = timer + "초";

  createTiles();
  shuffleTiles();
  drawBoard();

  gameStarted = true;
  startTimer();
}

function createTiles() {
  tiles = [];

  const totalTiles = boardSize * boardSize;

  for (let i = 1; i < totalTiles; i++) {
    tiles.push(i);
  }

  tiles.push("");
  emptyIndex = tiles.length - 1;
}

function shuffleTiles() {
  for (let i = 0; i < 200; i++) {
    const movableIndexes = getMovableIndexes();
    const randomIndex = Math.floor(Math.random() * movableIndexes.length);
    const selectedIndex = movableIndexes[randomIndex];

    swapTiles(selectedIndex, emptyIndex);
    emptyIndex = selectedIndex;
  }

  if (isSolved()) {
    shuffleTiles();
  }
}

function drawBoard() {
  board.innerHTML = "";
  board.style.gridTemplateColumns = "repeat(" + boardSize + ", 1fr)";
  board.style.gridTemplateRows = "repeat(" + boardSize + ", 1fr)";

  tiles.forEach(function(value, index) {
    const tile = document.createElement("button");

    if (value === "") {
      tile.className = "tile empty";
      tile.disabled = true;
    } else {
      tile.className = "tile";
      tile.textContent = value;

      tile.addEventListener("click", function() {
        moveTile(index);
      });
    }

    board.appendChild(tile);
  });
}

function moveTile(index) {
  if (!gameStarted) {
    return;
  }

  if (!canMove(index)) {
    return;
  }

  swapTiles(index, emptyIndex);
  emptyIndex = index;

  moveCount++;
  moveCountText.textContent = moveCount;

  drawBoard();

  if (isSolved()) {
    finishGame();
  }
}

function canMove(index) {
  const row = Math.floor(index / boardSize);
  const col = index % boardSize;

  const emptyRow = Math.floor(emptyIndex / boardSize);
  const emptyCol = emptyIndex % boardSize;

  const rowDiff = Math.abs(row - emptyRow);
  const colDiff = Math.abs(col - emptyCol);

  return rowDiff + colDiff === 1;
}

function getMovableIndexes() {
  const movableIndexes = [];

  for (let i = 0; i < tiles.length; i++) {
    if (canMove(i)) {
      movableIndexes.push(i);
    }
  }

  return movableIndexes;
}

function swapTiles(index1, index2) {
  const temp = tiles[index1];
  tiles[index1] = tiles[index2];
  tiles[index2] = temp;
}

function isSolved() {
  for (let i = 0; i < tiles.length - 1; i++) {
    if (tiles[i] !== i + 1) {
      return false;
    }
  }

  return tiles[tiles.length - 1] === "";
}

function startTimer() {
  timerId = setInterval(function() {
    timer++;
    timerText.textContent = timer + "초";
  }, 1000);
}

function stopTimer() {
  if (timerId !== null) {
    clearInterval(timerId);
    timerId = null;
  }
}

function finishGame() {
  gameStarted = false;
  stopTimer();

  finalTimeText.textContent = timer + "초";
  finalMovesText.textContent = moveCount;

  saveBestRecord();
  showBestRecord();

  showScreen(resultScreen);
}

function saveBestRecord() {
  const key = "bestRecord_" + boardSize;
  const currentRecord = {
    time: timer,
    moves: moveCount
  };

  const savedRecord = localStorage.getItem(key);

  if (savedRecord === null) {
    localStorage.setItem(key, JSON.stringify(currentRecord));
    return;
  }

  const bestRecord = JSON.parse(savedRecord);

  if (timer < bestRecord.time) {
    localStorage.setItem(key, JSON.stringify(currentRecord));
    return;
  }

  if (timer === bestRecord.time && moveCount < bestRecord.moves) {
    localStorage.setItem(key, JSON.stringify(currentRecord));
  }
}

function showBestRecord() {
  const key = "bestRecord_" + boardSize;
  const savedRecord = localStorage.getItem(key);

  if (savedRecord === null) {
    bestRecordText.textContent = "기록 없음";
    return;
  }

  const bestRecord = JSON.parse(savedRecord);
  bestRecordText.textContent = bestRecord.time + "초 / " + bestRecord.moves + "회";
}