# StringCheckEclipsePlugin
Eclipse plugin to scan java source to look for improper String checks. Example repo.

This code is part of tutorial http://itsallbinary.com/simple-useful-eclipse-plugin-to-scan-java-source-good-example-for-beginners/

Refer to this tutorial for detailed explination & walk through the code along with further instructions.

Plugin example in the article [http://itsallbinary.com/simple-useful-eclipse-plugin-to-scan-java-source-good-example-for-beginners/]
Add a menu item & toolbar button to eclipse.
On click of button/menu, plugin will scan Java source code & check if improper empty String checks are being done like string.equals(“”).
If improper checks found then show message dialog to use string.isEmpty() instead.
Mark the lines of code with improper checks using markers.
