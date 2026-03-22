find -name "*.java" > sources.txt
javac -d ./target/ @sources.txt