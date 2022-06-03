/*
Indexer Class definition
Usage Steps:
    Make a Java Maven Project
    Req Java 8+?

    Uses the file at project/data.json
    File should be in the project folder root
    Can change the name by changing the fileName var in main()

    Copy over Indexer.java or Searcher.java
    Put them in the project/src/test folder
    Run the Indexer.java


*/


package group.id3;

//source: https://www.tutorialspoint.com/lucene/lucene_first_application.htm#:~:text=First%20we%20need%20to%20create%20a%20package%20called,and%20other%20java%20classes%20under%20the%20com.tutorialspoint.lucene%20package.


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import java.nio.file.Path;
import java.util.Iterator; //json parsing





public class Indexer {
    private IndexWriter writer;
    //private String indexDir;
    private StandardAnalyzer analyzer; //tokenifies the document
    private Directory indexDirectory; //directory of the index
    private IndexSearcher isearcher; //
    private QueryBuilder builder;

    //constructor, assigns writer
    //  json file in project root ./Data.json
    //  index file in Index folder in project root
    public Indexer () throws IOException 
    {
        String workingDir = System.getProperty("user.dir");
        String indexDir = workingDir+"\\Index";
        //index directory, .open takes a 'Path type'
        Path path = (new File(indexDir)).toPath();
        indexDirectory = FSDirectory.open(path); 

        //create the indexer: writer    
        analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(indexDirectory,conf);

        try {
            //  json file in project root folder
            this.indexFile("Data.json");
            this.close(); 
            this.initializeSearcher();

            //parseJSON();
        } catch (Exception e) {
            
            e.printStackTrace();
            System.out.println("unable to read file, exiting...");
            System.exit(1);
        }

        
    }
    private void initializeSearcher() throws IOException
    {
        DirectoryReader ireader = DirectoryReader.open(indexDirectory);
        isearcher = new IndexSearcher(ireader);
        builder = new QueryBuilder(analyzer);

    }
    //destructor
    protected void finalize() throws CorruptIndexException, IOException 
    {
        
    }
    //closes the writer
    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    //Return a Document object for the indexer from the passed doc JSONObject
    //reference:
    //https://www.tutorialspoint.com/lucene/lucene_adddocument.htm
    public Document getDocument(JSONObject doc) {
        Document document = new Document();
  
        //index file contents
        //Field contentField = new TextField("contents", new FileReader(file));


        Field contentField = new TextField("content",  (String) doc.get("content"), Field.Store.YES); //don't store body in index
        Field titleField = new StringField("title",(String) doc.get("title"),Field.Store.YES);
        Field URLField = new StringField("url",(String) doc.get("url"),Field.Store.YES);
  
        document.add(contentField);
        document.add(titleField);
        document.add(URLField);

        //System.out.println(doc.get("title"));


        return document;
     }




    //Indexes a data file which is a JSONArray
    //Splits the array into objects
    //Converts objects into documents
    //Adds documents to the indexWriter
    private void indexFile(String fileName) throws IOException, ParseException {
        System.out.println("Indexing "+fileName);

        
        //Open the data file
        JSONArray ja = openJSON(fileName);
        if(ja == null) System.exit(1);
        
            
        
        //Iterate through each JSONObject in the JSONArray
        Iterator iteratorDocs = ja.iterator();
        
        while (iteratorDocs.hasNext())
        {
            JSONObject doc = (JSONObject) iteratorDocs.next();
            Document document = getDocument(doc);
            writer.addDocument(document); //add the document to the index
        }

        
        
     }
     public String search(String queryText) throws IOException
     {
         String defaultField = "content";
         return this.search(queryText,defaultField);
     }
     public String search(String queryText,String field) throws IOException
     {
        int numResults = 10;
        Query q = builder.createPhraseQuery(field, queryText);
        ScoreDoc[] hits = isearcher.search(q, numResults).scoreDocs;
        
        String returnedStr = "";
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            returnedStr += hitDoc.get("title") + "\n";
            //returnedStr += hitDoc.get("url") + "\n";

          }

        return returnedStr;
     }

     //
     //reference: https://www.geeksforgeeks.org/parse-json-java/
     //throws exception fixes error message
     public static JSONArray openJSON(String fileName) 
        throws IOException, ParseException {
            //get all files in the data directory
            String filePath = System.getProperty("user.dir") + "\\"+fileName;
            File file = new File(filePath);
            JSONArray ja = null;
            if(!file.isDirectory()
                  && !file.isHidden()
                  && file.exists()
                  && file.canRead()
                  //&& filter.accept(file) //import java.io.FileFilter;

               ){
                Object obj = new JSONParser().parse(new FileReader(fileName));
                ja = (JSONArray) obj;
               }
            else
            {
                System.out.println("Invalid filepath: "+filePath);
                System.exit(1);
            }

        return ja;

     }
      

     
    public static void main(String[] args) throws IOException {
        //System.out.println("Hello World");


        Indexer indexer = new Indexer();
        String search_results = indexer.search("Justin");
        System.out.println(search_results);




        //parse json file
        //make lucene thing
    }//
}
