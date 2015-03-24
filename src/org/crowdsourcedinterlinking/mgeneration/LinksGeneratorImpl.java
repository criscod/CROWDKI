package org.crowdsourcedinterlinking.mgeneration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import com.google.common.io.Files;

public abstract class LinksGeneratorImpl implements LinksGenerator{

	
	private Dataset dataset1; 
	
	public Dataset getDataset1() {
		return dataset1;
	}

	public void setDataset1(Dataset dataset1) {
		this.dataset1 = dataset1;
	}

	public Dataset getDataset2() {
		return dataset2;
	}

	public void setDataset2(Dataset dataset2) {
		this.dataset2 = dataset2;
	}

	private Dataset dataset2; 
	
	public abstract Interlinking generateLinks();
	
	public void registerDatasetsInTrackFile() throws IOException {
		File f = new File(ConfigurationManager.getInstance()
				.getCurrentTrackFile());

		String o1 = this.dataset1.getTitle() + ","
				+ this.dataset1.getLocation()+ ","
				+ this.dataset1.getTypeOfLocation().toString();

		Files.append(o1, f, Charset.defaultCharset());
		String ls = System.getProperty("line.separator");
		Files.append(ls, f, Charset.defaultCharset());

		String o2 = this.dataset2.getTitle() + ","
				+ this.dataset2.getLocation() + ","
				+ this.dataset2.getTypeOfLocation().toString();

		Files.append(o2, f, Charset.defaultCharset());
		Files.append(ls, f, Charset.defaultCharset());
	}
}
