package net.ysng.reader;

import java.io.File;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSortByName {   
	public List<File> getFile(List<File> files) {
		if (files != null) {
			ComparatorFile comparator = new ComparatorFile();
			Collections.sort(files, comparator);
		} else {
		}

		return files;
	}

	class ComparatorFile implements Comparator<File> {
		private Collator collator = Collator.getInstance();
		public ComparatorFile() {
		}
		public int compare(File file1, File file2) {
			CollationKey key1 = collator.getCollationKey(file1.getName().toString());
			CollationKey key2 = collator.getCollationKey(file2.getName().toString());
			return key1.compareTo(key2);
		}

	}
}
