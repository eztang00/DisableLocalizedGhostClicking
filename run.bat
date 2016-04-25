echo You may close this window
cd DisableLocalizedGhostClicking\bin && javaw DisableLocalizedGhostClicking
echo OFF
echo (I'm assuming you're using windows) If it says "'javaw' is not recognized", do the following. Double check you installed Java. Go to the Control Panel, type "System" in the search box, click "Edit the system environment variables", click "Environment Variables...", scroll until you see "Path", select it, click "Edit...". Click the leftmost of the Variable value textfield (make sure it's NOT highlighted--make sure you're adding text in front and you're NOT replacing text), type where javaw.exe is, and then type a ";" (EXAMPLE: "C:\Program Files\Java\jre1.8.0_45\bin;" yours will differ from the example). Finally click "OK", "OK", "OK".
pause
