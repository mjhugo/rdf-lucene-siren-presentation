package rdf.lucene.perf

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan
 */

import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory

/**
 * Adapted from Lucene in Action, Second Edition
 *
 * Utility class to get/refresh searchers when you are
 * using multiple threads.
 */
public class SearcherManager {

    private IndexSearcher currentSearcher
    private IndexWriter writer

    public SearcherManager(Directory dir) throws IOException {
        currentSearcher = new IndexSearcher(IndexReader.open(dir))
        warm(currentSearcher)
    }

    public SearcherManager(IndexWriter writer) throws IOException {
        this.writer = writer
        currentSearcher = new IndexSearcher(writer.reader)
        warm(currentSearcher)

        writer.mergedSegmentWarmer = new IndexWriter.IndexReaderWarmer() {
            public void warm(IndexReader reader) throws IOException {
                SearcherManager.this.warm(new IndexSearcher(reader))
            }
        }
    }

    public void warm(IndexSearcher searcher)
    throws IOException {}

    private boolean reopening

    private synchronized void startReopen()
    throws InterruptedException {
        while (reopening) {
            wait()
        }
        reopening = true
    }

    private synchronized void doneReopen() {
        reopening = false
        notifyAll()
    }

    public void maybeReopen()
    throws InterruptedException,
            IOException {

        startReopen()

        try {
            final IndexSearcher searcher = get()
            try {
                IndexReader newReader = currentSearcher.indexReader.reopen()
                if (newReader != currentSearcher.indexReader) {
                    IndexSearcher newSearcher = new IndexSearcher(newReader)
                    if (writer == null) {
                        warm(newSearcher)
                    }
                    swapSearcher(newSearcher)
                }
            } finally {
                release(searcher)
            }
        } finally {
            doneReopen()
        }
    }

    public synchronized IndexSearcher get() {
        currentSearcher.indexReader.incRef()
        return currentSearcher
    }

    public synchronized void release(IndexSearcher searcher)
    throws IOException {
        searcher.indexReader.decRef()
    }

    private synchronized void swapSearcher(IndexSearcher newSearcher)
    throws IOException {
        release(currentSearcher)
        currentSearcher = newSearcher
    }

    public void close() throws IOException {
        swapSearcher(null)
    }
}
