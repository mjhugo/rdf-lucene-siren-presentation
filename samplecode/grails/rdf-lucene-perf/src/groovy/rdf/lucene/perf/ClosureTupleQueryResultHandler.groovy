package rdf.lucene.perf


import org.openrdf.query.*

class ClosureTupleQueryResultHandler extends TupleQueryResultHandlerBase {

    private Closure closure
    private List<String> bindingNames

    public ClosureTupleQueryResultHandler(Closure closure) {
        this.closure = closure
    }

    public void startQueryResult(List<String> bindingNames) {
         this.bindingNames = bindingNames
    }

    public void handleSolution(BindingSet bindingSet) {
        Map tupleResult = [:]
        bindingNames.each {
            tupleResult[it] = bindingSet.getValue(it)  
        }

        closure(tupleResult)
    }
}