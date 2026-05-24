🎈 BalloonWala
A colourful sliding-tile puzzle game for kids aged 5 and up, built as a native Android app in Java.
Choose between the classic 8-puzzle (3×3 grid) or the more challenging 15-puzzle (4×4 grid). Slide the numbered tiles into the correct order to win — then enjoy the balloon shower celebration!

Features
Gameplay

Two puzzle modes — 8-puzzle (Easy ⭐) and 15-puzzle (Hard 🔥)
Every generated puzzle is mathematically guaranteed to be solvable
Live move counter and timer during play
Undo — step back through your moves one at a time
Best score and best time saved between sessions

Hint System

💡 Hint button — solver finds the optimal next move and shows it with:

A pulsing scale animation on the correct tile
An amber glow on the empty slot
A custom-drawn canvas arrow from tile center to empty slot center


🎯 Solution button — watch the full optimal solution auto-played step by step

Celebration

Balloon shower animation when the puzzle is solved
Win fanfare generated programmatically (no audio files needed)
"New Best!" indicator when a personal record is broken
Assisted solve tracked separately — best scores only recorded for unassisted wins

Sound

Tile tap sound on every move (programmatic sine wave)
Win fanfare melody — C–E–G–C ascending notes
Optional background music (add res/raw/background_music.ogg)
Sound on/off toggle on the home screen, persisted between sessions

Home Screen

Animated floating balloon
Mini grid preview on each puzzle button so kids understand the difficulty at a glance
No toolbar — full-screen, kid-friendly layout


Project Structure
com.example.balloonwala/
│
├── AppConstants.java          Puzzle mode sizes + SharedPreferences name
├── NavigationConstants.java   Intent extra keys for screen navigation
├── BalloonWalaApp.java        Application class — SoundHelper singleton
│
├── MainActivity.java          Home screen — mode selection + sound toggle
├── SplashActivity.java        2-second splash screen
├── PuzzleActivity.java        Game screen — orchestrates all helpers
│
├── helpers/
│   ├── AnimationHelper.java   120ms smooth tile slide with double-tap guard
│   ├── ButtonManager.java     All 16 tile buttons setup and lookup
│   ├── CelebrationHelper.java Balloon shower + win overlay
│   ├── GameState.java         Move counter + undo stack (ArrayDeque)
│   ├── GameTimer.java         Chronometer wrapper — excludes background time
│   ├── HintHelper.java        Pulse + glow + canvas arrow hint
│   ├── SolutionHelper.java    Auto-plays solution step by step
│   ├── SoundHelper.java       Sine-wave sounds + optional background music
│   ├── TileStyleHelper.java   Fixed color per tile number (pre-parsed)
│   └── UIHelper.java          Move display + confirmation dialogs
│
├── model/
│   └── Move.java              Single player move (from/to buttons)
│
├── solver/
│   ├── PuzzleState.java       Board state + Manhattan distance heuristic
│   └── PuzzleSolver.java      IDA* solver — optimal solution, 5s timeout
│
├── utils/
│   └── GenericUtils.java      Tile operations + board↔button conversion
│
└── views/
    └── HintArrowView.java     Custom Canvas view — draws the hint arrow

Architecture
PuzzleActivity is a pure orchestrator — it contains zero game logic itself. Every responsibility lives in a dedicated helper:
HelperResponsibilityButtonManagerRegisters all 16 tile buttons, fires OnTileClickListenerGameTimerTracks elapsed time, persists best time per modeGameStateTracks move count and undo stack, persists best moves per modeUIHelperUpdates move counter display and shows confirmation dialogsTileStyleHelperApplies fixed color to each tile number (1=red, 2=orange…)AnimationHelperAnimates tile slides, blocks input during animationSoundHelperPlays sounds and manages background music lifecycleHintHelperCoordinates pulse + glow + arrow for the hint featureSolutionHelperSequences auto-play of solver moves with delaysCelebrationHelperBalloon shower + win overlay with fade-inPuzzleSolverIDA* with Manhattan Distance — finds optimal solution

Solver
The hint and solution features use IDA* (Iterative Deepening A*) with the Manhattan Distance heuristic.

Admissible heuristic — guarantees the optimal (fewest moves) solution
Memory efficient — O(depth) space, suitable for a mobile device
5-second timeout prevents ANR on hard 15-puzzle positions
Runs on a background thread — UI never blocked
Board is captured as a snapshot before the solver starts, preventing corruption if the player moves tiles


Tile Colors
Each number always maps to the same color so kids can recognise pieces visually:
TileColor1🔴 Red #FF6B6B2🟠 Orange #FF9F433🟡 Yellow #F9CA244🟢 Green #6AB04C5🩷 Pink #FF9FF36🔵 Blue #54A0FF7🟣 Purple #5F27CD8🩵 Teal #00D2D39–15Mint, Rose, Indigo, Peach, Sky, Aqua, Amber

Setup
Prerequisites

Android Studio (latest stable)
Android SDK API 26+
Java 8

Required manifest change
Add to the <application> tag in AndroidManifest.xml:
xmlandroid:name=".BalloonWalaApp"
Optional background music

Create res/raw/ folder in Android Studio
Add a file named background_music.ogg (or .mp3)
The app works silently without it — SoundHelper skips gracefully if the file is missing

Free music sources:

mixkit.co/free-stock-music → filter by Children
pixabay.com/music → search "kids game"

Build and install
bash# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk

Resources
FolderContentsres/layout/activity_main.xmlHome screen layoutres/layout/puzzle.xmlGame screen layoutres/drawable/tile_shape.xmlRounded tile backgroundres/drawable/tile_empty_shape.xmlEmpty slot backgroundres/drawable/tile_hint_glow.xmlAmber glow for hint empty slotres/drawable/btn_rounded.xmlAction button shaperes/drawable/grid_cell.xmlMini grid preview filled cellres/drawable/grid_cell_empty.xmlMini grid preview empty cellres/values/colors.xmlAll named colors — change colorPrimary to rethemeres/values/strings.xmlAll UI strings

What's Persisted
All data is stored in SharedPreferences under the key BalloonWalaPrefs:
KeyWhatbest_time_8_puzzleBest completion time — 8-puzzle (ms)best_time_15_puzzleBest completion time — 15-puzzle (ms)best_moves_8_puzzleFewest moves to solve — 8-puzzlebest_moves_15_puzzleFewest moves to solve — 15-puzzlesound_enabledSound on/off preference
Best scores are only saved for unassisted wins — using Hint or Solution does not update records.

Future Ideas

Settings screen (tile size, animation speed)
High score leaderboard per mode
Difficulty selector (shuffle depth control)
Confetti particle system for the win celebration
Accessibility support (content descriptions for tiles)


Made with ❤️ as a gift for the kids.
