package de.joglearth.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;


public class PriorizedRunnableQueue implements BlockingQueue<Runnable>, Priorized {
    
    private class QueueEntry implements Comparable<QueueEntry>
    {
        public final long priority, sequenceNo;
        public final Runnable value; 
        
        public QueueEntry(Runnable value, long priority, long sequenceNo) {
            this.priority = priority;
            this.value = value;
            this.sequenceNo = sequenceNo;
        }
        
        @Override
        public int compareTo(QueueEntry other) {
            if (this.priority < other.priority) {
                return +1;
            } else if (this.priority > other.priority) {
                return -1;
            } else {
                if (this.sequenceNo > other.sequenceNo) {
                    return +1;
                } else if (this.sequenceNo < other.sequenceNo){
                    return -1;
                } else {
                    return 0; // Should not happen!
                }
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            QueueEntry other = (QueueEntry) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }

        private PriorizedRunnableQueue getOuterType() {
            return PriorizedRunnableQueue.this;
        }         
    }
    
    PriorityBlockingQueue<QueueEntry> queue = new PriorityBlockingQueue<>();
    private long currentPriority = 0;
    private long sequenceNo = 0;
    
        
    @Override
    public synchronized void increasePriority() {
        ++this.currentPriority;
        this.sequenceNo = 0;
    }
    
    
    public class QueueIterator implements java.util.Iterator<Runnable> {
        
        private Iterator<QueueEntry> queueIterator;

        QueueIterator(Iterator<QueueEntry> queueIterator) {
            this.queueIterator = queueIterator;
        }
        
        @Override
        public boolean hasNext() {
            return queueIterator.hasNext();
        }

        @Override
        public Runnable next() {
            QueueEntry element = queueIterator.next();
            return element != null ? element.value : null;
        }

        @Override
        public void remove() {
            queueIterator.remove();
        }
    }
    
    
    @Override
    public synchronized boolean offer(Runnable e) {
        return queue.offer(new QueueEntry(e, currentPriority, sequenceNo++));
    }

    @Override
    public synchronized boolean offer(Runnable e, long timeout, TimeUnit unit) 
            throws InterruptedException {
        return queue.offer(new QueueEntry(e, currentPriority, sequenceNo++), timeout, unit);
    }

    @Override
    public synchronized boolean add(Runnable e) {
        return queue.add(new QueueEntry(e, currentPriority, sequenceNo++));
    }

    @Override
    public synchronized void put(Runnable e) throws InterruptedException {
        queue.put(new QueueEntry(e, currentPriority, sequenceNo++));
    }

    @Override
    public Runnable element() {
        QueueEntry entry = queue.element();
        return entry != null ? entry.value : null;
    }

    @Override
    public Runnable peek() {
        QueueEntry entry = queue.peek();
        return entry != null ? entry.value : null;
    }

    @Override
    public Runnable poll() {
        QueueEntry entry = queue.poll();
        return entry != null ? entry.value : null;
    }

    @Override
    public Runnable remove() {
        QueueEntry entry = queue.remove();
        return entry != null ? entry.value : null;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends Runnable> collection) {
        ArrayList<QueueEntry> entryCollection = new ArrayList<>();
        for (Runnable Runnable : collection) {
            entryCollection.add(new QueueEntry(Runnable, currentPriority, sequenceNo++));
        }
        return queue.addAll(entryCollection);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public synchronized boolean containsAll(Collection<?> collection) {
        ArrayList<Object> entryCollection = new ArrayList<>();
        for (Object Runnable : collection) {
            entryCollection.add(new QueueEntry((Runnable) Runnable, currentPriority, sequenceNo++));
        }
        return queue.containsAll(entryCollection);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public Iterator<Runnable> iterator() {
        Iterator<QueueEntry> queueIterator = queue.iterator();
        return queueIterator != null ? new QueueIterator(queueIterator) : null;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> collection) {
        ArrayList<Object> entryCollection = new ArrayList<>();
        for (Object Runnable : collection) {
            entryCollection.add(new QueueEntry((Runnable) Runnable, currentPriority, sequenceNo++));
        }
        return queue.removeAll(entryCollection);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> collection) {
        ArrayList<Object> entryCollection = new ArrayList<>();
        for (Object Runnable : collection) {
            entryCollection.add(new QueueEntry((Runnable) Runnable, currentPriority, sequenceNo++));
        }
        return queue.removeAll(entryCollection);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public Object[] toArray() {
        Object[] entryObjects = queue.toArray();
        if (!(entryObjects instanceof QueueEntry[])) {
            return null;
        }
        
        QueueEntry[] entries = (QueueEntry[]) queue.toArray();
        Object[] values = new Object[entries.length];
        for (int i = 0; i < values.length; ++i) {
            values[i] = entries[i] != null ? entries[i].value : null;
        }
        return values;
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return null;
    }

    @Override
    public synchronized boolean contains(Object value) {
        if (!(value instanceof QueueEntry)) {
            return false;
        }
        return queue.contains(new QueueEntry((Runnable) value, currentPriority, sequenceNo++));
    }

    @Override
    public int drainTo(Collection<? super Runnable> collection) {
        ArrayList<QueueEntry> sink = new ArrayList<>();
        int result = queue.drainTo(sink);
        for (QueueEntry entry : sink) {
            collection.add(entry != null ? entry.value : null);
        }
        return result;
    }

    @Override
    public int drainTo(Collection<? super Runnable> collection, int n) {
        ArrayList<QueueEntry> sink = new ArrayList<>();
        int result = queue.drainTo(sink, n);
        for (QueueEntry entry : sink) {
            collection.add(entry != null ? entry.value : null);
        }
        return result;
    }

    @Override
    public Runnable poll(long arg0, TimeUnit arg1) throws InterruptedException {
        QueueEntry entry = queue.poll(arg0, arg1);
        return entry != null ? entry.value : null;
    }

    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    @Override
    public boolean remove(Object arg0) {
        if (!(arg0 instanceof QueueEntry[])) {
            return false;
        }
        return queue.remove(new QueueEntry((Runnable) arg0, 0, 0));
    }

    @Override
    public Runnable take() throws InterruptedException {
        QueueEntry entry = queue.take();
        return entry != null ? entry.value : null;
    }
}

