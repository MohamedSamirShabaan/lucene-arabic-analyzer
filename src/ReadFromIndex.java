

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.github.msarhan.lucene.ArabicRootExtractorAnalyzer;

public class ReadFromIndex {
	
	private String INDEX_DIRECTORY;
	private String Q_Path ;
	private String targetDirectory;
	private int NumberOfHits;
	
	public ReadFromIndex(String INDEX_DIRECTORY , String Q_Path , String targetDirectory ,int NumberOfHits ) throws IOException {
		this.INDEX_DIRECTORY = INDEX_DIRECTORY;
		this.Q_Path = Q_Path;
		this.NumberOfHits = NumberOfHits;
		this.targetDirectory = targetDirectory;
		String Q = ReadQuestion();
		ReadRelatedQ(Q);
	}
	
	
	public void ReadRelatedQ(String Q) throws IOException {
		Directory index = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		Analyzer analyzer = new ArabicRootExtractorAnalyzer();
		
		Query query = null;
		try {
			query = new QueryParser("title", analyzer)
					.parse(Q);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IndexReader reader = DirectoryReader.open(index);
		System.out.println("Number Of Pairs in index file = " + reader.maxDoc());
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, NumberOfHits);

		ScoreDoc[] hits = docs.scoreDocs;
		
		int flag = 0;
		
		System.out.println("Found " + hits.length + " hits:");
		for (ScoreDoc hit : hits) {
			int docId = hit.doc;
			Document d = searcher.doc(docId);
			System.out.printf("\t(%s): %s\n", d.get("number"), d.get("title"));
			
			String QA = d.get("title");
			if (flag == 0) {
				writer_In_File(QA);
				flag = 1;
			}else {
			try {
				String lines[] = QA.split("\\r?\\n");
				lines[0] = lines[0] + "\n";
				lines[1] = lines[1] + "\n";
			    Files.write(Paths.get(targetDirectory + "questions.txt") , lines[0].getBytes(), StandardOpenOption.APPEND);
			    Files.write(Paths.get(targetDirectory + "answers.txt") , lines[1].getBytes(), StandardOpenOption.APPEND);
			}catch (IOException e) {
			    e.printStackTrace();
			}
			finally {
				
			}
		}
			}
		System.out.println("All hits Printed in target directory" + targetDirectory);
	}
	
	public void writer_In_File(String QA) {
		final String FILENAME_Q = targetDirectory+ "questions.txt";
		final String FILENAME_A = targetDirectory+ "answers.txt";
		
		BufferedWriter bw_Q = null;
		FileWriter fw_Q = null;
		
		BufferedWriter bw_A = null;
		FileWriter fw_A = null;

		// split on new lines
		String lines[] = QA.split("\\r?\\n");
		try {


			fw_Q = new FileWriter(FILENAME_Q);
			bw_Q = new BufferedWriter(fw_Q);
			bw_Q.write(lines[0]+"\n");
			
			
			fw_A = new FileWriter(FILENAME_A);
			bw_A = new BufferedWriter(fw_A);
			bw_A.write(lines[1]+"\n");
			

			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw_Q != null)
					bw_Q.close();
				if (fw_Q != null)
					fw_Q.close();
				if (bw_A != null)
					bw_A.close();
				if (fw_A != null)
					fw_A.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public String ReadQuestion() {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = null;
			FileReader fr = null;

			try {
				fr = new FileReader(Q_Path);
				br = new BufferedReader(fr);

				String sCurrentLine;
				
				while ((sCurrentLine = br.readLine()) != null) {
					System.out.println("Q_ReadFromFile is = " + sCurrentLine);
					sb.append(sCurrentLine);
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
			return sb.toString();
	}
}