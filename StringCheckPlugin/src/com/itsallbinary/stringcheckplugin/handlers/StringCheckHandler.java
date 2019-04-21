package com.itsallbinary.stringcheckplugin.handlers;

import java.util.Scanner;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class StringCheckHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		String message = "";

		// Make sure current active editor is java editor.
		if (window.getActivePage().getActiveEditor() instanceof JavaEditor) {

			// Get file name from title of java editor & verify its java file.
			JavaEditor javaEditor = (JavaEditor) window.getActivePage().getActiveEditor();
			String fileName = javaEditor.getTitle();

			if (fileName.endsWith(".java")) {

				// Get java source code.
				IDocument doc = javaEditor.getDocumentProvider().getDocument(javaEditor.getEditorInput());
				String sourceCode = doc.get();
				IFile file = ((FileEditorInput) javaEditor.getEditorInput()).getFile();

				// Clear previous markers.
				try {
					for (IMarker m : ((IResource) file).findMarkers(IMarker.PROBLEM, true, 1)) {
						if (((String) m.getAttribute(IMarker.MESSAGE)).startsWith("StringCheck:")) {
							m.delete();
						}
					}
				} catch (CoreException e1) {
					e1.printStackTrace();
				}

				// Iterate line by line & look for wrong String check.
				boolean isWrongStringCheck = false;
				Scanner sc = new Scanner(sourceCode);
				int lineNr = 1;
				while (sc.hasNextLine()) {
					String line = sc.nextLine();

					// Look for empty string check usage using .equals("")
					Pattern emptyStringRegex = Pattern.compile(".*\\Q.equals(\"\")\\E", Pattern.MULTILINE);
					boolean isMatch = emptyStringRegex.matcher(line).find();

					if (isMatch) {
						isWrongStringCheck = true;

						// Set marker is wrong String check found
						try {
							IMarker marker = ((IResource) file).createMarker(IMarker.PROBLEM);
							marker.setAttribute(IMarker.LINE_NUMBER, lineNr);
							marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
							marker.setAttribute(IMarker.MESSAGE,
									"StringCheck: Improper String check. Please use String#isEmpty()");
							marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
						} catch (CoreException e) {
							message = "Oops ! Something went wrong !";
							e.printStackTrace();
						}
					}
					lineNr++;
				}

				// If improper check is present, show message in message box.
				message = isWrongStringCheck ? ("Improper String check. Please use String#isEmpty() ")
						: "Your code is good !";

			} else {
				message = "Not a java file";
			}
		} else {
			message = "Not a java file";
		}

		MessageDialog.openInformation(window.getShell(), "String check result", message);

		return null;
	}
}
