# Mucken Core

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/zT-1337/mucken-core/blob/main/LICENSE.txt)
[![Language: Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Build Tool: Gradle](https://img.shields.io/badge/Build%20Tool-Gradle-02303A.svg)](https://gradle.org/)

## 🚀 Overview

`Mucken Core` serves as a core library for projects that want to implement the german card game 'Mucken'. 
This library only provides the core logic and data structures of the game itself, but without any other components, for example a UI or multiplayer.

## ✨ Key Features

* **Core Logic Implementation:** Contains the core game logic of the game.
* **Design based upon Commands and Events:** Everything that happens is triggered by a corresponding command and resulting in a list of events, that were triggered by the command.
* **Example Usage:** Includes a basic usage of the library for CLI based implementation of the game in the `cli-demo-example` folder.

## 🛠 Prerequisites

To build and run this project, you need:

* **Java Development Kit (JDK) 17+**
* **Git**

The project uses the Gradle wrapper (`gradlew`), so a separate Gradle installation is not required.

## ⚙️ Building the Project

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/zT-1337/mucken-core.git](https://github.com/zT-1337/mucken-core.git)
    cd mucken-core
    ```

2.  **Build the project:**
    * **On Linux/macOS:**
        ```bash
        ./gradlew build
        ```
    * **On Windows (Command Prompt/PowerShell):**
        ```bash
        .\gradlew.bat build
        ```

    This command will download dependencies, compile the source code, and generate the necessary JAR files in the `build/libs` directory.
