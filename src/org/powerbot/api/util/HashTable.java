package org.powerbot.api.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.powerbot.bot.client.Node;

public class HashTable<N> implements Iterator<N>, Iterable<N> {
	private final WeakReference<org.powerbot.bot.client.HashTable> table;
	private final Class<N> type;
	private int bucket_index = 0;
	private Node curr;
	private Node next;

	public HashTable(final org.powerbot.bot.client.HashTable table, final Class<N> type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		this.table = new WeakReference<org.powerbot.bot.client.HashTable>(table);
		this.type = type;
	}

	@Override
	public Iterator<N> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if (next != null) {
			return true;
		}
		final org.powerbot.bot.client.HashTable table = this.table.get();
		final Node[] buckets = table != null ? table.getBuckets() : null;
		if (buckets == null) {
			return false;
		}
		if (bucket_index > 0 && bucket_index <= buckets.length && buckets[bucket_index - 1] != curr) {
			next = curr;
			curr = curr.getNext();
			return true;
		}
		while (bucket_index < buckets.length) {
			final Node n = buckets[bucket_index++].getNext();
			if (buckets[bucket_index - 1] != n) {
				next = n;
				curr = n.getNext();
				return true;
			}
		}
		return false;
	}

	@Override
	public N next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final N n = type.cast(next);
		next = null;
		return n;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
