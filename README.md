# Read below first:
# DisableLocalizedGhostClicking
Meant for people with touchscreens which click on their own (due to glitches or broken screen), but only in certain areas of the screen.

Sorry, I'm starting to think this program isn't very useful. The ghost clicks will still deselect the right click menu, making normal computer use difficult. I recommend calling your computer's sellers and asking if you have warranty to fix your screen. Maybe they can do it for free, but only within a few years of buying your computer.

My guess is this program only works on Windows.

To download from github:

(1) go to the page with this README file (you might be already on the right page)

(2) click "Download ZIP" (look to the right? Or above?)

(3) find the "DisableLocalizedGhostClicking.zip" file, and uncompress it. (On Windows, right click and select "Extract All...") There now should be a folder named "DisableLocalizedGhostClicking".

(4) delete the "DisableLocalizedGhostClicking.zip" file


To run it:

(1) Important: please save your work and close programs such as text editors before running. The program may crash your computer. 

(2) make sure Java is installed on your computer (one place to install: http://www.oracle.com/technetwork/java/javase/downloads/index.html)

(3) either

(3a)(1) run "run.bat" (and a popup should appear)

this may only work in Windows

(3) or

(3b)(1) open command prompt and enter the "bin" folder inside the "DisableLocalizedGhostClicking"

(3b)(2a) once in that folder, run "javaw DisableLocalizedGhostClicking".

(3b)(2b) if it says "'javaw' is not recognized", you have to type

"BlahBlah\Some Folder\The Folder Java is in\Java\blah1.2.3_45\bin\javaw.exe" DisableLocalizedGhostClicking.java

(3) or

(3c)(1) open command prompt and enter the "src" folder inside the "DisableLocalizedGhostClicking"

(3c)(2a) once in that folder, run "javac DisableLocalizedGhostClicking.java".

(3c)(2b) if it says "'javac' is not recognized", you have to type

"BlahBlah\Some Folder\The Folder Java is in\Java\jdk1.2.3_45\bin\javac.exe" DisableLocalizedGhostClicking.java

If you can't find any file named "javac.exe", download a jdk http://www.oracle.com/technetwork/java/javase/downloads/index.html

(3c)(3) run "javaw DisableLocalizedGhostClicking"

Again, if it says "'javaw' is not recognized", you have to manually type the location.

(3) Whether you use "\" or "/" depends on your operating system

(3) I prefer option (3c) because your computer might not allow (3a) or (3b).

You can email eztang00@gmail.com about any issues, especially cases where it crashes the computer.
