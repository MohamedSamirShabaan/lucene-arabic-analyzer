

import java.io.IOException;

public class Index_Main {
	
	private static String INDEX_DIRECTORY;
	private static String data_path;
	private static String Q_Path ; 
	private static String targetDirectory;
	private static int first_time ;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
			// if want to update the index writer file
		  
		  int updatOrRead = Integer.parseInt(args[0]);
		  if (updatOrRead == 1){
			  
		  	INDEX_DIRECTORY = args[1];
		  	data_path = args[2];
		  	first_time = Integer.parseInt(args[3]);
		  	new Update_Index(INDEX_DIRECTORY , data_path , first_time);
		  	
		  }else {
		  		// if want to read from indexing
		  	INDEX_DIRECTORY = args[1];
		  	Q_Path = args[2];
		  	targetDirectory = args[3];
		  	int NumberOfHits = Integer.parseInt(args[4]);
		  	new ReadFromIndex(INDEX_DIRECTORY , Q_Path , targetDirectory , NumberOfHits);
		  }
	}
}