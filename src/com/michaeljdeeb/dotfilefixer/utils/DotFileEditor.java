/**
 * DotFileEditor.java
 * Purpose: Edits .dot files in the resources folder.
 *
 * @author Michael Deeb
 * @version 1.0 08/01/2013
 */
package com.michaeljdeeb.dotfilefixer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author michaeldeeb
 *
 */
public class DotFileEditor {
	// Path to the folder containing the .dot files.
	public static final String PATH_TO_DOTS = "./resources/";

	// Change this to the font you would like to use instead (if applicable).
	public static final String NEW_FONT = "HelveticaNeue-Light";

	// Change this to the font size you would like to use instead (if applicable).
	public static final String NEW_SIZE = "13";

	// Change these HTML colors to the background colors you would like to search for instead or add more.
	public static final String[] PROBLEMATIC_BACKGROUND_COLORS = {"#1b263f", "#27375f"};

	// Change this HTML color to be the font color you would like to use on the problematic backgrounds.
	public static final String NEW_COLOR = "#FFFFFF";
	
	// Settings
	public static final boolean CHANGE_FONT = true;
	public static final boolean CHANGE_SIZE = true;

	/**
	 * Returns an array of files with the extension dot
	 * @param folderPath Path to the folder containing the files to be modified.
	 * @return An array of files with the extension dot
	 */
	public static File[] getDotFiles(String folderPath) {
		File[] files = new File(folderPath).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".dot");
			}
		});
		return files;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		for(File file: getDotFiles(PATH_TO_DOTS)) {
			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;

			StringBuilder fileContent = new StringBuilder();

			while ((strLine = br.readLine()) != null) {
				// Check for fonts and change them to NEW_FONT
				if(CHANGE_FONT && strLine.contains("fontname=")) {
					Pattern pattern = Pattern.compile("fontname=\"(.+?)\"");
					Matcher matcher = pattern.matcher(strLine);
					matcher.find();
					String columnName = matcher.group(1);
					strLine = strLine.replace(columnName, NEW_FONT);
				}

				// Check for font sizes and change them to NEW_SIZE
				if(CHANGE_SIZE && strLine.contains("fontsize")) {
					Pattern pattern = Pattern.compile("fontsize=\"(.+?)\"");
					Matcher matcher = pattern.matcher(strLine);
					matcher.find();
					String columnName = matcher.group(1);
					strLine = strLine.replace(columnName, NEW_SIZE);
				}

				// Look for the offending background color and change the font of that tag to something readable.
				// In this case we're making the font color on dark backgrounds white.
				for(String bgColor: PROBLEMATIC_BACKGROUND_COLORS) {
					if(strLine.contains("BGCOLOR=\""+bgColor+"\"")) {
						Pattern pattern = Pattern.compile("\">(.+?)</TD>");
						Matcher matcher = pattern.matcher(strLine);
						matcher.find();
						String regexFound = matcher.group();
						String columnName = matcher.group(1);
						strLine = strLine.replace(regexFound, "\"><font color=\""+NEW_COLOR+"\">"+columnName+"</font></TD>");

					}
				}
				// Append changes to temporary space.
				fileContent.append(strLine);
	        	fileContent.append("\n");
			}
			// Write the file back out to the same spot.
			FileWriter fstreamWrite = new FileWriter(file);
        	BufferedWriter out = new BufferedWriter(fstreamWrite);
        	out.write(fileContent.toString());
        	out.close();
        	br.close();
        	
        	/* If using Linux or Windows you might be able to use a Graphviz Java API
        	 * http://www.loria.fr/~szathmar/off/projects/java/GraphVizAPI/index.php
        	 * to generate the new PNGs for SchemaSpy to use, but I had to write a shell script for OS X.
        	*/

		}
	}
}
