# MCJE-PlayerAdvancementsMerge

## Overview
- This is a Java program for Minecraft: Java Edition that merges multiple player's advancements into single player's advancements.
- This program is useful when a player's account has changed and the UUID has changed, and the old advancements are no longer used.

## Disclaimer
- We are not responsible for any problems that may occur in your world after using this program.
- To avoid any potential issues, it is strongly recommended to create a backup of the target world before using the program.

## Warning
- The program is specifically designed for Minecraft: Java Edition.
- If you want to handle a multiplayer world, you need to know the old and current UUIDs of the target player. Please enter these information carefully.

## Requirements
- [Java](https://www.oracle.com/jp/java/technologies/downloads/) ([17](https://www.oracle.com/jp/java/technologies/downloads/#java17) was recommended)

## How to obtain the program
- Download JAR file from the release or clone the repository and build the artifact `MCJE-PlayerAdvancementsMerge` in IntelliJ IDEA.

## Usage
- It is recommended to back up the target world before proceeding.
- Enter `java -jar MCJE-PlayerAdvancementsMerge.jar -i <TARGET_ADVANCEMENTS_JSON_FILES> -o <OUTPUT_ADVANCEMENTS_JSON_FILE>` in the command line to merges statistics files.
> The TARGET_ADVANCEMENTS_JSON_FILES parameter refers to multiple JSON files with UUID names that are located in the "stats" folder of the target world. For single player worlds, you must specify all the files. However, for multiplayer worlds, you need to know both the old and current UUIDs of the target player and specify the corresponding files.

> The OUTPUT_ADVANCEMENTS_JSON_FILE parameter represents the JSON file where the merges advancements will be saved. This file should not exist prior to running the command.
- Rename the generated file to match the name of the advancements file most recently updated by the target player.
- If desired, you can delete the old advancements JSON files in the "advancements" folder of the target world.
- Open the world in Minecraft, verify that the advancements have been successfully merges, and you're good to go.