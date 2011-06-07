import org.openrdf.sail.nativerdf.NativeStore
import org.openrdf.repository.sail.SailRepository
import rdf.lucene.perf.SearcherManager
import org.apache.lucene.store.FSDirectory
import rdf.lucene.perf.LuceneController

beans = {
    nativeStore(NativeStore, new File('target/sesame-triplestore'), "spoc,posc,opsc,cspo")
    repository(SailRepository, ref('nativeStore')) { bean -> bean.initMethod = 'initialize' }

    luceneSearcherManager(SearcherManager, FSDirectory.open(new File(LuceneController.luceneIndexDir)))


}
