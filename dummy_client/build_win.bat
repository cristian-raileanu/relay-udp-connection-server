@echo off
dir /s /B *.java > sources.txt
javac -d ./target/ @sources.txt