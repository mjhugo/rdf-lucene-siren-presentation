import org.openrdf.sail.nativerdf.NativeStore
import org.openrdf.repository.sail.SailRepository
import rdf.lucene.perf.SearcherManager
import org.apache.lucene.store.FSDirectory
import rdf.lucene.perf.LuceneController
import rdf.lucene.perf.SirenController
import org.apache.lucene.index.IndexWriter
import org.sindice.siren.analysis.TupleAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.util.Version

beans = {
    nativeStore(NativeStore, new File('target/sesame-triplestore'), "spoc,posc,opsc,cspo")
    repository(SailRepository, ref('nativeStore')) { bean -> bean.initMethod = 'initialize' }

    //hack to initialize lucene dirs
    IndexWriter writer = new IndexWriter(org.apache.lucene.store.FSDirectory.open(new File(LuceneController.luceneIndexDir)),
            new StandardAnalyzer(Version.LUCENE_CURRENT),
            !(new File(LuceneController.luceneIndexDir).exists()), IndexWriter.MaxFieldLength.UNLIMITED)
    writer.close()

    writer = new IndexWriter(org.apache.lucene.store.FSDirectory.open(new File(SirenController.sirenIndexDir)),
            new TupleAnalyzer(new StandardAnalyzer(Version.LUCENE_CURRENT)),
            !(new File(SirenController.sirenIndexDir).exists()), IndexWriter.MaxFieldLength.UNLIMITED)
    writer.close()

    luceneSearcherManager(SearcherManager, FSDirectory.open(new File(LuceneController.luceneIndexDir)))
    sirenSearcherManager(SearcherManager, FSDirectory.open(new File(SirenController.sirenIndexDir)))
}
