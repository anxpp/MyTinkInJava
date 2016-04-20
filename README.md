# MyTinkInJava

```java
package com.anxpp.thinkinjava.chapter11.sourse;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
/**
 * LinkedList底层使用双向链表，实现了List和deque。实现所有的可选List操作，并可以只有所有元素（包括空值）
 * 其大小理论上仅受内存大小的限制
 *
 * 所有的操作都可以作为一个双联列表来执行（及对双向链表操作）。
 * 把对链表的操作封装起来，并对外提供看起来是对普通列表操作的方法。
 * 遍历从起点、终点、或指定位置开始
 * 内部方法，注释会描述为节点的操作(如删除第一个节点)，公开的方法会描述为元素的操作(如删除第一个元素)
 *
 * LinkedList不是线程安全的，如果在多线程中使用（修改），需要在外部作同步处理。
 * 
 * 需要弄清元素（节点）的索引和位置的区别，不然有几个地方不好理解，具体在碰到的地方会解释。
 * 
 * 迭代器可以快速报错
 */
public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
	//容量
    transient int size = 0;
    //首节点
    transient Node<E> first;
    //尾节点
    transient Node<E> last;
    //默认构造函数
    public LinkedList() {
    }
    //通过一个集合初始化LinkedList，元素顺序有这个集合的迭代器返回顺序决定
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
    //使用对应参数作为第一个节点，内部使用
    private void linkFirst(E e) {
        final Node<E> f = first;//得到首节点
        final Node<E> newNode = new Node<>(null, e, f);//创建一个节点
        first = newNode;		//设置首节点
        if (f == null)
            last = newNode;     //如果之前首节点为空(size==0)，那么尾节点就是首节点
        else
            f.prev = newNode;   //如果之前首节点不为空，之前的首节点的前一个节点为当前首节点
        size++;					//长度+1
        modCount++;				//修改次数+1
    }
    //使用对应参数作为尾节点
    void linkLast(E e) {
        final Node<E> l = last;	//得到尾节点
        final Node<E> newNode = new Node<>(l, e, null);//使用参数创建一个节点
        last = newNode;			//设置尾节点
        if (l == null)
            first = newNode;	//如果之前尾节点为空(size==0)，首节点即尾节点
        else
            l.next = newNode;	//如果之前尾节点不为空，之前的尾节点的后一个就是当前的尾节点
        size++;
        modCount++;
    }
    //在指定节点前插入节点，节点succ不能为空
    void linkBefore(E e, Node<E> succ) {
        final Node<E> pred = succ.prev;//获取前一个节点
        final Node<E> newNode = new Node<>(pred, e, succ);//使用参数创建新的节点，向前指向前一个节点，向后指向当前节点
        succ.prev = newNode;//当前节点指向新的节点
        if (pred == null)
            first = newNode;//如果前一个节点为null，新的节点就是首节点
        else
            pred.next = newNode;//如果存在前节点，那么前节点的向后指向新节点
        size++;
        modCount++;
    }
    //删除首节点并返回删除前首节点的值，内部使用
    private E unlinkFirst(Node<E> f) {
        final E element = f.item;//获取首节点的值
        final Node<E> next = f.next;//得到下一个节点
        f.item = null;
        f.next = null; 		//便于垃圾回收期清理
        first = next;		//首节点的下一个节点成为新的首节点
        if (next == null)
            last = null;	//如果不存在下一个节点，则首尾都为null(空表)
        else
            next.prev = null;//如果存在下一个节点，那它向前指向null
        size--;
        modCount++;
        return element;
    }
    //删除尾节点并返回删除前尾节点的值，内部使用
    private E unlinkLast(Node<E> l) {
        final E element = l.item;//获取值
        final Node<E> prev = l.prev;//获取尾节点前一个节点
        l.item = null;
        l.prev = null; 		//便于垃圾回收期清理
        last = prev;		//前一个节点成为新的尾节点
        if (prev == null)
            first = null;	//如果前一个节点不存在，则首尾都为null(空表)
        else
            prev.next = null;//如果前一个节点存在，先后指向null
        size--;
        modCount++;
        return element;
    }
    //删除指定节点并返回被删除的元素值
    E unlink(Node<E> x) {
    	//获取当前值和前后节点
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;
        if (prev == null) {
            first = next;	//如果前一个节点为空(如当前节点为首节点)，后一个节点成为新的首节点
        } else {
            prev.next = next;//如果前一个节点不为空，那么他先后指向当前的下一个节点
            x.prev = null;	//方便gc回收
        }
        if (next == null) {
            last = prev;	//如果后一个节点为空(如当前节点为尾节点)，当前节点前一个成为新的尾节点
        } else {
            next.prev = prev;//如果后一个节点不为空，后一个节点向前指向当前的前一个节点
            x.next = null;	//方便gc回收
        }
        x.item = null;		//方便gc回收
        size--;
        modCount++;
        return element;
    }
    //获取第一个元素
    public E getFirst() {
        final Node<E> f = first;//得到首节点
        if (f == null)			//如果为空，抛出异常
            throw new NoSuchElementException();
        return f.item;
    }
    //获取最后一个元素
    public E getLast() {
        final Node<E> l = last;//得到尾节点
        if (l == null)			//如果为空，抛出异常
            throw new NoSuchElementException();
        return l.item;
    }
    //删除第一个元素并返回删除的元素
    public E removeFirst() {
        final Node<E> f = first;//得到第一个节点
        if (f == null)			//如果为空，抛出异常
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }
    //删除最后一个元素并返回删除的值
    public E removeLast() {
        final Node<E> l = last;//得到最后一个节点
        if (l == null)			//如果为空，抛出异常
            throw new NoSuchElementException();
        return unlinkLast(l);
    }
    //添加元素作为第一个元素
    public void addFirst(E e) {
        linkFirst(e);
    }
    //店家元素作为最后一个元素
    public void addLast(E e) {
        linkLast(e);
    }
    //检查是否包含某个元素，返回bool
    public boolean contains(Object o) {
        return indexOf(o) != -1;//返回指定元素的索引位置，不存在就返回-1，然后比较返回bool值
    }
    //返回列表长度
    public int size() {
        return size;
    }
    //添加一个元素，默认添加到末尾作为最后一个元素
    public boolean add(E e) {
        linkLast(e);
        return true;
    }
    //删除指定元素，默认从first节点开始，删除第一次出现的那个元素
    public boolean remove(Object o) {
    	//会根据是否为null分开处理。若值不是null，会用到对象的equals()方法
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    //添加指定集合的元素到列表，默认从最后开始添加
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);//size表示最后一个位置，可以理解为元素的位置分别为1~size
    }
    //从指定位置（而不是下标！下标即索引从0开始，位置可以看做从1开始，其实也是0）后面添加指定集合的元素到列表中，只要有至少一次添加就会返回true
    //index换成position应该会更好理解，所以也就是从索引为index(position)的元素的前面索引为index-1的后面添加！
    //当然位置可以为0啊，为0的时候就是从位置0(虽然它不存在)后面开始添加嘛，所以理所当前就是添加到第一个位置（位置1的前面）的前面了啊！
    //比如列表：0 1 2 3，如果此处index=4(实际索引为3)，就是在元素3后面添加；如果index=3(实际索引为2)，就在元素2后面添加。
    //原谅我的表达水平，我已经尽力解释了...
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);	//检查索引是否正确（0<=index<=size）
        Object[] a = c.toArray();	//得到元素数组
        int numNew = a.length;		//得到元素个数
        if (numNew == 0)			//若没有元素要添加，直接返回false
            return false;
        Node<E> pred, succ;
        if (index == size) {	//如果是在末尾开始添加，当前节点后一个节点初始化为null，前一个节点为尾节点
            succ = null;		//这里可以看做node(index)，不过index=size了（index最大只能是size-1），所以这里的succ只能=null，也方便后面判断
            pred = last;		//这里看做noede(index-1)，当然实现是不能这么写的，看做这样只是为了好理解，所以就是在node(index-1的后面开始添加元素)
        } else {				//如果不是从末尾开始添加，当前位置的节点为指定位置的节点，前一个节点为要添加的节点的前一个节点
            succ = node(index);	//添加好元素后(整个新加的)的后一个节点
            pred = succ.prev;	//这里依然是node(index-1)
        }
        //遍历数组并添加到列表中
        for (Object o : a) {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);//创建一个节点，向前指向上面得到的前节点
            if (pred == null)
                first = newNode;	//若果前节点为null，则新加的节点为首节点
            else
                pred.next = newNode;//如果存在前节点，前节点会向后指向新加的节点
            pred = newNode;			//新加的节点成为前一个节点
        }
        if (succ == null) {
        	//pred.next = null	//加上这句也可以更好的理解
            last = pred;		//如果是从最后开始添加的，则最后添加的节点成为尾节点
        } else {
            pred.next = succ;	//如果不是从最后开始添加的，则最后添加的节点向后指向之前得到的后续第一个节点
            succ.prev = pred;	//当前，后续的第一个节点也应改为向前指向最后一个添加的节点
        }
        size += numNew;
        modCount++;
        return true;
    }
    //清空表
    public void clear() {
    	//方便gc回收垃圾
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }
    //获取指定索引的节点的值
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
    //修改指定索引的值并返回之前的值
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }
    //指定位置后面（即索引为这个值的元素的前面）添加元素
    public void add(int index, E element) {
        checkPositionIndex(index);
        if (index == size)
            linkLast(element);	//如果指定位置为最后，则添加到链表最后
        else					//如果指定位置不是最后，则添加到指定位置前
            linkBefore(element, node(index));
    }
    //删除指定位置的元素，
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }
    //检查索引是否超出范围，因为元素索引是0~size-1的，所以index必须满足0<=index<size
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }
    //检查位置是否超出范围，index必须在index~size之间（含），如果超出，返回false
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
    //异常详情
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }
    //检查元素索引是否超出范围，若已超出，就抛出异常
    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    //检查位置是否超出范围，若已超出，就抛出异常
    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    //获取指定位置的节点
    Node<E> node(int index) {
    	//如果位置索引小于列表长度的一半(或一半减一)，从前面开始遍历；否则，从后面开始遍历
        if (index < (size >> 1)) {
            Node<E> x = first;//index==0时不会循环，直接返回first
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
    //获取指定元素从first开始的索引位置，不存在就返回-1
    //不能按条件双向找了，所以通常根据索引获得元素的速度比通过元素获得索引的速度快
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }
    //获取指定元素从first开始最后出现的索引，不存在就返回-1
    //但实际查找是从last开始的
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }
    //提供普通队列和双向队列的功能，当然，也可以实现栈，FIFO，FILO
    //出队（从前端），获得第一个元素，不存在会返回null，不会删除元素（节点）
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }
    //出队（从前端），不删除元素，若为null会抛出异常而不是返回null
    public E element() {
        return getFirst();
    }
    //出队（从前端），如果不存在会返回null，存在的话会返回值并移除这个元素（节点）
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
    //出队（从前端），如果不存在会抛出异常而不是返回null，存在的话会返回值并移除这个元素（节点）
    public E remove() {
        return removeFirst();
    }
    //入队（从后端），始终返回true
    public boolean offer(E e) {
        return add(e);
    }
    //入队（从前端），始终返回true
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }
    //入队（从后端），始终返回true
    public boolean offerLast(E e) {
        addLast(e);//linkLast(e)
        return true;
    }
    //出队（从前端），获得第一个元素，不存在会返回null，不会删除元素（节点）
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
     }
    //出队（从后端），获得最后一个元素，不存在会返回null，不会删除元素（节点）
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }
    //出队（从前端），获得第一个元素，不存在会返回null，会删除元素（节点）
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
    //出队（从后端），获得最后一个元素，不存在会返回null，会删除元素（节点）
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }
    //入栈，从前面添加
    public void push(E e) {
        addFirst(e);
    }
    //出栈，返回栈顶元素，从前面移除（会删除）
    public E pop() {
        return removeFirst();
    }
    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }
    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * Obeys the general contract of {@code List.listIterator(int)}.<p>
     *
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own {@code remove} or {@code add}
     * methods, the list-iterator will throw a
     * {@code ConcurrentModificationException}.  Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     *
     * @param index index of the first element to be returned from the
     *              list-iterator (by a call to {@code next})
     * @return a ListIterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }
    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;
        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }
        public boolean hasNext() {
            return nextIndex < size;
        }
        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();
            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }
        public boolean hasPrevious() {
            return nextIndex > 0;
        }
        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();
            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }
        public int nextIndex() {
            return nextIndex;
        }
        public int previousIndex() {
            return nextIndex - 1;
        }
        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();
            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }
        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }
        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
    //节点的数据结构，包含前后节点的引用和当前节点
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;
        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
    //返回迭代器
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }
    //因为采用链表实现，所以迭代器很简单
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }
    @SuppressWarnings("unchecked")
    private LinkedList<E> superClone() {
        try {
            return (LinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    /**
     * Returns a shallow copy of this {@code LinkedList}. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this {@code LinkedList} instance
     */
    public Object clone() {
        LinkedList<E> clone = superClone();
        // Put clone into "virgin" state
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;
        // Initialize clone with our elements
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);
        return clone;
    }
    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list
     *         in proper sequence
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }
    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     *
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to {@code null}.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        if (a.length > size)
            a[size] = null;
        return a;
    }
    private static final long serialVersionUID = 876323262645176354L;
    /**
     * Saves the state of this {@code LinkedList} instance to a stream
     * (that is, serializes it).
     *
     * @serialData The size of the list (the number of elements it
     *             contains) is emitted (int), followed by all of its
     *             elements (each an Object) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();
        // Write out size
        s.writeInt(size);
        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }
    /**
     * Reconstitutes this {@code LinkedList} instance from a stream
     * (that is, deserializes it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();
        // Read in size
        int size = s.readInt();
        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }
    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#ORDERED}.  Overriding implementations should document
     * the reporting of additional characteristic values.
     *
     * @implNote
     * The {@code Spliterator} additionally reports {@link Spliterator#SUBSIZED}
     * and implements {@code trySplit} to permit limited parallelism..
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new LLSpliterator<E>(this, -1, 0);
    }
    /** A customized variant of Spliterators.IteratorSpliterator */
    static final class LLSpliterator<E> implements Spliterator<E> {
        static final int BATCH_UNIT = 1 << 10;  // batch array size increment
        static final int MAX_BATCH = 1 << 25;  // max batch array size;
        final LinkedList<E> list; // null OK unless traversed
        Node<E> current;      // current node; null until initialized
        int est;              // size estimate; -1 until first needed
        int expectedModCount; // initialized when est set
        int batch;            // batch size for splits
        LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }
        final int getEst() {
            int s; // force initialization
            final LinkedList<E> lst;
            if ((s = est) < 0) {
                if ((lst = list) == null)
                    s = est = 0;
                else {
                    expectedModCount = lst.modCount;
                    current = lst.first;
                    s = est = lst.size;
                }
            }
            return s;
        }
        public long estimateSize() { return (long) getEst(); }
        public Spliterator<E> trySplit() {
            Node<E> p;
            int s = getEst();
            if (s > 1 && (p = current) != null) {
                int n = batch + BATCH_UNIT;
                if (n > s)
                    n = s;
                if (n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do { a[j++] = p.item; } while ((p = p.next) != null && j < n);
                current = p;
                batch = j;
                est = s - j;
                return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            }
            return null;
        }
        public void forEachRemaining(Consumer<? super E> action) {
            Node<E> p; int n;
            if (action == null) throw new NullPointerException();
            if ((n = getEst()) > 0 && (p = current) != null) {
                current = null;
                est = 0;
                do {
                    E e = p.item;
                    p = p.next;
                    action.accept(e);
                } while (p != null && --n > 0);
            }
            if (list.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
        public boolean tryAdvance(Consumer<? super E> action) {
            Node<E> p;
            if (action == null) throw new NullPointerException();
            if (getEst() > 0 && (p = current) != null) {
                --est;
                E e = p.item;
                current = p.next;
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
}
```
