PATH_TO_JFX = "dependencies/lib"

JC = javac
MODULES = ALL-MODULE-PATH
JFLAGS = --module-path $(PATH_TO_JFX) --add-modules $(MODULES) --add-exports javafx.graphics/com.sun.javafx.scene=com.jfoenix

all :
	@find -name "*.java" > sources.txt
	@echo "Compiling..."
	@javac $(JFLAGS) @sources.txt
	@echo "Success."
	@rm sources.txt;

run: 
	java $(JFLAGS) Deadwood

clean:
	@find . -type f -name '*.class' -exec rm {} +
	@echo "All clean. =D"