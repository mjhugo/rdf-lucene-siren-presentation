import org.openrdf.sail.nativerdf.NativeStore
import org.openrdf.repository.sail.SailRepository

beans = {
    nativeStore(NativeStore, new File('target/sesame-triplestore'), "spoc,posc,opsc,cspo")
    repository(SailRepository, ref('nativeStore')) { bean -> bean.initMethod = 'initialize' }

}
