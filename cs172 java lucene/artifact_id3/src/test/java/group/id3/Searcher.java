package group.id3;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.IOUtils; 
import org.apache.lucene.util.QueryBuilder;



//References:
//** 
// https://lucene.apache.org/core/9_1_0/core/index.html
// **


public class Searcher {

    //StandardQueryParser queryParser;
    
    private IndexSearcher isearcher; //
    private QueryBuilder builder;
    private Directory indexDirectory;
    private DirectoryReader ireader;


    public Searcher() throws IOException {
        String userDir = System.getProperty("user.dir");
        String indexDir = userDir + "\\Index";
        Path path = (new File(indexDir)).toPath();
        indexDirectory = FSDirectory.open(path);
        ireader = DirectoryReader.open(indexDirectory);

        isearcher = new IndexSearcher(ireader);
        StandardAnalyzer analyzer = new StandardAnalyzer();
        builder = new QueryBuilder(analyzer);
    }
    protected void finalize() throws CorruptIndexException, IOException 
    {
        ireader.close();
        indexDirectory.close();
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

    public static void main(String[] args) throws IOException {
        
        /* 
        String userDir = System.getProperty("user.dir");
        String indexDir = userDir + "\\Index";
        //Searcher searcher = new Searcher(indexDir);

        //Define IndexSearcher
        Path path = (new File(indexDir)).toPath();
        Directory indexDirectory = FSDirectory.open(path); 
        DirectoryReader ireader = DirectoryReader.open(indexDirectory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        
        //make the Query
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryBuilder builder = new QueryBuilder(analyzer); // builder that makes query objects

        String field = "content";
        String queryText = "Justin";
        Query q = builder.createPhraseQuery(field, queryText);

        //get Query results
        ScoreDoc[] hits = isearcher.search(q, 10).scoreDocs; //get query results, max 10
        //assertEquals(1, hits.length);
        System.out.println("hits length "+ hits.length);
        System.out.println("Search Results for: "+queryText);

        //Print Query Results
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            System.out.println(hitDoc.get("url"));
          }
          */

        //first Document content
        //Document firstDoc = isearcher.doc(hits[0].doc);
        //System.out.println("content of firstDoc"+firstDoc.get("content")+"\n");
        
        //Explanation exp = isearcher.explain(q,0);
        //System.out.println("explanation of firstDoc+n"+exp.toString());
        Searcher searcher = new Searcher();
        String results = searcher.search("Justin");
        System.out.println(results);

        //IOUtils.rm(path); //removes the entire index







        /*
        StandardQueryParser qpHelper = new StandardQueryParser();
        StandardQueryConfigHandler config =  qpHelper.getQueryConfigHandler();
        config.setAllowLeadingWildcard(true);
        config.setAnalyzer(new WhitespaceAnalyzer());
        Query query = qpHelper.parse("apache AND lucene", "defaultField");*/
    }
}
