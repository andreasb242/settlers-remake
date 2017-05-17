/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.core.resources.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.android.core.resources.AndroidMapListFactory;
import jsettlers.main.android.core.resources.AndroidResourceProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import org.androidannotations.annotations.EBean;

@EBean
public class ResourcesLocationManager {
	private static final String ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY = "external-files-path";

	private final Context context;

	public ResourcesLocationManager(Context context) {
		this.context = context;
	}

	public boolean scanForResources() {
		File storage = Environment.getExternalStorageDirectory();
		File jSettlersDirectory = new File(storage, "JSettlers");
		ArrayList<File> files = new ArrayList<>();
		File outputDirectory = context.getExternalFilesDir(null); // <- output dir, always writable
		files.add(outputDirectory);
		files.add(jSettlersDirectory);
		files.add(storage);
		String path = PreferenceManager.getDefaultSharedPreferences(context).getString(ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY, "");
		if (!path.isEmpty()) {
			files.add(new File(path));
		}

		if (!hasImagesOnPath(files)) {
			return false;
		}

		for (File file : files) {
			ImageProvider.getInstance().addLookupPath(findDir(file, "Gfx"));
			SoundManager.addLookupPath(findDir(file, "Snd"));
		}
		MapList.setDefaultListFactory(new AndroidMapListFactory(context.getAssets(), files.get(0)));

		AndroidResourceProvider provider = new AndroidResourceProvider(context, outputDirectory);
		ResourceManager.setProvider(provider);
		return true;
	}

	public static boolean hasImagesOnPath(List<File> files) {
		boolean hasSnd = false;
		boolean hasGfx = false;
		for (File file : files) {
			File gfx = findDir(file, "Gfx");
			for (DatFileType t : DatFileType.values()) {
				hasGfx |= new File(gfx, "siedler3_00" + t.getFileSuffix()).exists();
			}
			File snd = findDir(file, "Snd");
			hasSnd |= new File(snd, "Siedler3_00.dat").exists();
		}
		return hasGfx && hasSnd;
	}

	@SuppressLint("DefaultLocale")
	private static File findDir(File file, String dirname) {
		File a = new File(file, dirname.toLowerCase());
		if (a.isDirectory()) {
			return a;
		}
		a = new File(file, dirname.toUpperCase());
		if (a.isDirectory()) {
			return a;
		}
		return a;
	}

	public void setResourcesDirectory(String path) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ORIGINAL_SETTLERS_FILES_PATH_SETTING_KEY, path).apply();
	}
}