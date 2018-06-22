

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.github.msarhan.lucene.ArabicRootExtractorAnalyzer;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;


public class Update_Index {
	private ArrayList<String> l = new ArrayList<>();
	private String INDEX_DIRECTORY;
	private String data_path ;
	private int first_time;
	private int id = 0;

	public Update_Index(String INDEX_DIRECTORY , String path , int flag) throws IOException {
		this.INDEX_DIRECTORY = INDEX_DIRECTORY;
		this.data_path = path;
		this.first_time = flag;
		System.out.println(INDEX_DIRECTORY);
		System.out.println(data_path);
		System.out.println(first_time);
		
		add_Doc();
	}
	
	private void add_Doc() throws IOException {
		Directory index = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		Analyzer analyzer = new ArabicRootExtractorAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(index, config);
		
		if (first_time != 1) {
			IndexReader reader = DirectoryReader.open(index);
			id = reader.maxDoc();
			id++;
		}
		
		
		 listFilesForFolder(new File (data_path) );
		
		for (int i = 0 ; i < l.size() ; i++) {
			System.out.println("File_Name_toAdd" + l.get(i));
			updat_index(writer , l.get(i));
		}
				
		writer.close();
	}
	
	
	private void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	           l.add(fileEntry.getName());
	        }
	    }
	}
	
	private void addToIndex(IndexWriter writer , String QA) {
		Document doc = new Document();
		doc.add(new StringField("number", Integer.toString(id++) , Field.Store.YES));
		doc.add(new TextField("title", QA , Field.Store.YES));
		System.out.println("Pair ID: "+(id)+"\nString: "+QA);
		
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("Execption will write in index" + e);
			e.printStackTrace();
		}
	}
	
	private void updat_index(IndexWriter writer , String file_name) {
			// TODO Auto-generated method stub
		final String FILENAME = data_path + "/" + file_name;
		BufferedReader br = null;
	    FileReader fr = null;

	    try {
	    	boolean empty;
	        fr = new FileReader(FILENAME);
	        br = new BufferedReader(fr);
	        String currentLine;
	        StringBuilder sb = new StringBuilder();
	    	while ((currentLine = br.readLine()) != null) {
	    		empty = currentLine.trim().isEmpty();
	    		while(empty) {
	    			//new need heeba to check it
	    			if( (currentLine = br.readLine()) != null) {
	            		empty = currentLine.trim().isEmpty();
	            	}else {
	            		return;}
	    		}
	    		while(!empty) {
	    			sb.append(currentLine);
	    			if( (currentLine = br.readLine()) != null) {
	            		empty = currentLine.trim().isEmpty();
	            	}else {
	            		addToIndex(writer , sb.toString());
	            		return;}
	    		}
	    		sb.append("\n");
	    		while(empty) {
	    			if( (currentLine = br.readLine()) != null) {
	            		empty = currentLine.trim().isEmpty();
	            	}else {
	            		addToIndex(writer , sb.toString());
	            		return;}
	    		}
	    		while(!empty) {
	    			sb.append(currentLine);
	    			if( (currentLine = br.readLine()) != null) {
	            		empty = currentLine.trim().isEmpty();
	            	}else {
	            		addToIndex(writer , sb.toString());
	            		return;}
	    		}
//	    		System.out.println(sb.toString());
	    		addToIndex(writer , sb.toString());
	    		sb = new StringBuilder();
	    	}
	    	if (sb.toString().length() != 0) {
	    		addToIndex(writer , sb.toString());
	    	}
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (br != null)
	                br.close();
	            if (fr != null)
	                fr.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	}
}