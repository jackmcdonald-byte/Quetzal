# Quetzal
A Java-based UCI chess engine
## Description

Quetzal is a UCI compatible chess engine written in Java. The engine uses a neural network to evaluate the position of the chess board. The neural network is based on the NNUE (Efficiently Updatable Neural Network) architecture. The engine is capable of playing chess at a high level and can be used with any UCI compatible chess GUI.

I began this project out of an interest in the world of chess programming. Almost every modern chess engine is written in C/C++ with near perfect optimization which gives this field a very high barrier to entry. I wanted to create a chess engine that was written in a high-level language and was easy to understand, but still utilizes modern techniques and algorithms such as zobrist hashing, transposition tables, principle variation search, and more to play chess at a high level. 
I also wanted to experiment with neural networks and see how they could be used to evaluate chess positions.

## Features

Early stage - nothing is finalised yet!

## Installation

To use this engine, simply build the Quetzal.jar file using the provided source code. The engine requires a NNUE file to function. The NNUE file is a neural network file that is used to evaluate the position of the chess board. The NNUE file is provided in this repository. You can download other NNUE files from the Stockfish website. The file should have a .nnue extension and should be placed in the same directory as the Quetzal.jar file.

## Usage

Quetzal is a UCI compatible chess engine. It can be used with any UCI compatible chess GUI such as *Arena*.
Simply follow the GUI's instructions to load the engine and select the Quetzal.jar file as the engine executable.

Please ensure that the Quetzal.jar file is in the same directory as the NNUE file.

For more information on how to use the UCI Protocol, please refer to the UCI Protocol documentation.
https://backscattering.de/chess/uci/2006-04.txt

## Credits

NNUE Probe library - Developed by Daniel Shawul (https://github.com/dshawul)
- Since Java has no support for loading NNUEs whatsoever, I used Daniel Shawul's NNUE Probe library to load the NNUE file and evaluate the position of the chess board. The library is written in C/C++ and I used JNI to call the library from Java. I would write my own library for this, but I frankly don't have the time or expertise to do so when all NNUE documentation is exclusive to C/C++.

## Future Plans

After completing this project, I would later like to write Quetzal 2.0 in C/C++ to improve performance and to have more control over the engine. I would also like to experiment with training other neural networks and see how they can be used to evaluate chess positions.

---
